<template>
  <div ref="chartContainer" class="choropleth-chart"></div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import Plotly from 'plotly.js-dist-min'

const chartContainer = ref(null)
let plotData, layout;
// Fetch and process data
async function loadData() {
  try {
    const [geoJson, csvData] = await Promise.all([
      fetch('https://raw.githubusercontent.com/plotly/datasets/master/geojson-counties-fips.json')
        .then(response => response.json()),
      fetch('https://raw.githubusercontent.com/plotly/datasets/master/fips-unemp-16.csv')
        .then(response => response.text())
    ])

    // Process CSV data
    const rows = csvData.split('\n').slice(1)
    const processedData = rows.map(row => {
      const [fips, unemp] = row.split(',')
      return { fips: fips.padStart(5, '0'), unemp: parseFloat(unemp) }
    })

    return { geoJson, processedData }
  } catch (error) {
    console.error('Error loading data:', error)
    return null
  }
}

// Create the choropleth map
async function createChart() {
  const data = await loadData()
  if (!data) return

   plotData = [{
    type: 'choropleth',
    geojson: data.geoJson,
    locations: data.processedData.map(d => d.fips),
    z: data.processedData.map(d => d.unemp),
    colorscale: 'Viridis',
    zmin: 0,
    zmax: 12,
    marker: {
      line: {
        color: 'white',
        width: 0.5
      }
    },
    colorbar: {
      title: 'Unemployment Rate'
    }
  }]

   layout = {
    title: 'US Unemployment Rate by County',
    geo: {

      scope: 'usa',
      projection: {
        type: 'albers usa'
      }
    },
    width: window.innerWidth,
    height: window.innerHeight,
    margin: { r: 25, t: 25, l: 25, b: 25 }
  }

  Plotly.newPlot(chartContainer.value, plotData, layout)
}
const resizeMap = () => {
  layout.width = window.innerWidth;
  layout.height = window.innerHeight;
  Plotly.newPlot(chartContainer.value, plotData, layout)

};

onMounted(() => {
  createChart()
  window.addEventListener("resize", resizeMap);

})

onUnmounted(() => {
  if (chartContainer.value) {
    Plotly.purge(chartContainer.value)
  }
  window.removeEventListener("resize", resizeMap);

})
</script>

<style scoped>
.choropleth-chart {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
}
</style>
