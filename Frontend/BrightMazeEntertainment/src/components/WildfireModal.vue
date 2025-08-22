<template>
  <div v-if="wildfire || yearDetails" class="modal-overlay" @click="emit('close')">
    <div class="modal-content" @click.stop>
      <!-- Year-specific details (LineGraph) -->
      <template v-if="yearDetails && !yearDetails.error">
        <h3>Wildfire Details for {{ yearDetails.year }}</h3>
        <p><strong>Most Common Cause:</strong> {{ yearDetails.most_common_cause.cause }} ({{ yearDetails.most_common_cause.percentage }}%)</p>
        <h4>Largest Fire: {{ yearDetails.largest_fire.FIRE_NAME || 'Unknown' }}</h4>
        <p><strong>Size (acres):</strong> {{ (yearDetails.largest_fire.FIRE_SIZE || 0).toLocaleString() }}</p>
        <p><strong>State:</strong> {{ getStateDisplay(yearDetails.largest_fire.STATE) }}</p>
      </template>

      <!-- Single wildfire details (BarGraph) -->
      <template v-if="wildfire">
        <h3>{{ extractYear(wildfire.DISCOVERY_DATE) }} {{ wildfire.FIRE_NAME || 'Unknown' }}</h3>
        <p><strong>Size (acres):</strong> {{ (wildfire.FIRE_SIZE || 0).toLocaleString() }}</p>
        <p><strong>State:</strong> {{ getStateDisplay(wildfire.STATE) }}</p>
        <p><strong>Discovery Date:</strong> {{ formatDate(wildfire.DISCOVERY_DATE) }}</p>
        <p><strong>Containment Date:</strong> {{ formatDate(wildfire.CONT_DATE) }}</p>
        <p><strong>Cause:</strong> {{ wildfire.STAT_CAUSE_DESCR || 'Unknown' }}</p>
        <p><strong>Duration (days):</strong> {{ wildfire.FIRE_DURATION || 'Unknown' }}</p>
        <p><strong>Latitude:</strong> {{ wildfire.LATITUDE || 'Unknown' }}</p>
        <p><strong>Longitude:</strong> {{ wildfire.LONGITUDE || 'Unknown' }}</p>
      </template>

      <!-- Error case (LineGraph) -->
      <template v-if="yearDetails && yearDetails.error">
        <h3>Error</h3>
        <p>{{ yearDetails.error }}</p>
      </template>

      <button class="close-button" @click="emit('close')">Close</button>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'
import { US_STATES } from '@/constants/states'

defineProps({
  wildfire: {
    type: Object,
    default: null
  },
  yearDetails: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close'])

const getStateDisplay = (stateAbbr) => {
  const state = US_STATES[stateAbbr]
  return state ? `${state.name} (${state.id})` : stateAbbr || 'Unknown'
}

const formatDate = (dateStr) => {
  if (!dateStr || dateStr === 'Unknown') return 'Unknown'
  try {
    const date = new Date(dateStr)
    return date.toLocaleDateString('en-US', {
      month: 'long',
      day: 'numeric',
      year: 'numeric'
    })
  } catch {
    return 'Unknown'
  }
}

const extractYear = (dateStr) => {
  if (!dateStr || dateStr === 'Unknown') return 'Unknown'
  try {
    const date = new Date(dateStr)
    return date.getFullYear().toString()
  } catch {
    return 'Unknown'
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 8px;
  max-width: 500px;
  width: 90%;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  position: relative;
}

.modal-content h3 {
  margin-top: 0;
}

.modal-content h4 {
  margin: 15px 0 10px;
}

.modal-content p {
  margin: 10px 0;
}

.close-button {
  position: absolute;
  bottom: 20px;
  right: 20px;
  padding: 10px 20px;
  background-color: #ffa500;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.close-button:hover {
  background-color: #e69500;
}
</style>
