<template>
  <div class="route-block" @click="expand" @mouseenter="showHover = true" @mouseleave="showHover = false">
    <div ref="mapContainer" class="map-container"></div>
    <div v-if="showHover" class="hover-overlay">
      Klik om de kaart te vergroten
    </div>
    <Teleport to="body" v-if="isExpanded">
      <div class="expanded-overlay" @click.stop>
        <div class="expanded-content">
          <button class="close-btn" @click="close">✕</button>
          <div ref="expandedMapContainer" class="expanded-map-container"></div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue';
import mapboxgl from 'mapbox-gl';
import pointsData from '../../data/points.json';
import connectionsData from '../../data/connections.json';

const { start, end } = defineProps({
  start: { type: String, required: true },
  end: { type: String, required: true }
});

const showHover = ref(false);
const isExpanded = ref(false);
const mapContainer = ref(null);
const expandedMapContainer = ref(null);
let map = null;
let expandedMap = null;
let animationFrameId = null;

const MAPBOX_TOKEN = import.meta.env.VITE_MAPBOX_TOKEN;

// Coördinaten van de Corda Campus polygoon voor het masker
const cordaCampusPolygon = [
  [5.34974351102025, 50.954003631664506],
  [5.348864605654882, 50.95279154169182],
  [5.348956377767536, 50.95204941181126],
  [5.349461147753971, 50.95136958933156],
  [5.350808640486889, 50.95101389971214],
  [5.352417197412478, 50.950674491827925],
  [5.353369556195219, 50.950659735794574],
  [5.3548163247484695, 50.95090665983483],
  [5.355409378936429, 50.95125805282487],
  [5.355279731003151, 50.95160823323323],
  [5.357144601408123, 50.952032684010106],
  [5.35814714118689, 50.9526164185244],
  [5.357807292212357, 50.95367102779247],
  [5.35670522284687, 50.95443785064219],
  [5.355427064219185, 50.954827082730645],
  [5.354360192807036, 50.95510624735297],
  [5.352373381189494, 50.95481417348341],
  [5.3505138421481035, 50.95454570264934],
  [5.34974351102025, 50.954003631664506],
];

// GeoJSON voor het masker rond Corda Campus
const maskGeoJSON = {
  type: 'Feature',
  geometry: {
    type: 'Polygon',
    coordinates: [
      [[-180, -90], [180, -90], [180, 90], [-180, 90], [-180, -90]],
      cordaCampusPolygon,
    ],
  },
};

// Bereken het centrum van de polygoon
const center = [
  cordaCampusPolygon.reduce((sum, coord) => sum + coord[0], 0) / cordaCampusPolygon.length,
  cordaCampusPolygon.reduce((sum, coord) => sum + coord[1], 0) / cordaCampusPolygon.length,
];

// Bouw de verbindingsmatrix met afstanden
const graph = {};
pointsData.features.forEach(point => {
  graph[point.properties.name] = {};
});
connectionsData.forEach(conn => {
  const from = conn.from;
  conn.to.forEach(to => {
    const fromPoint = pointsData.features.find(p => p.properties.name === from);
    const toPoint = pointsData.features.find(p => p.properties.name === to);
    if (fromPoint && toPoint) {
      const distance = haversineDistance(fromPoint.geometry.coordinates, toPoint.geometry.coordinates);
      graph[from][to] = distance;
      graph[to][from] = distance;
    }
  });
});

// Bereken de Haversine-afstand tussen twee coördinaten in meters
function haversineDistance(coord1, coord2) {
  const R = 6371e3;
  const lat1 = coord1[1] * Math.PI / 180;
  const lat2 = coord2[1] * Math.PI / 180;
  const deltaLat = (coord2[1] - coord1[1]) * Math.PI / 180;
  const deltaLon = (coord2[0] - coord1[0]) * Math.PI / 180;
  const a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
            Math.cos(lat1) * Math.cos(lat2) *
            Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return R * c;
}

