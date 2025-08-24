<script setup>
import MapLiveComponent from "@/components/Maps/MapLiveComponent.vue";
import MapVoorspellingComponent from "@/components/Maps/MapVoorspellingComponent.vue";
import TimeLineComponent from "@/components/TimeLineComponent.vue";
import { ref } from "vue";
import { useMapStore } from "@/stores/map";
import HomeButtonComponent from "@/components/Menu/HomeButtonComponent.vue";

const mapStore = useMapStore();

const items = [
  { title: "Live", disabled: false, value: "live" },
  { title: "Predictions", disabled: false, value: "voorspelling" },
];

const selected = ref("live");

const selectComponent = (value) => {
  selected.value = value;
};

const handleStateSelected = (state) => {
  const index = mapStore.locations.indexOf(state);
  const count = mapStore.z[index] || 0;
  console.log(`Wildfires in ${state} (${mapStore.selectedDate}): ${count}`);
};
</script>

<template>
  <v-container fluid class="pa-0 ma-0">
    <v-row class="top-bar" align="center">
      <v-col cols="4">
        <HomeButtonComponent />
      </v-col>
      <v-col cols="4" class="d-flex justify-center">
        <v-breadcrumbs :items="items" class="breadcrumbs">
          <template v-slot:divider>
            <span class="breadcrumb-divider">/</span>
          </template>
          <template v-slot:title="{ item }">
            <span
              @click="selectComponent(item.value)"
              class="breadcrumb-item"
              :class="{ active: selected === item.value }"
            >
              {{ item.title.toUpperCase() }}
            </span>
          </template>
        </v-breadcrumbs>
      </v-col>
      <v-col cols="4"></v-col>
    </v-row>

    <div class="component-container">
      <MapLiveComponent
        v-if="selected === 'live'"
        :max-value="11000"
        @stateSelected="handleStateSelected"
      />
      <MapVoorspellingComponent v-else-if="selected === 'voorspelling'" />
    </div>

    <v-footer class="custom-footer">
      <div class="flex-1-0-100 text-center mt-2">
        <TimeLineComponent v-if="selected === 'live'" />
        <span v-else>
          {{ mapStore.formattedDate }} (Total: {{ mapStore.z.reduce((sum, count) => sum + count, 0) }} wildfires)
        </span>
      </div>
    </v-footer>
  </v-container>
</template>

<style scoped>
html,
body {
  overflow: hidden;
  margin: 0;
  padding: 0;
  background: #000 !important;
}

.v-container {
  background: #000 !important;
}

.top-bar {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  padding: 10px 20px;
  z-index: 10;
  background: rgba(0, 0, 0, 0.1);
}

.breadcrumbs {
  margin-left: 100px;
  display: flex;
  align-items: center;
}

.breadcrumb-divider {
  color: white;
  padding: 0 5px;
}

.breadcrumb-item {
  cursor: pointer;
  padding: 5px 10px;
  color: white;
  font-weight: normal;
  transition: all 0.3s;
}

.breadcrumb-item:hover {
  text-decoration: underline;
}

.breadcrumb-item.active {
  font-weight: bold;
  border-bottom: 2px solid white;
}

.component-container {
  margin: 70px 0 0 0;
  padding: 0;
  height: calc(100vh - 140px);
  background: #000 !important;
  display: flex;
  justify-content: center;
  align-items: center;
}

.custom-footer {
  color: white;
  position: fixed;
  bottom: 40px;
  left: 0;
  width: 100%;
  background: rgba(0, 0, 0, 0);
  text-align: center;
  padding: 10px 0;
  margin: 0;
  z-index: 2;
}
</style>
