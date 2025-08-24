<template>
  <v-card class="pa-6 mx-auto" elevation="10" rounded="xl">
    <v-card-title class="text-h5 font-weight-bold">
      Locatie
    </v-card-title>

    <v-card-text>
      <div class="mb-4">
        <div class="text-body-1 mb-4">Verander de locatie van Cordi</div>
        <strong>Huidige locatie:</strong> {{ currentLocation || 'Laden...' }}
      </div>

      <v-select
        v-model="selectedLocation"
        :items="locations"
        label="Selecteer een locatie"
        item-title="label"
        item-value="value"
        outlined
        rounded
        dense
        class="mb-4"
        :menu-props="{ maxHeight: '400' }"
      />

      <v-btn
        color="primary"
        class="text-none"
        block
        rounded
        @click="confirmLocation"
      >
        Bevestig Locatie
      </v-btn>
    </v-card-text>
  </v-card>
</template>

<script setup>
import { ref, onMounted, defineExpose } from 'vue'
import axios from 'axios'
import { useLocationsStore } from '@/stores/locationsStore'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL

const { locations } = useLocationsStore()

const selectedLocation = ref(null)
const currentLocation = ref(null)

function reset() {  
  selectedLocation.value = "Corda 2"
  confirmLocation()
}
defineExpose({ reset });

async function fetchCurrentLocation() {
  try {
    const response = await axios.get(`${API_BASE_URL}admin/api/settings/current-location`)
    currentLocation.value = response.data
  } catch (error) {
    console.error('Fout bij ophalen locatie:', error)
    currentLocation.value = 'Onbekend'
  }
}

async function confirmLocation() {
  if (!selectedLocation.value) {
    alert('Selecteer eerst een locatie.')
    return
  }

  try {
    await axios.post(`${API_BASE_URL}admin/api/settings/current-location`, {
      current_building: selectedLocation.value
    })
    alert(`Locatie succesvol bijgewerkt naar ${selectedLocation.value}`)
    fetchCurrentLocation()
    selectedLocation.value = null
  } catch (error) {
    console.error('Fout bij versturen locatie:', error)
    alert('Fout bij bijwerken van locatie.')
  }
}

onMounted(() => {
  fetchCurrentLocation()
})
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
</style>
