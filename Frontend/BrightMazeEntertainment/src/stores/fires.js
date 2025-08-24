import { defineStore } from 'pinia'
import axios from 'axios'
import { API_URL_LOG } from '@/config/api.js'

export const useFireStore = defineStore('fire', {
  state: () => ({
    firesData: [],
    total: 0,
    page: 1,
    pageSize: 15,
    isLoading: false,
    error: null,
    searchQuery: '',
  }),

  getters: {
    filteredFires: (state) => {
      // Client-side sorting (modify if your API does sorting)
      return [...state.firesData].sort((a, b) => b.year - a.year)
    },
  },

  actions: {
    async fetchFires(page = this.page, pageSize = this.pageSize) {
      this.isLoading = true
      this.error = null

      try {
        const token = localStorage.getItem('token');
        let baseUrl = API_URL_LOG || 'http://localhost:8000';
        if (baseUrl.endsWith('/')) baseUrl = baseUrl.slice(0, -1);
        if (baseUrl.includes('backend')) baseUrl = baseUrl.replace('backend', 'localhost');

        const response = await axios.get(`${baseUrl}/wildfires`, {
          params: { page, page_size: pageSize },
          headers: {
            Authorization: token ? `Bearer ${token}` : ''
          }
        })

        this.firesData = response.data.items
        this.total     = response.data.total
        this.page      = response.data.page
        this.pageSize  = response.data.page_size

      } catch (err) {
        this.error = 'Failed to fetch wildfires'
        // Log more details for debugging
        if (err.response) {
          console.error('API error:', err.response.status, err.response.data)
        } else {
          console.error('API error:', err)
        }
      } finally {
        this.isLoading = false
      }
    },

    setSearchQuery(query) {
      this.searchQuery = query
      // Refresh data when search changes
      this.fetchFires(1, this.pageSize)
    },

    setPageSize(newSize) {
      this.pageSize = newSize;
      this.page = 1;
      this.fetchFires();
    },

    setPage(newPage) {
      this.page = Math.max(1, Math.min(newPage, Math.ceil(this.total / this.pageSize)));
      this.fetchFires();
    }
  }
})
