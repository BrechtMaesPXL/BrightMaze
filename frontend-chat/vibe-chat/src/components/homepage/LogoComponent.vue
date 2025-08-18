<template>
  <div class="logo-wrapper">
    <!-- <img class="logo-icon" src="../../../public/Logo.ico" alt="Vibe Chat Logo" /> -->
     <div class="vibe-text">
        <span>VIBE</span>
      </div>
    <p class="current-location" v-if="currentBuilding">
      U bent nu bij: <strong>{{ currentBuilding }}</strong>
    </p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL

const currentBuilding = ref(null)

async function fetchCurrentLocation() {
  try {
    const response = await axios.get(`${API_BASE_URL}admin/api/settings/current-location`)
    currentBuilding.value = response.data
  } catch (error) {
    console.error('Fout bij ophalen locatie:', error)
    currentBuilding.value = 'Onbekend'
  }
}

onMounted(() => {
  fetchCurrentLocation()
})
</script>

<style scoped>
.logo-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  gap: 1rem; /* extra ruimte tussen items */
  padding: 1rem;
}

.logo-icon {
  width: 12rem;
  height: auto;
  max-width: 100%;
}

.vibe-text {
  font-size: 4em;
  font-family: 'Sigmar', sans-serif;
  font-weight: bold;
  color: white;
}

.current-location {
  font-size: 1.2rem;
  color: #ffffff;
  font-weight: 500;
}

@media (max-width: 768px) {
  .logo-icon {
    width: 8rem;
  }

  .vibe-text {
    font-size: 2.5em;
  }

  .current-location {
    font-size: 1rem;
  }
}

</style>
