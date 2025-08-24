import { defineStore } from "pinia"
import { FireCauses } from "@/constants/FireCauses.js"

export const useFirePreventionStore = defineStore("firePrevention", {
  state: () => ({
    causes: FireCauses,
    currentRecommendations: [],
  }),

  actions: {
    updateRecommendations(selectedCauses) {
      this.currentRecommendations = []

      if (!Array.isArray(selectedCauses)) return

      selectedCauses.forEach((causeName) => {
        const cause = this.causes.find((c) => c.cause === causeName)
        if (cause) {
          this.currentRecommendations.push({
            cause: cause.cause,
            recommendations: cause.recommendations,
          })
        } else {
          this.currentRecommendations.push({
            cause: causeName,
            recommendations: ["No specific prevention tips available for this cause."],
          })
        }
      })
    },
  },
})
