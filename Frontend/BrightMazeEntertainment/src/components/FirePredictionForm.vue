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
      <input v-model="localForm.county" type="text" placeholder="County" readonly style="background:#222;color:#ff9800;" />
    </label>
    <label v-if="mode !== 'fire-size'">
      Fire Size (ha):
      <input v-model.number="localForm.fire_size" type="number" min="0" step="any" placeholder="Fire size in hectares" required />
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
      {{ mode === 'weather' ? 'Predict Weather' : mode === 'fire-size' ? 'Predict Fire Size' : 'Predict Cause' }}
    </button>
    <div v-if="mode === 'cause' && predictedCause" class="prediction-result">
      <strong>Predicted Cause:</strong> {{ predictedCause }}
      <div v-if="recommendations.length" class="prevention-tips">
        <strong>How to prevent:</strong>
        <ul style="list-style-type: none; padding-left: 0;">
          <li v-for="rec in recommendations" :key="rec">{{ rec }}</li>
        </ul>
      </div>
    </div>
    <div v-if="mode === 'weather' && weatherResult" class="prediction-result">
      <strong>Predicted Weather:</strong>
      <ul style="list-style-type: none; padding-left: 0;">
        <li>Temperature: {{ weatherResult.temperature }} °C</li>
        <li>Wind speed: {{ weatherResult.wind }} km/h</li>
        <li>Precipitation: {{ weatherResult.prcp }}</li>
      </ul>
    </div>
    <div v-if="mode === 'fire-size' && fireSizeResult !== null" class="prediction-result">
      <strong v-if="fireSizeResult !== 'Error'">Predicted Fire Size Class:</strong>
      <span v-if="fireSizeResult !== 'Error'">{{ fireSizeResult }}</span>
      <span v-else style="color:red;">Prediction failed. Please check your input.</span>
      <div v-if="fireSizeValue !== null && fireSizeValue !== undefined && fireSizeValue !== 'Error'" style="margin-top: 0.7rem;">
        <strong>Predicted Fire Size (acres):</strong> {{ fireSizeValue }}
      </div>
    </div>
    <div class="fire-size-legenda" v-if="mode === 'fire-size'">
      <strong>Legenda (Fire Size Class):</strong>
      <ul class="fire-size-legenda-list" style="margin-top: 0.7rem; margin-bottom: 0.7rem; list-style: none; padding-left: 0;">
        <div><strong>low</strong> 0 – {{ fireSizeClassInfo.q1 }} acres</div>
        <div><strong>medium</strong> {{ fireSizeClassInfo.q1 }} – {{ fireSizeClassInfo.q2 }} acres</div>
        <div><strong>high</strong> {{ fireSizeClassInfo.q2 }} – {{ fireSizeClassInfo.q3 }} acres</div>
        <div><strong>extreme</strong> &gt; {{ fireSizeClassInfo.q3 }} acres</div>
      </ul>
    </div>
  </form>
</template>

<script setup>
import { reactive, watch, ref } from 'vue'
import axios from 'axios'
import { useFirePreventionStore } from '@/stores/firePrevention.js'
import * as predictionStore from '@/stores/predictionStore.js' // Import all as object
import { countyToEncoded } from '@/constants/counties.js'
import { FireCauses } from '@/constants/FireCauses.js'

const props = defineProps({
  form: {
    type: Object,
    required: true
  },
  mode: {
    type: String,
    default: 'cause' // 'cause', 'weather', or 'fire-size'
  }
})
const emit = defineEmits(['update:form', 'submit', 'fire-circle'])

const localForm = reactive({ ...props.form, fire_size: props.form.fire_size ?? null })
const predictedCause = ref(null)
const firePreventionStore = useFirePreventionStore()
const recommendations = ref([])
const weatherResult = ref(null)
const fireSizeResult = ref(null)
const fireSizeValue = ref(null)

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8000'

// Vul deze waarden in met de exacte grenzen uit je Python-omgeving/dataset!
const fireSizeClassInfo = {
  q1: 10,
  q2: 500,
  q3: 1000,
  low:    { range: [0, 10],    mean: 10,    draw: 500 },     // 500 acres
  medium: { range: [10, 500],  mean: 500,   draw: 3000 },    // 3000 acres
  high:   { range: [500, 1000],mean: 1000,  draw: 10000 },   // 10,000 acres
  extreme:{ range: [1000, 2000], mean: 2000, draw: 50000 }   // 50,000 acres (much bigger)
}

// Helper om straal in meters te berekenen op basis van acres
function acresToRadiusMeters(acres) {
  // 1 acre = 4046.86 m², oppervlakte cirkel: πr² => r = sqrt(area/π)
  const areaM2 = acres * 4046.86
  return Math.sqrt(areaM2 / Math.PI)
}

