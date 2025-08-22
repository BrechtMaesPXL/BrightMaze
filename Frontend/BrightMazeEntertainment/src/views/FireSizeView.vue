<template>
  <HomeButtonComponent />
  <div class="fire-size-graph-view">
    <h2>Top 10 Largest Wildfires</h2>
    <BarGraphFireSizeComponent />
  </div>
</template>

<script setup lang="ts">
import BarGraphFireSizeComponent from '@/components/Graphs/BarGraphFireSizeComponent.vue'
import HomeButtonComponent from '@/components/Menu/HomeButtonComponent.vue'
</script>

<style scoped>


.fire-size-graph-view {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px;
  font-family: Arial, sans-serif;
}

.fire-size-graph-view h2 {
  text-align: center;
  margin-bottom: 40px;
}
</style>
