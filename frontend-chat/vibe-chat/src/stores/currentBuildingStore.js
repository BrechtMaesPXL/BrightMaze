import { defineStore } from 'pinia'
import axios from 'axios'

export const useLocationStore = defineStore('location', {
  state: () => ({
    currentBuilding: null,
    isLoading: false,
  }),

  actions: {
    async fetchCurrentBuilding() {
      const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || '').replace(/\/+$/, '')
      this.isLoading = true
      try {
        const response = await axios.get(`${API_BASE_URL}/admin/api/settings/current-location`, {
          timeout: 5000
        })
        this.currentBuilding = response.data || 'Onbekend'
        console.log('Locatie succesvol opgehaald:', this.currentBuilding)
      } catch (error) {
        console.error('Fout bij ophalen locatie:', error)
        this.currentBuilding = 'Onbekend'
      } finally {
        this.isLoading = false
      }
    }
  }
})
