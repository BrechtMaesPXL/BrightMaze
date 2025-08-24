import { defineStore } from 'pinia'
import axios from 'axios'
import { ref } from 'vue'

export const useAverageFireSizeStore = defineStore('averageFireSize', {
  state: () => ({
    averagePerYear: {},
    yearDetails: null
  }),

  actions: {
    async fetchAverageFireSize() {
      try {
        const token = localStorage.getItem('token');
        const response = await axios.get('http://localhost:8000/api/average-fire-size-per-year', {
          headers: {
            Authorization: `Bearer ${token}`,
          }
        })
        this.averagePerYear = response.data.average_fire_size_per_year
      } catch (error) {
        console.error('Fout bij ophalen data:', error)
      }
    },

    async fetchYearDetails(year) {
      try {
        const token = localStorage.getItem('token');
        const response = await axios.get(`http://localhost:8000/api/wildfire-details/${year}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          }
        })
        this.yearDetails = response.data
      } catch (error) {
        console.error(`Fout bij ophalen details voor jaar ${year}:`, error)
        this.yearDetails = { error: `Geen gegevens beschikbaar voor jaar ${year}` }
      }
    }
  },

  getters: {
    chartData: (state) => {
      const years = Object.keys(state.averagePerYear)
      const values = Object.values(state.averagePerYear)

      return {
        labels: years,
        datasets: [{
          label: 'Fire Size',
          data: values,
          fill: false,
          borderColor: 'rgba(255, 165, 0, 1)',
          backgroundColor: 'rgba(255, 165, 0, 0.6)',
          tension: 0.1
        }]
      }
    }
  }
})
