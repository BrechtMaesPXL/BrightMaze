import axios from 'axios';
import { defineStore } from 'pinia';

const apiUrl = 'http://127.0.0.1:8000';

export const useMapStore = defineStore('map', {
  state: () => ({
    locations: [],
    z: [],
    currentDate: new Date(),
    selectedDate: 1992,
    error: null, // Added error state
  }),
  getters: {
    formattedDate: (state) => {
      return new Intl.DateTimeFormat('en-GB').format(new Date(state.selectedDate, 0, 1));
    },
  },
  actions: {
    async getAllLocation() {
      try {
        const token = localStorage.getItem('token');
        const response = await axios.get('http://127.0.0.1:8000/api/wildfires', {
          headers: {
            Authorization: `Bearer ${token}`,
          }
        });
        const data = response.data;
        this.locations = Object.keys(data);
        this.z = Object.values(data);
        console.log("Locations:", this.locations);
        console.log("Counts (z):", this.z);
        this.error = null; // Clear error on success
      } catch (error) {
        console.error("Error fetching wildfire data:", error);
        this.locations = [
          "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA",
          "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK",
          "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"
        ];
        this.z = this.locations.map(() => 0);
        this.error = "Could not fetch wildfire data. Please check your connection to the backend server.";
      }
    },

    async getLocationsFiresByTime() {
      try {
        const token = localStorage.getItem('token');
        const response = await axios.get(
          `${apiUrl}/api/wildfires-by-year/${this.selectedDate}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            }
          }
        );
        const { wildfires_by_state } = response.data;
        if (wildfires_by_state === undefined || wildfires_by_state === null) {
          throw new Error("No wildfire data for selected year.");
        }
        this.locations = [
          "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA",
          "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK",
          "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"
        ];
        this.z = this.locations.map(state => wildfires_by_state[state] || 0);
        console.log("Selected year:", this.selectedDate);
        console.log("Locations:", this.locations);
        console.log("Wildfire counts (z):", this.z);
        this.error = null; // Clear error on success
      } catch (error) {
        console.error("Error fetching wildfires by year:", error);
        this.locations = [
          "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA",
          "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK",
          "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"
        ];
        this.z = this.locations.map(() => 0);
        this.error = "Could not fetch wildfires by year. Please ensure the backend server is running or data exists for this year.";
      }
    },
  },
});
