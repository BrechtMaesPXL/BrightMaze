<script setup>
import {onBeforeUnmount, onMounted, watch} from "vue";
import Plotly from "plotly.js-dist";
import { useMapStore } from "../../stores/map.js";

const mapStore = useMapStore();
let data, layout;

const updateMap = () => {
   data = [
    {
      type: "choroplethmap",
      name: "US states",
      geojson: "https://raw.githubusercontent.com/python-visualization/folium/master/examples/data/us-states.json",
      locations: mapStore.locations,
      z: mapStore.z,
      zmin: 25,
      zmax: 280,
      colorbar: {
        x: 0,
        y: 0,
        yanchor: "bottom",
        title: { text: "US States Fire's", side: "right", font: { color: "white" } },
        tickfont: { color: "white" },
      },
    },
  ];

   layout = {
    map: { style: "dark", center: { lon: -110, lat: 50 }, zoom: 3 },
    width: window.innerWidth,
    height: window.innerHeight,
    margin: { t: 0, b: 0, l: 0, r: 0 },
  };

  Plotly.react("mapContainer", data, layout);
};
const resizeMap = () => {
  layout.width = window.innerWidth;
  layout.height = window.innerHeight;
  Plotly.react("mapContainer", data, layout);
};
onMounted(async () => {
  await mapStore.getLocationsFiresByTime();
  updateMap();
  window.addEventListener("resize", resizeMap);
});
onBeforeUnmount(() => {
  window.removeEventListener("resize", resizeMap);
});
watch(
  () => [
    mapStore.z,
    mapStore.locations,
    mapStore.selectedDateStartDate,
    mapStore.selectedDateEndDate,
  ],
  () => {
    updateMap();
  }

);
</script>

<template>
  <div id="mapContainer"></div>
</template>

<style scoped>
#mapContainer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
}
</style>
