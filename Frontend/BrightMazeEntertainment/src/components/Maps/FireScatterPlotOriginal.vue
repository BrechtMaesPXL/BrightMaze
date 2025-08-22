<template>

  <div id="plotly-map" ref="plotlyMap"></div>

</template>



<script setup>

import { ref, onMounted, onBeforeUnmount } from 'vue'

import Plotly from 'plotly.js-dist-min'

import { circle } from '@turf/turf'

import { useFiresStore } from '@/stores/firesStore.js'



const plotlyMap = ref(null)

const fireStore = useFiresStore()



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

    const options = { steps: 50, units: 'meters' }

    const polygon = circle(center, radius, options)



    return {

      type: 'scattermapbox',

      mode: 'lines',

      fill: 'toself',

      lon: polygon.geometry.coordinates[0].map(coord => coord[0]),

      lat: polygon.geometry.coordinates[0].map(coord => coord[1]),

      line: { color: getColor(fire.FIRE_SIZE) },

      fillcolor: getColor(fire.FIRE_SIZE) + '80',

      opacity: 0.7,

      text: `Name: ${fire.FIRE_NAME || 'Unknown'}<br>Size: ${fire.FIRE_SIZE} hectares<br>Cause: ${fire.STAT_CAUSE_DESCR}`,

      hoverinfo: 'text'

    }

  })



  const centerLat = fireStore.firesData.reduce((sum, fire) => sum + fire.LATITUDE, 0) / fireStore.firesData.length

  const centerLon = fireStore.firesData.reduce((sum, fire) => sum + fire.LONGITUDE, 0) / fireStore.firesData.length



  layout = {

    mapbox: {

      style: 'open-street-map',

      center: { lat: centerLat, lon: centerLon },

      zoom: 3

    },

    width: window.innerWidth,

    height: window.innerHeight,

    margin: { l: 0, r: 0, t: 0, b: 0 },

    showlegend: false

  }



  Plotly.newPlot(plotlyMap.value, traces, layout)

}



const resizeMap = () => {

  layout.width = window.innerWidth

  layout.height = window.innerHeight

  Plotly.newPlot(plotlyMap.value, traces, layout)

}



onMounted(async () => {

  await fireStore.fetchFires()

  drawMap()

  window.addEventListener("resize", resizeMap)

})



onBeforeUnmount(() => {

  window.removeEventListener("resize", resizeMap)

})

</script>



<style scoped>

#plotly-map {

  position: absolute;

  top: 0;

  left: 0;

  width: 100%;

  height: 100vh;

}

</style>
