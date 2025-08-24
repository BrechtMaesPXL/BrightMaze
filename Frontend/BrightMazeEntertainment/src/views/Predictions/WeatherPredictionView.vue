<script setup>
import { ref } from 'vue'
import PredictionForm from '@/components/PredictionForm.vue'
import HomeButtonComponent from '@/components/Menu/HomeButtonComponent.vue'
import AmericaMap from '@/components/Maps/AmericaMap.vue'

const form = ref({
  location: '',
  date: '',
  temperature: '',
  wind: '',
  longitude: '',
  latitude: '',
  county: ''
})

const mapboxToken = import.meta.env.VITE_MAPBOX_TOKEN

if (!mapboxToken || mapboxToken === 'undefined') {
  // eslint-disable-next-line no-console
  console.warn('Mapbox access token ontbreekt of is niet geladen uit .env!');
}

function handleCoords(coords) {
  form.value.latitude = coords.latitude
  form.value.longitude = coords.longitude
}
function handleCounty(county) {
  form.value.county = county
}
</script>

<template>
  <div class="container">
    <div class="home-btn-wrapper">
      <HomeButtonComponent />
    </div>
    <div class="map-section">
      <AmericaMap
        v-model="form"
        :mapboxToken="mapboxToken"
        @update:coords="handleCoords"
        @update:county="handleCounty"
      />
    </div>
    <div class="form-section">
      <PredictionForm :form="form" mode="weather" @update:form="val => Object.assign(form, val)" />
    </div>
  </div>
</template>

<style scoped>
.container {
  display: flex;
  flex-direction: row;
  width: 100%;
  height: 100vh;
  background: #111;
  position: relative;
}
.home-btn-wrapper {
  position: absolute;
  top: 20px;
  left: 30px;
  z-index: 9999;
  pointer-events: auto;
}
.map-section {
  flex: 2;
  min-width: 0;
  position: relative;
}
.form-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #111;
}
</style>