function dijkstra(start, end) {
  console.log(`Berekenen pad van ${start} naar ${end}`);

  // afstanden initialiseren
  const distances = {};
  const previous = {};
  const unvisited = new Set(Object.keys(graph));

  Object.keys(graph).forEach(node => {
    distances[node] = Infinity;
    previous[node] = null;
  });
  distances[start] = 0;

  while (unvisited.size > 0) {
    const current = [...unvisited].reduce((minNode, node) =>
      distances[node] < distances[minNode] ? node : minNode
    );

    console.log(`Naar ${current}, afstand: ${distances[current]}`);

    if (current === end) break;

    unvisited.delete(current);

    Object.keys(graph[current]).forEach(neighbor => {
      const alt = distances[current] + graph[current][neighbor];
      if (alt < distances[neighbor]) {
        console.log(`${neighbor} updaten: nieuwe afstand ${alt} (was ${distances[neighbor]})`);
        distances[neighbor] = alt;
        previous[neighbor] = current;
      }
    });
  }

  // pad maken
  const path = [];
  let current = end;
  while (current) {
    path.unshift(current);
    current = previous[current];
  }

  console.log('pad: ', path);
  return {
    path: distances[end] === Infinity ? [] : path,
    distance: distances[end] === Infinity ? null : distances[end]
  };
}

function findBestEntrance(startBuilding, endBuilding) {
  console.log('Start berekening route tussen:', startBuilding, 'en', endBuilding);

  const normalizeName = (name) => {
    const lowerName = name.toLowerCase().trim();
    if (lowerName === "corda arena") return "corda arena";
    if (lowerName === "cordaat") return "Cordaat";
    if (lowerName === "corda bar") return "CBAR";
    const numberMatch = lowerName.match(/corda (\d+)/);
    if (numberMatch) return `C${numberMatch[1]}`;
    return name;
  };

  const normalizedStart = normalizeName(startBuilding);
  const normalizedEnd = normalizeName(endBuilding);

  console.log('Genormaliseerde namen:', { normalizedStart, normalizedEnd });

  const getEntrancesForBuilding = (building) => {
    console.log('Zoeken naar dichtsbijzijnde ingang:', building);
    if (building === "CBAR") {
      const entries = pointsData.features
        .filter(p => ["CBARI1", "CBARI2"].includes(p.properties.name))
        .map(p => p.properties.name);
      console.log('Corda Bar ingangen:', entries);
      return entries;
    } else if (building === "Cordaat") {
      const entries = pointsData.features
        .filter(p => p.properties.name === "Cordaat")
        .map(p => p.properties.name);
      console.log('Cordaat ingang:', entries);
      return entries;
    } else if (building === "corda arena") {
      return pointsData.features
        .filter(p => p.properties.name.toLowerCase() === "corda arena")
        .map(p => p.properties.name);
    } else {
      // Voor gewone Corda gebouwen (C1, C2, etc.)
      return pointsData.features
        .filter(p => p.properties.name.startsWith(building + "I"))
        .map(p => p.properties.name);
    }
  };

  const startEntrances = getEntrancesForBuilding(normalizedStart);
  const endEntrances = getEntrancesForBuilding(normalizedEnd);

  console.log('Gevonden ingangen:', { startEntrances, endEntrances });

  if (!startEntrances.length || !endEntrances.length) {
    console.error('Geen ingangen gevonden voor:', normalizedStart, normalizedEnd,
                 'Beschikbare punten:', pointsData.features.map(p => p.properties.name));
    return { startEntrance: null, endEntrance: null, path: [] };
  }

  let bestPath = { distance: Infinity, path: [], startEntrance: null, endEntrance: null };

  startEntrances.forEach(start => {
    endEntrances.forEach(end => {
      console.log(`Proberen pad van ${start} naar ${end}`);
      const result = dijkstra(start, end);
      console.log(`Resultaat:`, result);
      if (result.distance !== null && result.distance < bestPath.distance) {
        bestPath = {
          distance: result.distance,
          path: result.path,
          startEntrance: start,
          endEntrance: end,
        };
      }
    });
  });

  if (bestPath.distance === Infinity) {
    console.error('Geen geldig pad gevonden tussen:', normalizedStart, normalizedEnd);
    return { startEntrance: null, endEntrance: null, path: [] };
  }

  console.log('Beste pad:', bestPath);
  return bestPath;
}

