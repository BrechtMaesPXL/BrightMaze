<script setup>
import { ref } from "vue";

import HomeComponent from "@/components/HomeComponent.vue";
import HeaderComponent from "@/components/HeaderComponent.vue";
import CordiLocationComponent from "@/components/CordiLocationComponent.vue";
import RouteSettingsComponent from "@/components/RouteSettingsComponent.vue";
import VoiceSettingsComponent from "@/components/VoiceSettingsComponent.vue";

const cordiRef = ref(null);
const routeRef = ref(null);
const voiceRef = ref(null);

function resetAll() {
  cordiRef.value?.reset?.();
  routeRef.value?.reset?.();
  voiceRef.value?.reset?.();
}
</script>


<template>
  <HomeComponent />
  <HeaderComponent />
  <div class="reset-button-wrapper">
    <button @click="resetAll" class="reset-button">Reset settings</button>
  </div>
   <div class="card-container">
    <CordiLocationComponent ref="cordiRef" />
    <RouteSettingsComponent ref="routeRef" />
    <VoiceSettingsComponent ref="voiceRef" />
  </div>
</template>

<style scoped>
.card-container {
  display: flex;
  flex-direction: row;
  gap: 2rem;
  max-width: 1200px;
  min-height: calc(100vh - 200px); /* Adjust based on your header height */
  margin: 0 auto;
  padding: 0 1rem;
  justify-content: center;
  align-items: center;
}
.reset-button-wrapper {
  display: flex;
  justify-content: center;
  margin: 2rem 0;
}
.reset-button {
  background-color: #e53e3e;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.reset-button:hover {
  background-color: #c53030;
}

</style>
