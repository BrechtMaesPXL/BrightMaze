<template>
  <v-container class="checklist-wrapper pa-4">
    <v-card elevation="2" class="pa-4">
      <v-card-title class="headline text-center">
        Top 5 Fire Causes and Recommendations
      </v-card-title>
      <v-divider></v-divider>
      <v-list class="recommendations-list">
        <v-list-item
          v-for="item in firePreventionStore.currentRecommendations"
          :key="item.cause"
          class="recommendation-item"
        >
          <v-list-item-content>
            <v-list-item-title class="font-weight-bold">
              {{ item.cause }}
            </v-list-item-title>
            <v-list-item-subtitle>
              <ul>
                <li
                  v-for="rec in item.recommendations"
                  :key="rec"
                  class="recommendation-text"
                >
                  {{ rec }}
                </li>
              </ul>
            </v-list-item-subtitle>
          </v-list-item-content>
        </v-list-item>
      </v-list>
    </v-card>
  </v-container>
</template>

<script setup>
import { watch } from "vue";
import { userSafeStore } from "@/stores/safe.js";
import { useFirePreventionStore } from "@/stores/firePrevention.js"

const safeStore = userSafeStore();
const firePreventionStore = useFirePreventionStore();

watch(
  () => safeStore.FireCausesString,
  (newCauses) => {
    firePreventionStore.updateRecommendations(newCauses);
  },
  { immediate: true }
);
</script>

<style scoped>
.checklist-wrapper {
  max-width: 800px;
  margin: 0 auto;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.recommendations-list {
  padding: 0;
  margin-top: 20px;
  width: 100%;
}

.recommendation-item {
  padding: 18px 24px;
  background: #f1f3f5;
  border-radius: 8px;
  margin-bottom: 12px;
  transition: box-shadow 0.3s ease;
}

.recommendation-item:hover {
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

ul {
  list-style-type: disc;
  padding-left: 1rem;
  margin: 0;
}

.recommendation-text {
  white-space: normal;
  word-break: break-word;
  overflow: visible;
  text-overflow: unset;
  display: list-item;
  line-height: 1.6;
}
</style>
