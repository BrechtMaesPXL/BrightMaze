<template>
  <div class="page-wrapper">
    <div class="content-wrapper">
      <div class="container fire-prediction">
        <h1>How big will the fire be?</h1>

        <div v-if="fireSizeResult" class="fire-size-result">
          <strong>Predicted Fire Size Class:</strong> {{ fireSizeResult }}
          <div class="actions">
            <button @click="resetForm">New Prediction</button>
          </div>
        </div>

        <div v-else>
          <!-- Stepper -->
          <div class="timeline-steps">
            <div
              v-for="(step, index) in steps"
              :key="index"
              class="timeline-step"
              :class="{ active: currentStep === index, completed: index < currentStep }"
              @click="goToStep(index)">
              <div class="step-circle">
                <span v-if="index < currentStep">✓</span>
              </div>
              <div class="step-label">{{ step.label }}</div>
            </div>
          </div>

          <!-- Step content -->
          <transition name="slide-fade" mode="out-in">
            <div class="step-content" :key="currentStep">
              <div v-if="currentStep === 0" class="step-panel">
                <label>
                  Latitude:
                  <input
                    type="number"
                    v-model.number="form.latitude"
                    placeholder="Enter latitude"
                  />
                </label>
                <label>
                  Longitude:
                  <input
                    type="number"
                    v-model.number="form.longitude"
                    placeholder="Enter longitude"
                  />
                </label>
              </div>

              <div v-else-if="currentStep === 1" class="step-panel">
                <label>
                  Discovery Date:
                  <input
                    type="date"
                    v-model="form.discoveryDate"
                    min="2016-01-01"
                    max="2016-12-31"
                  />
                </label>
                <label>
                  Discovery Time:
                  <input
                    type="time"
                    v-model="form.discoveryTime"
                  />
                </label>
              </div>

              <div v-else class="step-panel">
                <label>
                  Wind snelheid (km/h):
                  <input
                    type="number"
                    v-model.number="form.windSpeed"
                    placeholder="Optional"
                  />
                </label>
                <label>
                  Precipitation (mm):
                  <input
                    type="number"
                    v-model.number="form.precipitation"
                    placeholder="Optional"
                  />
                </label>
                <label>
                  Temperatuur (°C):
                  <input
                    type="number"
                    v-model.number="form.temp"
                    placeholder="Optional"
                  />
                </label>
              </div>
            </div>
          </transition>

          <!-- Navigation buttons -->
          <div class="nav-buttons">
            <button @click="prevStep" :disabled="currentStep === 0">Previous</button>
            <button
              v-if="currentStep < steps.length - 1"
              @click="nextStep"
              :disabled="!isStepValid"
            >
              Next
            </button>
            <button
              v-else
              @click="predictFireSize"
              :disabled="!isStepValid"
            >
              Predict
            </button>
          </div>
        </div>
      </div>

      <div class="map-container">
        <AmericaMap
          class="usa-map"
          :latitude="form.latitude !== null ? form.latitude : 39.8283"
          :longitude="form.longitude !== null ? form.longitude : -98.5795"
          @update:coords="coords => {
            form.latitude = coords.latitude;
            form.longitude = coords.longitude;
          }"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import AmericaMap from '@/components/Maps/AmericaMap.vue'
import { predictFireSizeClass } from '@/stores/predictionStore.js'

const steps = [
  { label: 'Location' },
  { label: 'Start Time' },
  { label: 'Weather' }
]
const currentStep = ref(0)

const form = ref({
  latitude: null,
  longitude: null,
  discoveryDate: null,
  discoveryTime: null,
  windSpeed: null,
  precipitation: null,
  temp: null
})

const fireSizeResult = ref(null)

const isStepValid = computed(() => {
  switch (currentStep.value) {
    case 0:
      return form.value.latitude !== null && form.value.longitude !== null
    case 1:
      return form.value.discoveryDate !== null && form.value.discoveryTime !== null
    case 2:
      return true
    default:
      return false
  }
})

function goToStep(index) {
  if (index < currentStep.value || isStepValid.value) {
    currentStep.value = index
  }
}

function nextStep() {
  if (currentStep.value < steps.length - 1 && isStepValid.value) {
    currentStep.value++
  }
}

function prevStep() {
  if (currentStep.value > 0) currentStep.value--
}

