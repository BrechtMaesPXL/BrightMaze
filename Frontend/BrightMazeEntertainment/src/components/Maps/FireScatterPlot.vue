<template>
  <div>
    <input
      v-model="selectedFips"
      @change="onFipsChange"
      list="fips-list"
      placeholder="Enter county (FIPS)"
      class="fips-input"
    />
    <datalist id="fips-list">
      <option v-for="f in fipsList" :key="f" :value="f" />
    </datalist>
  </div>
  <div id="plotly-map" ref="plotlyMap"></div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import Plotly from 'plotly.js-dist-min'
import { circle } from '@turf/turf'
import { useFiresStore } from '@/stores/firesStore.js'

const plotlyMap = ref(null)
const fireStore = useFiresStore()
const selectedFips = ref('')
const fipsList = ref([])

function getRadius(hectares) {
  const areaMeters = hectares * 10000
  return Math.sqrt(areaMeters / Math.PI)
}

function getColor(size) {
  if (size < 10) return 'green'
  else if (size < 100) return 'yellow'
  else return 'red'
}

let traces = []
let layout = {}

const drawMap = () => {
  if (!fireStore.firesData.length) return

  traces = fireStore.firesData.map(fire => {
    const radius = getRadius(fire.FIRE_SIZE)
    const center = [fire.LONGITUDE, fire.LATITUDE]
    const polygon = circle(center, radius, { steps: 50, units: 'meters' })

    return {
      type: 'scattermapbox',
      mode: 'lines',
      fill: 'toself',
      lon: polygon.geometry.coordinates[0].map(c => c[0]),
      lat: polygon.geometry.coordinates[0].map(c => c[1]),
      line: { color: getColor(fire.FIRE_SIZE) },
      fillcolor: getColor(fire.FIRE_SIZE) + '80',
      opacity: 0.7,
      text: `Name: ${fire.FIRE_NAME || 'Unknown'}<br>Size: ${fire.FIRE_SIZE} ha<br>Cause: ${fire.STAT_CAUSE_DESCR}`,
      hoverinfo: 'text'
    }
  })

  const centerLat = fireStore.firesData.reduce((sum, f) => sum + f.LATITUDE, 0) / fireStore.firesData.length
  const centerLon = fireStore.firesData.reduce((sum, f) => sum + f.LONGITUDE, 0) / fireStore.firesData.length

  layout = {
    mapbox: {
      style: 'carto-darkmatter',
      center: { lat: centerLat, lon: centerLon },
      zoom: 10
    },
    width: window.innerWidth,
    height: window.innerHeight,
    margin: { l: 0, r: 0, t: 0, b: 0 },
    showlegend: false,
    paper_bgcolor: 'black',
    plot_bgcolor: 'black'
  }

  Plotly.newPlot(plotlyMap.value, traces, layout)
}

const onFipsChange = async () => {
  if (!selectedFips.value) return
  await fireStore.fetchFires(selectedFips.value)
  drawMap()
}

const resizeMap = () => {
  layout.width = window.innerWidth
  layout.height = window.innerHeight
  Plotly.newPlot(plotlyMap.value, traces, layout)
}

onMounted(async () => {
  fipsList.value = await fireStore.fetchAllFIPS()
  if (fipsList.value.length) {
    selectedFips.value = fipsList.value[0]
    await fireStore.fetchFires(selectedFips.value)
  }
  drawMap()
  window.addEventListener('resize', resizeMap)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeMap)
})
</script>

<style scoped>
.fips-input {
  position: absolute;
  z-index: 10;
  top: 3%;
  right: 50%;
  left: 50%;
  padding: 0.5rem;
  border-radius: 0.375rem;
  border: none;
  background: rgba(255,255,255,0.9);
  width: 200px;
}

#plotly-map {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
}
</style>
