<script setup>
import { onMounted, onBeforeUnmount, ref, watch } from "vue";
import Plotly from "plotly.js-dist";
import { useMapStore } from "@/stores/map";

const props = defineProps({
  maxValue: {
    type: Number,
    default: 11000,
  },
});

const mapStore = useMapStore();
const selectedState = ref(null);
const emit = defineEmits(["stateSelected"]);

const data = ref(null);
const layout = ref(null);

const drawMap = () => {
  data.value = [
    {
      type: "choropleth",
      name: "US States",
      geojson: "https://raw.githubusercontent.com/python-visualization/folium/master/examples/data/us-states.json",
      locations: mapStore.locations,
      z: mapStore.z,
      zmin: 0,
      zmax: props.maxValue,
      colorscale: [
        [0, "#FFEDA0"],
        [0.2, "#FC4E2A"],
        [0.4, "#E31A1C"],
        [0.6, "#BD0026"],
        [1, "#800026"],
      ],
      colorbar: {
        x: 0.05,
        y: 0.5,
        len: 0.5,
        yanchor: "middle",
        title: { text: "Wildfires", side: "right", font: { color: "white" } },
        tickfont: { color: "white" },
        thickness: 20,
      },
      hoverinfo: "location+z",
    },
  ];

  layout.value = {
    geo: {
      scope: "usa",
      projection: { type: "albers usa" },
      showlakes: true,
      lakecolor: "rgb(255, 255, 255)",
      bgcolor: "#000",
    },
    width: window.innerWidth,
    height: window.innerHeight,
    margin: { t: 0, b: 0, l: 0, r: 0 },
    paper_bgcolor: "#000",
    showlegend: false,
  };

  Plotly.newPlot("mapContainer", data.value, layout.value).then((chart) => {
    chart.on("plotly_click", handleStateClick);
  });
};

const handleStateClick = (eventData) => {
  if (eventData.points && eventData.points[0]) {
    selectedState.value = eventData.points[0].location;
    emit("stateSelected", selectedState.value);
    console.log("Selected state:", selectedState.value);
  }
};

const resizeMap = () => {
  if (layout.value) {
    layout.value.width = window.innerWidth;
    layout.value.height = window.innerHeight;
    Plotly.relayout("mapContainer", layout.value);
  }
};

onMounted(async () => {
  mapStore.selectedDate = 1992; // Always start at minimum year
  await mapStore.getLocationsFiresByTime();
  drawMap();
  window.addEventListener("resize", resizeMap);
});

onBeforeUnmount(() => {
  window.removeEventListener("resize", resizeMap);
});

watch(
  () => mapStore.z,
  () => {
    if (data.value && layout.value) {
      data.value[0].z = mapStore.z;
      data.value[0].zmax = props.maxValue;
      Plotly.react("mapContainer", data.value, layout.value);
    }
  },
  { deep: true }
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
  z-index: 1;
  background: #000 !important;
}
</style>
