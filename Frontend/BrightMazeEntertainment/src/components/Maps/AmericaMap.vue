<template>
  <div class="mapbox-map" ref="container"></div>
</template>

<script setup>
import { ref, onMounted, watch, onBeforeUnmount } from 'vue'
import mapboxgl from 'mapbox-gl'
import * as turf from '@turf/turf'
import { useMapboxPointer } from '@/stores/mapStore.js'

const props = defineProps({
  latitude: Number,
  longitude: Number,
  mapboxToken: String,
  circle: Object
})
const emit = defineEmits(['update:coords', 'update:county'])

const container = ref(null)
const { map, marker, setPointer, clearPointer, initMap } = useMapboxPointer(props.mapboxToken)

const circleLayerId = 'fire-prediction-circle'
const circleSourceId = 'fire-prediction-source'

function drawCircleOnMap(circle) {
  if (!map.value || !circle || !circle.lat || !circle.lng || !circle.radius) return

  // Remove old layers/sources if exist
  if (map.value.getLayer(circleLayerId + '-border')) {
    map.value.removeLayer(circleLayerId + '-border')
  }
  if (map.value.getLayer(circleLayerId)) {
    map.value.removeLayer(circleLayerId)
  }
  if (map.value.getSource(circleSourceId)) {
    map.value.removeSource(circleSourceId)
  }

  // Use turf to create a circle (radius in meters)
  const turfCircle = turf.circle([circle.lng, circle.lat], circle.radius, {
    steps: 64,
    units: 'meters'
  })

  map.value.addSource(circleSourceId, {
    type: 'geojson',
    data: turfCircle
  })

  map.value.addLayer({
    id: circleLayerId,
    type: 'fill',
    source: circleSourceId,
    layout: {},
    paint: {
      'fill-color': '#ff9800',
      'fill-opacity': 0.25
    }
  })

  map.value.addLayer({
    id: circleLayerId + '-border',
    type: 'line',
    source: circleSourceId,
    layout: {},
    paint: {
      'line-color': '#ff9800',
      'line-width': 3
    }
  })
}

function removeCircleFromMap() {
  if (!map.value) return
  if (map.value.getLayer(circleLayerId + '-border')) {
    map.value.removeLayer(circleLayerId + '-border')
  }
  if (map.value.getLayer(circleLayerId)) {
    map.value.removeLayer(circleLayerId)
  }
  if (map.value.getSource(circleSourceId)) {
    map.value.removeSource(circleSourceId)
  }
}

onMounted(() => {
  mapboxgl.accessToken = props.mapboxToken
  const m = initMap(container.value, { center: [props.longitude || -98.5795, props.latitude || 39.8283], zoom: 3.5, style: 'mapbox://styles/mapbox/streets-v11' })
  m.on('click', e => {
    const { lng, lat } = e.lngLat
    emit('update:coords', { latitude: lat, longitude: lng })
    setPointer(lat, lng)
  })
  if (props.latitude && props.longitude) {
    setPointer(props.latitude, props.longitude)
  }
  if (props.circle) {
    m.on('load', () => {
      drawCircleOnMap(props.circle)
    })
  }
})

watch(() => props.circle, (circle) => {
  if (!map.value) return
  removeCircleFromMap()
  if (circle && circle.lat && circle.lng && circle.radius) {
    drawCircleOnMap(circle)
  }
})

onBeforeUnmount(() => {
  if (map.value) {
    removeCircleFromMap()
    map.value.remove()
  }
})
</script>

<style scoped>
.mapbox-map {
  width: 100%;
  height: 100vh;
  z-index: 2;
}
</style>
