import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'

export const useBiggestFireSizeStore = defineStore('biggestFireSizeStore', () => {
  const wildfireData = ref([])

  async function fetchWildfireData() {
    console.log('[CHART] Fetching wildfire data...')
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get('http://localhost:8000/api/top-10-wildfires', {
        headers: {
          Authorization: `Bearer ${token}`,
        }
      })
      console.log('[FETCH] Status:', response.status)

      const json = response.data
      console.log('[FETCH] Response JSON:', json)

      if (json.top_10_wildfires) {
        wildfireData.value = json.top_10_wildfires.map(item => {
          console.log('[ITEM]', item)
          return {
            OBJECTID: item.OBJECTID || null,
            FIRE_NAME: item.FIRE_NAME_x || 'Unknown',
            STATE: item.STATE || 'Unknown',
            FIRE_SIZE: item.FIRE_SIZE || 0,
            CONT_DATE: item.CONT_DATE || 'Unknown',
            DISCOVERY_DATE: item.DISCOVERY_DATE || 'Unknown',
            STAT_CAUSE_DESCR: item.STAT_CAUSE_DESCR || 'Unknown',
            LATITUDE: item.LATITUDE || 0,
            LONGITUDE: item.LONGITUDE || 0,
            FIRE_DURATION: item.FIRE_DURATION || 0
          }
        })
        console.log('[PROCESSED DATA]', wildfireData.value)
      } else {
        console.warn('[FETCH] No top_10_wildfires in response')
        wildfireData.value = []
      }
    } catch (error) {
      console.error('[FETCH ERROR]', error)
      wildfireData.value = []
    }
  }

  return {
    wildfireData,
    fetchWildfireData,
  }
})
