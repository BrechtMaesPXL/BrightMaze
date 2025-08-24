<script setup>
import HomeComponent from "@/components/HomeComponent.vue";
import HeaderComponent from "@/components/HeaderComponent.vue";
import ListLogsComponent from "@/components/ListLogsComponent.vue";
</script>

<template>
  <HomeComponent />
  <HeaderComponent />
  <div class="container">
    <ListLogsComponent />
  </div>
</template>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  height: 100vh;
}

.v-data-table {
  width: 80%;
  margin-top: 20px;
}
</style>
