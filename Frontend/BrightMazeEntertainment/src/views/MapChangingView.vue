<script setup>

import HomeButtonComponent from "@/components/Menu/HomeButtonComponent.vue";
import FireScatterPlot from "@/components/Maps/FireScatterPlot.vue";
import router from "@/router/index.js";
import MapLiveComponent from "@/components/Maps/MapLiveComponent.vue";
import TempMapComponent from "@/components/Maps/TempMapComponent.vue";
import ErrorPage from "@/components/Menu/ErrorPage.vue";
import FireSizePrediction from '@/components/Size/FireSizePrediction.vue'

const Component = router.currentRoute.value.params.Component

console.log(Component)

</script>

<template>

  <v-row class="top-bar" >
    <v-col >
      <HomeButtonComponent/>
    </v-col>
    <v-col   class="text-fix ">
      <h2> {{ Component }} map</h2>

    </v-col>
  </v-row>
  <FireScatterPlot  v-if="Component === 'scatterplot'" />
  <TempMapComponent  v-else-if="Component === 'Temperature'" />
  <FireSizePrediction v-else-if="Component === 'FireSizePrediction'" />

</template>

<style scoped>
.text-fix{
  color: #e78515;
  text-align: end;
  margin-right: 50px;
  margin-top: 10px;
}
.top-bar {
  position: fixed;
  top: 25px;
  left: 0;
  width: 30%;
  padding: 10px 10px;
  z-index: 10;
  background: rgba(0, 0, 0, 0.75);
}
</style>
