import axios from "axios";
import { countyToEncoded } from "@/constants/counties.js";

// Get day of year from date string
export function getDiscoveryDOY(dateStr) {
  if (!dateStr) return null;
  const date = new Date(dateStr);
  const start = new Date(date.getFullYear(), 0, 0);
  const diff = date - start;
  const oneDay = 1000 * 60 * 60 * 24;
  return Math.floor(diff / oneDay);
}

// Encode season (0=winter, 1=spring, 2=summer, 3=fall)
export function encodeSeason(month) {
  if ([12, 1, 2].includes(month)) return 0;
  if ([3, 4, 5].includes(month)) return 1;
  if ([6, 7, 8].includes(month)) return 2;
  if ([9, 10, 11].includes(month)) return 3;
  return 0;
}

// Get sin/cos of DOY
export function getSinCosDOY(doy) {
  const sin = Math.sin((2 * Math.PI * doy) / 365);
  const cos = Math.cos((2 * Math.PI * doy) / 365);
  return { sin, cos };
}

// Precipitation category to number
export function prcpCategoryToNum(category) {
  const map = { "no rain": 0, "low": 1, "medium": 2, "high": 3 };
  return map[category] ?? 0;
}

// Helper to get API URL from .env and fallback to localhost
function getApiUrl() {
  // Remove trailing slash if present
  let url = import.meta.env.VITE_API_URL || 'http://localhost:8000';
  if (url.endsWith('/')) url = url.slice(0, -1);
  // Fallback: if url contains 'backend', replace with 'localhost'
  if (url.includes('backend')) url = url.replace('backend', 'localhost');
  return url;
}

// Helper to get Bearer token headers
function getAuthHeaders() {
  const token = localStorage.getItem('token');
  return token ? { Authorization: `Bearer ${token}` } : {};
}

// --- Prediction endpoints ---
export async function predictTemp({ LATITUDE, LONGITUDE, DISCOVERY_DOY }) {
  const API_URL = getApiUrl();
  const { data } = await axios.post(
    `${API_URL}/model/temp`,
    { LATITUDE, LONGITUDE, DISCOVERY_DOY },
    { headers: getAuthHeaders() }
  );
  // temperature afronden op 1 decimaal
  if (typeof data.temp === 'number') {
    data.temp = Math.round(data.temp * 10) / 10;
  }
  return data;
}

export async function predictPrcp({ LATITUDE, LONGITUDE, DISCOVERY_DOY }) {
  const API_URL = getApiUrl();
  const { data } = await axios.post(
    `${API_URL}/model/prcp`,
    { LATITUDE, LONGITUDE, DISCOVERY_DOY },
    { headers: getAuthHeaders() }
  );
  return data;
}

export async function predictWspd({ LATITUDE, LONGITUDE, DISCOVERY_DOY }) {
  const API_URL = getApiUrl();
  const { data } = await axios.post(
    `${API_URL}/model/wspd`,
    { LATITUDE, LONGITUDE, DISCOVERY_DOY },
    { headers: getAuthHeaders() }
  );
  // windspeed afronden op 0 decimalen
  if (typeof data.wspd === 'number') {
    data.wspd = Math.round(data.wspd);
  }
  return data;
}

export async function predictStatCauseDescr({ latitude, longitude, date, county }) {
  const API_URL = getApiUrl();
  const FIRE_YEAR = 2016;
  const OWNER_CODE = 14;
  const FIRE_DURATION_MINUTES = 8;

  // Prepare date features
  const dt = new Date(date);
  const month = dt.getMonth() + 1;
  const season_encoded = encodeSeason(month);
  const discovery_doy = getDiscoveryDOY(date);
  const { sin: DISCOVERY_DOY_sin, cos: DISCOVERY_DOY_cos } = getSinCosDOY(discovery_doy);

  // Predict weather features
  const wspdRes = await predictWspd({ LATITUDE: latitude, LONGITUDE: longitude, DISCOVERY_DOY: discovery_doy });
  const prcpRes = await predictPrcp({ LATITUDE: latitude, LONGITUDE: longitude, DISCOVERY_DOY: discovery_doy });
  const tempRes = await predictTemp({ LATITUDE: latitude, LONGITUDE: longitude, DISCOVERY_DOY: discovery_doy });

  // prcpRes.prcp is a string, map to number
  const prcp_num = prcpCategoryToNum(prcpRes.prcp);

  // County encoding
  let county_encoded = 0;
  if (county && countyToEncoded && countyToEncoded[county]) {
    county_encoded = countyToEncoded[county];
  }

  // Prepare input for stat_cause_descr endpoint
  const payload = {
    LATITUDE: latitude,
    LONGITUDE: longitude,
    FIRE_YEAR,
    wspd_mean_0: wspdRes.wspd,
    prcp_sum_0: prcp_num,
    temp_mean_0: tempRes.temp,
    OWNER_CODE,
    SEASON_ENCODED: season_encoded,
    DISCOVERY_DOY_sin,
    DISCOVERY_DOY_cos,
    FIRE_DURATION_MINUTES,
    COUNTY_ENCODED: county_encoded,
  };

  const { data } = await axios.post(
    `${API_URL}/model/stat_cause_descr`,
    payload,
    { headers: getAuthHeaders() }
  );
  return data;
}

/**
 * Predict fire size class.
 * @param {Object} params - { latitude, longitude, date, county }
 * @returns {Promise<{fire_size_class: string}>}
 */