// Teken de route op de kaart
function calculateAndDrawPath(start, end, targetMap) {
  const { startEntrance, endEntrance, path } = findBestEntrance(start, end);

  if (!startEntrance || !endEntrance || !path.length) {
    console.error('Kan geen route tekenen: ongeldige ingangen of pad.');
    return;
  }

  const pathCoords = path
  .slice() // kopie maken zodat je het origineel niet muteert
  .reverse() // de volgorde omdraaien: van end naar start --> betere animatie tussen start- en eindpunt
  .map(node => {
    const point = pointsData.features.find(p => p.properties.name === node);
    if (!point) {
      console.error('Geen coördinaten gevonden voor node:', node);
      return null;
    }
    return point.geometry.coordinates;
  })
  .filter(coord => coord !== null);

  if (pathCoords.length < 2) {
    console.error('Onvoldoende geldige coördinaten voor route.');
    return;
  }

  if (targetMap.getSource('path')) {
    targetMap.getSource('path').setData({
      type: 'Feature',
      geometry: { type: 'LineString', coordinates: pathCoords },
    });
  } else {
    targetMap.addSource('path', {
      type: 'geojson',
      data: { type: 'Feature', geometry: { type: 'LineString', coordinates: pathCoords } },
    });
    targetMap.addLayer({
      id: 'path-layer',
      type: 'line',
      source: 'path',
      paint: {
        'line-color': '#007cbf',
        'line-width': 3,
        'line-dasharray': [0, 4, 3],
      },
    });
  }

  if (targetMap.getSource('start')) {
    targetMap.getSource('start').setData({
      type: 'Feature',
      geometry: { type: 'Point', coordinates: pathCoords[0] },
    });
  } else {
    targetMap.addSource('start', {
      type: 'geojson',
      data: { type: 'Feature', geometry: { type: 'Point', coordinates: pathCoords[0] } },
    });
    targetMap.addLayer({
      id: 'start-layer',
      type: 'circle',
      source: 'start',
      paint: { 'circle-radius': 8, 'circle-color': '#FF0000' },
    });
  }

  if (targetMap.getSource('end')) {
    targetMap.getSource('end').setData({
      type: 'Feature',
      geometry: { type: 'Point', coordinates: pathCoords[pathCoords.length - 1] },
    });
  } else {
    targetMap.addSource('end', {
      type: 'geojson',
      data: { type: 'Feature', geometry: { type: 'Point', coordinates: pathCoords[pathCoords.length - 1] } },
    });
    targetMap.addLayer({
      id: 'end-layer',
      type: 'circle',
      source: 'end',
      paint: { 'circle-radius': 8, 'circle-color': '#00FF00' },
    });
  }

  const bounds = pathCoords.reduce((bounds, coord) => {
    return bounds.extend(coord);
  }, new mapboxgl.LngLatBounds(pathCoords[0], pathCoords[0]));
  targetMap.fitBounds(bounds, { padding: 50, maxZoom: 17 });
}

// Animatie stippellijn
function startPathAnimation(targetMap) {
  const waitForLayer = () => {
    if (!targetMap.isStyleLoaded() || !targetMap.getSource('path') || !targetMap.getLayer('path-layer')) {
      setTimeout(waitForLayer, 100);
      return;
    }

    if (animationFrameId) cancelAnimationFrame(animationFrameId);

    let dashOffset = 0;
    const dashCycle = 7;
    const animateDash = () => {
      dashOffset = (dashOffset + 0.05) % dashCycle;
      const dashArray = [
        0,
        Math.max(0, 4 - dashOffset),
        Math.min(7, 3 + dashOffset),
      ];
      if (targetMap.style && targetMap.getLayer('path-layer')) {
        targetMap.setPaintProperty('path-layer', 'line-dasharray', dashArray);
      }
      animationFrameId = requestAnimationFrame(animateDash);
    };
    animateDash();
  };

  waitForLayer();
}

