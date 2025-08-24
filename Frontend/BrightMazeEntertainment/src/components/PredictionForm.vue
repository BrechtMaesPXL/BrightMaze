<template>
  <form @submit.prevent="onSubmit">
    <label>
      Longitude:
      <input v-model="localForm.longitude" type="number" step="any" placeholder="Longitude" />
    </label>
    <label>
      Latitude:
      <input v-model="localForm.latitude" type="number" step="any" placeholder="Latitude" />
    </label>
    <label>
      County:
      <input v-model="localForm.county" type="text" placeholder="County" readonly />
    </label>
    <label>
      Date:
      <input
        v-model="localForm.date"
        type="date"
        required
        min="2016-01-01"
        max="2016-12-31"
      />
    </label>
    <button type="submit">
      {{ mode === 'weather' ? 'Predict Weather' : 'Predict Cause' }}
    </button>

    <!-- Cause Prediction -->
    <div v-if="mode === 'cause' && predictedCause" class="prediction-result">
      <strong>Predicted Cause:</strong> {{ predictedCause }}
      <div v-if="recommendations.length" class="prevention-tips">
        <strong>How to prevent:</strong>
        <ul>
          <li v-for="rec in recommendations" :key="rec">{{ rec }}</li>
        </ul>
      </div>
    </div>

    <!-- Weather Prediction -->
    <div v-if="mode === 'weather' && weatherResult" class="weather-result-box">
      <div class="section">
        <h3 class="section-title">🌤️ Predicted Weather</h3>
        <ul class="info-list">
          <li><strong>🌡️ Temperature:</strong> {{ weatherResult.temperature }} °C</li>
          <li><strong>💨 Wind speed:</strong> {{ weatherResult.wind }} km/h</li>
          <li><strong>🌧️ Precipitation:</strong> {{ weatherResult.prcp }}</li>
        </ul>
      </div>

      <div class="section">
        <h4 class="section-title">🛡️ Weather Safety Tips</h4>
        <ul class="tips-list">
          <li v-for="tip in getWeatherSafetyTips(weatherResult)" :key="tip">{{ tip }}</li>
        </ul>
      </div>
    </div>
  </form>
</template>


<style scoped>
form {
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
  width: 85%;
  background: #181818;
  padding: 2rem 2rem 1.5rem 2rem;
  border-radius: 1.2rem;
  box-shadow: 0 0 24px 0 #ff980055;
}
label {
  display: flex;
  flex-direction: column;
  font-weight: bold;
  color: #ff9800;
  font-size: 1.1rem;
  gap: 0.3rem;
}
input[type="text"],
input[type="number"],
input[type="date"] {
  padding: 0.5rem 0.7rem;
  border-radius: 0.5rem;
  border: 1px solid #ff9800;
  background: #222;
  color: #fff;
  font-size: 1rem;
  outline: none;
  transition: border 0.2s;
}
input[type="text"]:focus,
input[type="number"]:focus,
input[type="date"]:focus {
  border: 2px solid #ff9800;
}
button {
  padding: 0.7rem 1.2rem;
  font-size: 1.1rem;
  background: #ff9800;
  color: #111;
  border: none;
  border-radius: 0.5rem;
  font-weight: bold;
  cursor: pointer;
  margin-top: 1rem;
  transition: background 0.2s, color 0.2s;
}
button:hover {
  background: #fff;
  color: #ff9800;
}
.prediction-result {
  margin-top: 1rem;
  color: #ff9800;
  background: #222;
  padding: 0.7rem 1rem;
  border-radius: 0.5rem;
  font-weight: bold;
  text-align: center;
}
.prevention-tips {
  margin-top: 0.7rem;
  color: #fff;
  background: #333;
  padding: 0.6rem 1rem;
  border-radius: 0.5rem;
  font-size: 1rem;
}
.prevention-tips ul {
  margin: 0.3rem 0 0 1.2rem;
  padding: 0;
}

.weather-result-box {
  background-color: #1a1a1a;
  color: #ffffff;
  padding: 1.5rem;
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  max-width: 450px;
  margin: 1rem auto;
  font-family: Arial, sans-serif;
}

.section {
  margin-bottom: 1.5rem;
}

.section-title {
  font-size: 1.2rem;
  margin-bottom: 0.5rem;
  color: #ffcc00;
}

.info-list,
.tips-list {
  list-style: none;
  padding-left: 0;
  margin: 0;
}

.tips-list {
  list-style-type: disc;
  padding-left: 1.5rem;
}
</style>


<script setup>
import { reactive, watch, ref } from 'vue'
import { useFirePreventionStore } from '@/stores/firePrevention.js'
import {
  predictTemp,
  predictPrcp,
  predictWspd,
  predictStatCauseDescr,
  getWeatherSafetyTips,
  getDiscoveryDOY,
  encodeSeason,
  getSinCosDOY,
  prcpCategoryToNum
} from '@/stores/predictionStore.js'
import { countyToEncoded } from '@/constants/counties.js'
import { FireCauses } from '@/constants/FireCauses.js'

const props = defineProps({
  form: {
    type: Object,
    required: true
  },
  mode: {
    type: String,
    default: 'cause' // 'cause' or 'weather'
  }
})
const emit = defineEmits(['update:form', 'submit'])

const localForm = reactive({ ...props.form })
const predictedCause = ref(null)
const firePreventionStore = useFirePreventionStore()
const recommendations = ref([])
const weatherResult = ref(null)

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8000'

