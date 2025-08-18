<template>
  <v-card class="pa-6 mx-auto" elevation="10" rounded="xl">
    <v-card-title class="text-h5 font-weight-bold">
      Route
    </v-card-title>

    <v-card-text class="d-flex flex-column justify-center" style="height: calc(100% - 80px);">
      <div class="text-body-1 mb-4">Schakel route functionaliteit in of uit </div>

      <div class="toggle-container">
        <button class="toggle-button" :class="{ active: isActive }" @click="toggleActive">
          {{ isActive ? "Route functionaliteit: Aan" : "Route functionaliteit: Uit" }}
        </button>
      </div>
    </v-card-text>
  </v-card>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;
const isActive = ref(false);

function reset() {  
  isActive.value = true;
  toggleActive();
}
defineExpose({ reset });

async function fetchRouteEnabled() {
  try {
    const response = await axios.get(`${API_BASE_URL}admin/api/settings/route-settings`);
    isActive.value = response.data;
  } catch (error) {
    console.error('Error fetching route status:', error);
    alert('Fout bij ophalen van route status.');
  }
}

async function toggleActive() {
  try {
    await axios.post(`${API_BASE_URL}admin/api/settings/route-settings`, {
      enabled: !isActive.value
    });
    isActive.value = !isActive.value; // Toggle the button state
  } catch (error) {
    console.error('Error toggling route status:', error);
    alert('Fout bij bijwerken van route status.');
  }
}

onMounted(() => {
  fetchRouteEnabled(); // Fetch the current status when the component is mounted
});
</script>

<style scoped>
.v-card {
  color: white;
  width: 450px;
  height: 350px;
}

.v-card-title {
  justify-content: center;
  color: white;
}

.v-card-text {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.toggle-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 1rem;
}

.toggle-button {
  padding: 0.5rem 1.5rem;
  font-size: 1rem;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  background-color: #ff0000;
  color: #ffffff;
  transition: background-color 0.3s, color 0.3s;
}

.toggle-button.active {
  background-color: #4caf50; /* Green for active state */
  color: white;
}
</style>