// Emit de straal en locatie naar de parent als fire-size prediction gedaan is
function emitFireCircle() {
  if (
    fireSizeResult.value !== null &&
    fireSizeResult.value !== 'Error' &&
    fireSizeClassInfo[fireSizeResult.value] &&
    localForm.latitude &&
    localForm.longitude
  ) {
    // Use the 'draw' property for visual circle size
    const acres = fireSizeClassInfo[fireSizeResult.value].draw
    const radiusMeters = acresToRadiusMeters(acres)
    emit('fire-circle', {
      lat: Number(localForm.latitude),
      lng: Number(localForm.longitude),
      radius: radiusMeters
    })
  }
}

// Automatically fetch county when latitude/longitude change
watch(
  () => [localForm.latitude, localForm.longitude],
  async ([lat, lng], [oldLat, oldLng]) => {
    if (lat !== oldLat || lng !== oldLng) {
      predictedCause.value = null
      recommendations.value = []
      weatherResult.value = null
      fireSizeResult.value = null
    }
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
  const { latitude, longitude, date, county, fire_size } = localForm
  if (!latitude || !longitude || !date) {
    predictedCause.value = null
    recommendations.value = []
    weatherResult.value = null
    fireSizeResult.value = null
    return
  }
  const discovery_doy = predictionStore.getDiscoveryDOY(date)
  if (discovery_doy === null) {
    predictedCause.value = "Invalid date"
    recommendations.value = []
    weatherResult.value = null
    fireSizeResult.value = null
    return
  }

  if (props.mode === 'weather') {
    try {
      const [tempRes, wspdRes, prcpRes] = await Promise.all([
        predictionStore.predictTemp({ LATITUDE: parseFloat(latitude), LONGITUDE: parseFloat(longitude), DISCOVERY_DOY: discovery_doy }),
        predictionStore.predictWspd({ LATITUDE: parseFloat(latitude), LONGITUDE: parseFloat(longitude), DISCOVERY_DOY: discovery_doy }),
        predictionStore.predictPrcp({ LATITUDE: parseFloat(latitude), LONGITUDE: parseFloat(longitude), DISCOVERY_DOY: discovery_doy })
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
  } else if (props.mode === 'fire-size') {
    fireSizeResult.value = null
    fireSizeValue.value = null
    try {
      const res = await predictionStore.predictFireSizeClass({
        latitude: parseFloat(latitude),
        longitude: parseFloat(longitude),
        date,
        county
      })
      fireSizeResult.value = res.fire_size_class
      fireSizeValue.value = res.fire_size ?? null
      emitFireCircle() // <-- emit circle info
    } catch (err) {
      fireSizeResult.value = 'Error'
      fireSizeValue.value = 'Error'
    }
    emit('submit', { ...localForm, fireSizeResult: fireSizeResult.value, fireSizeValue: fireSizeValue.value })
    return
  } else {
    // Fire cause prediction
    try {
      const [tempResult, prcpResult, wspdResult] = await Promise.all([
        predictionStore.predictTemp({
          LATITUDE: parseFloat(latitude),
          LONGITUDE: parseFloat(longitude),
          DISCOVERY_DOY: discovery_doy
        }),
        predictionStore.predictPrcp({
          LATITUDE: parseFloat(latitude),
          LONGITUDE: parseFloat(longitude),
          DISCOVERY_DOY: discovery_doy
        }),
        predictionStore.predictWspd({
          LATITUDE: parseFloat(latitude),
          LONGITUDE: parseFloat(longitude),
          DISCOVERY_DOY: discovery_doy
        })
      ])

      const dt = new Date(date)
      const FIRE_YEAR = 2016
      const OWNER_CODE = 5
      const FIRE_DURATION_MINUTES = 5
      const season_encoded = predictionStore.encodeSeason(dt.getMonth() + 1)
      const { sin: DISCOVERY_DOY_sin, cos: DISCOVERY_DOY_cos } = predictionStore.getSinCosDOY(discovery_doy)
      const prcp_num = predictionStore.prcpCategoryToNum(prcpResult.prcp)
      let county_encoded = 0
      if (county && countyToEncoded[county]) {
        county_encoded = countyToEncoded[county]
      }

      const statCauseResult = await predictionStore.predictStatCauseDescr({
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
</script>

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
.fire-size-legenda {
  margin-top: 1.5rem;
  margin-bottom: 1.5rem;
  background: #222;
  border: 1px solid #ff9800;
  border-radius: 0.7rem;
  padding: 0.5rem 0.5rem 0.5rem 0.5rem;
  color: #ff9800;
  font-size: 1.05em;
  max-width: 100%;
  word-break: break-word;
}
.fire-size-legenda-list div {
  margin-bottom: 0.3rem;
}
</style>
