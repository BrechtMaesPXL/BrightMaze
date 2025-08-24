import { defineStore } from 'pinia'
import axios from 'axios'
import { BASE_URL } from '@/config/api.js'

export const useFiresStore = defineStore('fires', {
  state: () => ({
    firesData: [],
    fipsList: [],

    inputs: {
      latitude: null,
      longitude: null,
      discoveryDatetime: null,
      windSpeed: null,
      precipitation: null
    },
    result: null,
    loading: false,
    error: null
  }),

  actions: {
    async fetchFires(FIPS) {
      this.loading = true
      this.error = null
      try {
        const token = localStorage.getItem('token')
        if (!token) throw new Error('Not authorized')

        const response = await axios.get(
          `${BASE_URL}/wildfires/${FIPS}`,
          { headers: { Authorization: `Bearer ${token}` }}
        )
        this.firesData = response.data
      } catch (err) {
        this.error = 'Kon branddata niet ophalen.'
        console.error(err)
      } finally {
        this.loading = false
      }
    },

    async fetchAllFIPS() {
      this.loading = true
      this.error = null
      try {
        const token = localStorage.getItem('token')
        if (!token) throw new Error('Not authorized')

        const response = await axios.get(
          `${BASE_URL}/PIPS/All`,
          { headers: { Authorization: `Bearer ${token}` }}
        )
        this.fipsList = response.data
        return response.data
      } catch (err) {
        this.error = 'Kon FIPS data niet ophalen.'
        console.error(err)
        return []
      } finally {
        this.loading = false
      }
    },

    setInputs(payload) {
      this.inputs = { ...payload }
    },

    reset() {
      this.inputs = {
        latitude: null,
        longitude: null,
        discoveryDatetime: null,
        windSpeed: null,
        precipitation: null,
        temp: null,
      }
      this.result = null
      this.error = null
      this.loading = false

    },

    async predict() {
      this.loading = true
      this.error = null
      try {
        const token = localStorage.getItem('token')
        if (!token) throw new Error('Not authorized')

        const response = await axios.post(
          `${BASE_URL}/predict_fire`,
          this.inputs,
          { headers: { Authorization: `Bearer ${token}` }}
        )
        this.result = response.data
      } catch (err) {
        this.error = 'Voorspelling mislukt.'
        console.error(err)
      } finally {
        this.loading = false
      }
    }
  }
})
