// stores/user.js
import axios from 'axios'
import { defineStore } from 'pinia'

const API_URL = 'http://127.0.0.1:8000'

export const useUserStore = defineStore('user', {
  state: () => ({
    id: null,
    username: 'unknown',
    email: '',
    token: localStorage.getItem('token') || null,
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
  },

  actions: {
    async login({ username, password }) {
      const form = new URLSearchParams()
      form.append('username', username)
      form.append('password', password)

      this.username = username

      const response = await axios.post(`${API_URL}/token`, form, {
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
      })
      const { access_token, token_type } = response.data

      this.token = access_token
      localStorage.setItem('token', access_token)
    },

    logout() {
      this.token = null
      localStorage.removeItem('token')
    },

    async fetchUser(userId) {
      try {
        if (!this.token) {
          // Not logged in, reset state
          this.id = 0;
          this.username = 'Username';
          this.email = 'email@email.com';
          return;
        }
        const response = await axios.get(
          `${API_URL}/users/${userId}`,
          {
            headers: {
              Authorization: `Bearer ${this.token}`,
            }
          }
        );
        const user = response.data;
        this.id = user.id;
        this.username = user.username;
        this.email = user.email;
        // Optionally set token if returned
      } catch (error) {
        // Reset to default values on error
        this.id = 0;
        this.username = 'Username';
        this.email = 'email@email.com';
        // Optionally clear token if unauthorized
        if (error.response && error.response.status === 401) {
          this.token = null;
          localStorage.removeItem('token');
        }
      }
    }
  }
})
