import { defineStore } from "pinia";
import axios from "axios";
import { API_URL_LOG } from '@/config/api.js'

export const userLogsStore = defineStore("logs", {
  state: () => ({
    logs: []
  }),

  actions: {
    async getAllLogs() {
      try {
        const response = await axios.get(API_URL_LOG + 'all');

        this.logs = response.data;

        return this.logs;
      } catch (error) {
        console.error("Error fetching logs:", error);
        return error.response?.status || 500;
      }
    },
    async getLogById(id) {
      try {
        const response = await axios.get(API_URL_LOG  + id);
        return response.data;
      } catch (error) {
        console.error("Error fetching logs:", error);
        return error.response?.status || 500;
      }
    }
  }
});
