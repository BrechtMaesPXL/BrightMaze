<script setup>
import { ref, watch } from "vue";
import { US_STATES } from "@/constants/states.js";
import { userSafeStore } from "@/stores/safe.js";

const safeStore = userSafeStore();

const stateList = Object.values(US_STATES).map((s) => s.name);

function getAbbreviation(stateName) {
  const entry = Object.values(US_STATES).find((s) => s.name === stateName);
  return entry ? entry.id : null;
}

function getFullName(abbr) {
  return US_STATES[abbr]?.name || abbr;
}

// Start met de naam die in store zit
const selectedStateName = ref(getFullName(safeStore.CurrentState));

watch(selectedStateName, (newName) => {
  const abbr = getAbbreviation(newName);
  if (abbr && abbr !== safeStore.CurrentState) {
    safeStore.setCurrentState(abbr);
  }
});
</script>

<template>
  <v-container class="pa-4" style="max-width: 400px;">
    <v-combobox
      v-model="selectedStateName"
      :items="stateList"
      label="Select a State"
      variant="outlined"
      clearable
      dense
      hide-details
    />
  </v-container>
</template>

<style scoped>

</style>
