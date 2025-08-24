<template>
  <div class="container">
    <div class="home-btn-wrapper">
      <HomeButtonComponent />
    </div>
    <div class="map-section">
      <AmericaMap
        :latitude="form.latitude !== null ? form.latitude : 39.8283"
        :longitude="form.longitude !== null ? form.longitude : -98.5795"
        :mapboxToken="mapboxToken"
        :circle="fireCircle"
        @update:coords="handleCoords"
        @update:county="handleCounty"
      />
    </div>
    <div class="form-section">
      <FirePredictionForm
        :form="form"
        mode="fire-size"
        @update:form="val => Object.assign(form, val)"
        @fire-circle="handleFireCircle"
      />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import FirePredictionForm from '@/components/FirePredictionForm.vue'
import HomeButtonComponent from '@/components/Menu/HomeButtonComponent.vue'
import AmericaMap from '@/components/Maps/AmericaMap.vue'

const form = ref({
  name: '',
  latitude: null,
  longitude: null,
  county: '',
  date: '',
  fire_size: null
})

const fireCircle = ref(null)
const mapboxToken = import.meta.env.VITE_MAPBOX_TOKEN

function handleCoords(coords) {
  form.value.latitude = coords.latitude
  form.value.longitude = coords.longitude
}
function handleCounty(county) {
  form.value.county = county
}
function handleFireCircle(circle) {
  fireCircle.value = circle
}
</script>

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
  z-index: 2;
}
.form-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #111;
  z-index: 1;
}
</style>