// kaart initialiseren
function initializeMap(container, start, end, isExpanded = false) {
  if (!MAPBOX_TOKEN) {
    console.error('Mapbox token niet gevonden.');
    return null;
  }

  if (!container) {
    console.error('Map container is niet beschikbaar.');
    return null;
  }

  mapboxgl.accessToken = MAPBOX_TOKEN;

  const newMap = new mapboxgl.Map({
    container: container,
    style: 'mapbox://styles/mapbox/streets-v11',
    center: center,
    zoom: 15,
    pitch: 40,
    bearing: 20,
  });

  newMap.on('style.load', () => {
    const layers = newMap.getStyle().layers;
    const labelLayerId = layers.find(layer => layer.type === 'symbol' && layer.layout['text-field'])?.id;

    newMap.addLayer({
      id: '3d-buildings',
      source: 'composite',
      'source-layer': 'building',
      type: 'fill-extrusion',
      minzoom: 12,
      paint: {
        'fill-extrusion-color': '#aaa',
        'fill-extrusion-height': ['interpolate', ['linear'], ['zoom'], 14, 0, 16, ['number', ['get', 'height'], 0]],
        'fill-extrusion-base': ['interpolate', ['linear'], ['zoom'], 14, 0, 16, ['number', ['get', 'base_height'], 0]],
        'fill-extrusion-opacity': 0.6,
      },
    }, labelLayerId);

    newMap.addSource('mask', { type: 'geojson', data: maskGeoJSON });
    newMap.addLayer({
      id: 'mask-layer',
      type: 'fill',
      source: 'mask',
      paint: { 'fill-color': '#FFFFFF', 'fill-opacity': 1 },
    });

    calculateAndDrawPath(start, end, newMap);
    startPathAnimation(newMap);
  });

  return newMap;
}

onMounted(() => {
  nextTick(() => {
    map = initializeMap(mapContainer.value, start, end);
  });
});

onUnmounted(() => {
  if (map) map.remove();
  if (expandedMap) expandedMap.remove();
  if (animationFrameId) cancelAnimationFrame(animationFrameId);
});

function expand() {
  isExpanded.value = true;
  nextTick(() => {
    expandedMap = initializeMap(expandedMapContainer.value, start, end, true);
  });
}

function close() {
  isExpanded.value = false;
  if (expandedMap) {
    expandedMap.remove();
    expandedMap = null;
  }
}

// debug
console.log('Beschikbare punten:', pointsData.features.map(p => p.properties.name));
console.log('Connecties voor Cordaat:', connectionsData.filter(c => c.from === "Cordaat" || c.to.includes("Cordaat")));
</script>

<style scoped>
.route-block {
  background-color: gray;
  border-radius: 10px;
  width: 100%;
  max-width: 765px;
  height: 350px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.map-container {
  flex: 1;
  width: 100%;
  height: 100%;
}

.hover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(128, 128, 128, 0.8);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.9em;
  border-radius: 10px;
  pointer-events: none;
}

.expanded-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1002;
}

.expanded-content {
  background-color: grey;
  border-radius: 10px;
  width: 90vw;
  height: 90vh;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  padding: 10px;
}

.expanded-map-container {
  flex: 1;
  width: 100%;
  height: 100%;
}

.close-btn {
  position: absolute;
  top: 5px;
  right: 5px;
  background: #e74c3c;
  border: none;
  color: white;
  font-size: 1.5em;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 50%;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.3);
  z-index: 1003; /* Verhoogd naar 1003 om boven expanded-overlay te blijven */
  transition: background-color 0.2s ease;
}

.close-btn:hover {
  background-color: #c0392b;
}
</style>
