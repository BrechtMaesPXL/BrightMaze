<template>
  <v-slider
    v-model="value"
    class="time-line"
    color="white"
    track-color="grey"
    step="1"
    max="2015"
    min="1992"
    thumb-label="always"
    :disabled="loading"
    @update:modelValue="updateDate"
  />
</template>

<script setup>
import { ref, watch } from "vue";
import { useMapStore } from "@/stores/map";

const mapStore = useMapStore();
const value = ref(mapStore.selectedDate);
const loading = ref(false);

const updateDate = async (newValue) => {
  if (newValue === mapStore.selectedDate) return;
  console.log("Selected year:", newValue);
  loading.value = true;
  mapStore.selectedDate = newValue;
  console.log("mapStore.selectedDate:", mapStore.selectedDate);
  await mapStore.getLocationsFiresByTime();
  loading.value = false;
};

watch(
  () => mapStore.selectedDate,
  (newDate) => {
    value.value = newDate;
  }
);
</script>

<style scoped>
.time-line {
  margin: 0 125px !important;
}

.time-line .v-slider__thumb-label {
  color: white !important;
}

.time-line :deep(.v-slider-track__fill) {
  background: transparent !important;
}
</style>