// Automatically fetch county when latitude/longitude change
watch(
  () => [localForm.latitude, localForm.longitude],
  async ([lat, lng]) => {
    if (lat && lng && !isNaN(lat) && !isNaN(lng)) {
      try {
        const url = `https://nominatim.openstreetmap.org/reverse?lat=${lat}&lon=${lng}&format=json&zoom=10&addressdetails=1`
        const response = await fetch(url, { headers: { 'Accept-Language': 'en' } })
        const data = await response.json()
        localForm.county = data.address?.county || data.address?.state_district || ''
      } catch {
        localForm.county = ''
      }
    } else {
      localForm.county = ''
    }
  }
)

watch(
  () => ({ ...localForm }),
  (val) => emit('update:form', { ...val }),
  { deep: true }
)
watch(
  () => props.form,
  (val) => Object.assign(localForm, val),
  { deep: true }
)

async function onSubmit(e) {
  if (e && typeof e.preventDefault === 'function') e.preventDefault()
  const { latitude, longitude, date, county } = localForm
  if (!latitude || !longitude || !date) {
    predictedCause.value = null
    recommendations.value = []
    weatherResult.value = null
    return
  }
  const discovery_doy = getDiscoveryDOY(date)
  if (discovery_doy === null) {
    predictedCause.value = "Invalid date"
    recommendations.value = []
    weatherResult.value = null
    return
  }

  if (props.mode === 'weather') {
    // Weather prediction
    try {
      const [tempRes, wspdRes, prcpRes] = await Promise.all([
        predictTemp({ LATITUDE: parseFloat(latitude), LONGITUDE: parseFloat(longitude), DISCOVERY_DOY: discovery_doy }),
        predictWspd({ LATITUDE: parseFloat(latitude), LONGITUDE: parseFloat(longitude), DISCOVERY_DOY: discovery_doy }),
        predictPrcp({ LATITUDE: parseFloat(latitude), LONGITUDE: parseFloat(longitude), DISCOVERY_DOY: discovery_doy })
      ])
      weatherResult.value = {
        temperature: tempRes.temp,
        wind: wspdRes.wspd,
        prcp: prcpRes.prcp
      }
      emit('submit', { ...localForm, ...weatherResult.value })
    } catch (err) {
      weatherResult.value = { temperature: 'Error', wind: 'Error', prcp: 'Error' }
    }
    predictedCause.value = null
    recommendations.value = []
  } else {
    // Fire cause prediction
    try {
      // Predict weather features
      const [tempResult, prcpResult, wspdResult] = await Promise.all([
        predictTemp({
          LATITUDE: parseFloat(latitude),
          LONGITUDE: parseFloat(longitude),
          DISCOVERY_DOY: discovery_doy
        }),
        predictPrcp({
          LATITUDE: parseFloat(latitude),
          LONGITUDE: parseFloat(longitude),
          DISCOVERY_DOY: discovery_doy
        }),
        predictWspd({
          LATITUDE: parseFloat(latitude),
          LONGITUDE: parseFloat(longitude),
          DISCOVERY_DOY: discovery_doy
        })
      ])

      // Prepare stat_cause_descr input
      const dt = new Date(date)
      const FIRE_YEAR = 2016
      const OWNER_CODE = 5
      const FIRE_DURATION_MINUTES = 5
      const season_encoded = encodeSeason(dt.getMonth() + 1)
      const { sin: DISCOVERY_DOY_sin, cos: DISCOVERY_DOY_cos } = getSinCosDOY(discovery_doy)
      const prcp_num = prcpCategoryToNum(prcpResult.prcp)
      let county_encoded = 0
      if (county && countyToEncoded[county]) {
        county_encoded = countyToEncoded[county]
      }

      const statCauseResult = await predictStatCauseDescr({
        latitude: parseFloat(latitude),
        longitude: parseFloat(longitude),
        date,
        county: county,
        FIRE_YEAR,
        wspd_mean_0: wspdResult.wspd,
        prcp_sum_0: prcp_num,
        temp_mean_0: tempResult.temp,
        OWNER_CODE,
        SEASON_ENCODED: season_encoded,
        DISCOVERY_DOY_sin,
        DISCOVERY_DOY_cos,
        FIRE_DURATION_MINUTES,
        COUNTY_ENCODED: county_encoded
      })

      // Map encoded value to cause name using FireCauses
      let causeString = "No prediction"
      if (
        statCauseResult.stat_cause_descr !== undefined &&
        statCauseResult.stat_cause_descr !== null
      ) {
        const idx = Number(statCauseResult.stat_cause_descr)
        if (!isNaN(idx) && FireCauses[idx] && FireCauses[idx].cause) {
          causeString = FireCauses[idx].cause
        } else {
          causeString = String(statCauseResult.stat_cause_descr)
        }
      }
      predictedCause.value = causeString
    } catch (e) {
      predictedCause.value = "Prediction failed"
    }

    firePreventionStore.updateRecommendations([predictedCause.value])
    recommendations.value = firePreventionStore.currentRecommendations.length
      ? firePreventionStore.currentRecommendations[0].recommendations
      : []
    weatherResult.value = null
    emit('submit', { ...localForm, predictedCause: predictedCause.value })
  }
}

// Reset prediction results when lat/lng change (map click)
watch(
  () => [localForm.latitude, localForm.longitude],
  ([lat, lng], [oldLat, oldLng]) => {
    if (
      lat !== oldLat ||
      lng !== oldLng
    ) {
      predictedCause.value = null
      recommendations.value = []
      weatherResult.value = null
    }
  }
)
</script>