async function predictFireSize() {
  if (!isStepValid.value) return

  // Combine date and time for the backend
  const date =
    form.value.discoveryDate && form.value.discoveryTime
      ? `${form.value.discoveryDate}T${form.value.discoveryTime}`
      : form.value.discoveryDate

  const county = form.value.county || ''

  fireSizeResult.value = null
  try {
    const res = await predictFireSizeClass({
      latitude: form.value.latitude,
      longitude: form.value.longitude,
      date,
      county
    })
    fireSizeResult.value = res.fire_size_class
  } catch (err) {
    fireSizeResult.value = 'Error'
  }
}

function resetForm() {
  fireSizeResult.value = null
  currentStep.value = 0
  form.value = {
    latitude: null,
    longitude: null,
    discoveryDate: null,
    discoveryTime: null,
    windSpeed: null,
    precipitation: null,
    temp: null
  }
}
</script>

<style scoped>
.page-wrapper {
  position: relative;
  display: flex;
  flex-direction: column;
  width: 100vw;
  height: 100vh;
  background: linear-gradient(rgba(0,0,0,0.85), rgba(0,0,0,0.85)), url('https://cdn.mos.cms.futurecdn.net/2NrTmPW9XnRMczrPiRM8M3.jpg');
  background-size: cover;
  color: #fff;
}

.content-wrapper {
  display: flex;
  flex: 1;
  overflow: hidden;
  justify-content: space-between;
  align-items: center;
}

.container {
  flex: 1;
  max-width: 40%;
  padding: 30px;
  text-align: center;
  background: rgba(0,0,0,0.6);
  border-radius: 12px;
  overflow-y: auto;
}

.map-container {
  flex: 2;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.usa-map {
  border: 2px solid #e98616;
  border-radius: 6px;
}

h1 {
  color: #e98616;
  margin-bottom: 1.5rem;
  font-size: 2.2rem;
}

.timeline-steps {
  position: relative;
  display: flex;
  justify-content: space-between;
  margin: 30px 0;
}

.timeline-step {
  position: relative;
  z-index: 1;
  cursor: pointer;
  text-align: center;
}

.step-circle {
  width: 30px;
  height: 30px;
  border: 3px solid #e98616;
  border-radius: 50%;
  background: #000;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.1rem;
  transition: background 0.3s, transform 0.3s;
}

.timeline-step.active .step-circle,
.timeline-step.completed .step-circle {
  background: #e98616;
  transform: scale(1.3);
}

.step-label {
  margin-top: 10px;
  font-size: 1rem;
  color: #e98616;
}

.step-content {
  margin-top: 20px;
}

.step-panel {
  animation: fadeIn 0.5s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateX(20px); }
  to { opacity: 1; transform: translateX(0); }
}

.step-content label {
  display: block;
  margin: 16px 0;
  text-align: left;
  font-size: 1.1rem;
}

.step-content input {
  width: 100%;
  padding: 10px;
  border: 1px solid #555;
  border-radius: 6px;
  background: #111;
  color: #fff;
  font-size: 1rem;
}

.nav-buttons {
  margin-top: 25px;
  display: flex;
  justify-content: space-between;
}

.nav-buttons button {
  padding: 12px 24px;
  background: #e98616;
  border: none;
  border-radius: 6px;
  color: #000;
  font-size: 1rem;
  cursor: pointer;
  transition: background 0.2s;
}

.nav-buttons button:disabled {
  background: #444;
  cursor: not-allowed;
}

.nav-buttons button:hover:not(:disabled) {
  background: #ffab40;
}

.content-wrapper.no-map .nav-buttons {
  justify-content: center;
}

.content-wrapper.no-map .nav-buttons button + button {
  margin-left: 20px;
}

.results-wrapper {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
}

.fire-size-result {
  margin-top: 2rem;
  color: #ff9800;
  background: #222;
  padding: 1rem 1.5rem;
  border-radius: 0.5rem;
  font-weight: bold;
  text-align: center;
}

.actions {
  margin-top: 1.5rem;
}

.actions button {
  background: #e98616;
  color: #000;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  font-size: 1rem;
  cursor: pointer;
  transition: background 0.2s;
}

.actions button:hover {
  background: #ffab40;
}
</style>