export async function predictFireSizeClass({ latitude, longitude, date, county }) {
  const API_URL = getApiUrl();
  const FIRE_YEAR = 2016;
  const OWNER_CODE = 14;

  // Prepare date features
  const dt = new Date(date);
  const discovery_doy = getDiscoveryDOY(date);
  const { sin: DISCOVERY_DOY_sin, cos: DISCOVERY_DOY_cos } = getSinCosDOY(discovery_doy);

  // County encoding
  let county_encoded = 0;
  if (county && countyToEncoded && countyToEncoded[county]) {
    county_encoded = countyToEncoded[county];
  }

  // Predict weather features
  const wspdRes = await predictWspd({ LATITUDE: latitude, LONGITUDE: longitude, DISCOVERY_DOY: discovery_doy });
  const prcpRes = await predictPrcp({ LATITUDE: latitude, LONGITUDE: longitude, DISCOVERY_DOY: discovery_doy });
  const tempRes = await predictTemp({ LATITUDE: latitude, LONGITUDE: longitude, DISCOVERY_DOY: discovery_doy });
  const prcp_num = prcpCategoryToNum(prcpRes.prcp);

  const FIRE_DURATION_MINUTES = Math.floor(Math.random() * (1000 - 100 + 1)) + 100;

  // Prepare payload
  const payload = {
    LATITUDE: latitude,
    LONGITUDE: longitude,
    FIRE_YEAR,
    DISCOVERY_DOY: discovery_doy,
    OWNER_CODE,
    COUNTY_ENCODED: county_encoded,
    DISCOVERY_DOY_sin,
    DISCOVERY_DOY_cos,
    temp_mean_0: tempRes.temp,
    wspd_mean_0: wspdRes.wspd,
    prcp_sum_0: prcp_num,
    FIRE_DURATION_MINUTES,
  };

  const { data } = await axios.post(
    `${API_URL}/model/fire_size_class`,
    payload,
    { headers: getAuthHeaders() }
  );
  return data;
}

/**
 * Get fire safety tips based on weather scenario.
 * @param {Object} weather - { temperature, wind, prcp }
 * @returns {string[]} Array of tips
 */
export function getWeatherSafetyTips({ temperature, wind, prcp }) {
  const tips = []
  // Scenario 1: Very hot, dry, windy
  if (
    typeof temperature === 'number' && temperature >= 32 &&
    (prcp === 'no rain' || prcp === 'low') &&
    typeof wind === 'number' && wind >= 20
  ) {
    tips.push('🔥 Extreme fire risk: Avoid all open flames, BBQs, and campfires. Do not use fireworks. Smoking is strongly discouraged.');
    tips.push('💨 Strong wind: Sparks can travel far. Never burn debris or light fires.');
    tips.push('🚭 Never throw cigarettes on the ground.');
    return tips
  }
  // Scenario 2: Hot, dry, not much wind
  if (
    typeof temperature === 'number' && temperature >= 28 &&
    (prcp === 'no rain' || prcp === 'low') &&
    (typeof wind !== 'number' || wind < 20)
  ) {
    tips.push('🔥 High fire risk: Be extra careful with BBQs and campfires. Avoid burning debris.');
    tips.push('🚭 Never throw cigarettes on the ground.');
    return tips
  }
  // Scenario 3: Moderate temp, dry, windy
  if (
    typeof temperature === 'number' && temperature >= 18 && temperature < 28 &&
    (prcp === 'no rain' || prcp === 'low') &&
    typeof wind === 'number' && wind >= 20
  ) {
    tips.push('💨 Windy and dry: Avoid burning debris or lighting fires. Sparks can spread quickly.');
    return tips
  }
  // Scenario 4: Rainy or wet
  if (prcp === 'medium' || prcp === 'high') {
    tips.push('🌧️ Wet conditions: Fire risk is lower, but always follow local fire safety guidelines.');
    return tips
  }
  // Scenario 5: Cold and dry
  if (
    typeof temperature === 'number' && temperature < 10 &&
    (prcp === 'no rain' || prcp === 'low')
  ) {
    tips.push('🥶 Cold and dry: Fire risk is lower, but dry vegetation can still catch fire. Use caution with heaters and open flames.');
    return tips
  }
  // Scenario 6: Mild temperature, low wind, no rain
  if (
    typeof temperature === 'number' && temperature >= 10 && temperature < 18 &&
    (prcp === 'no rain' || prcp === 'low') &&
    (typeof wind !== 'number' || wind < 10)
  ) {
    tips.push('🌤️ Mild and dry: Fire risk is moderate. Always extinguish campfires completely and never leave them unattended.');
    return tips
  }
  // Scenario 7: Very windy, any temperature, any precipitation
  if (typeof wind === 'number' && wind >= 30) {
    tips.push('💨 Very windy: Avoid any outdoor burning or open flames. Wind can spread fire rapidly.');
    return tips
  }
  // Scenario 8: Cool, wet, and calm
  if (
    typeof temperature === 'number' && temperature < 15 &&
    (prcp === 'medium' || prcp === 'high') &&
    (typeof wind !== 'number' || wind < 10)
  ) {
    tips.push('🌧️ Cool and wet: Fire risk is low. Still, follow local fire safety rules.');
    return tips
  }
  // Scenario 9: Hot and wet
  if (
    typeof temperature === 'number' && temperature >= 28 &&
    (prcp === 'medium' || prcp === 'high')
  ) {
    tips.push('🌦️ Hot and wet: Fire risk is reduced by rain, but be careful during dry spells.');
    return tips
  }
  // Scenario 10: Default fallback
  tips.push('Always follow local fire safety guidelines. Never leave fires unattended and dispose of cigarettes properly.');
  return tips
}
