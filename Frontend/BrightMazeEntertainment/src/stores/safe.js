import { defineStore } from "pinia";
import axios from "axios";
import { US_STATES } from "@/constants/states.js";

export const userSafeStore = defineStore("safe", {
  state: () => ({
    FireCausesString: [],
    FireCausesInt: [],
    CurrentState: "CA", // default
    states: Object.values(US_STATES),
  }),

  actions: {
    async getFireCauses() {
      if (!this.CurrentState) return;

      try {
        const token = localStorage.getItem('token');
        const response = await axios.get(
          `http://localhost:8000/api/top-5-causes?state=${this.CurrentState}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            }
          }
        );
        const data = response.data;

        if (data.error) {
          console.error("API error:", data.error);
          this.FireCausesString = [];
          this.FireCausesInt = [];
          return;
        }

        this.FireCausesString = Object.keys(data);
        this.FireCausesInt = Object.values(data);
      } catch (error) {
        console.error("Fout bij ophalen brandoorzaken:", error);
        this.FireCausesString = [];
        this.FireCausesInt = [];
      }
    },

    async setCurrentState(stateId) {
      this.CurrentState = stateId;
      await this.getFireCauses();
    },
  },
});
