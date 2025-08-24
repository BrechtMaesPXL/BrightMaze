<script setup>
import router from "@/router/index.js";

const goHome = () => {
  router.push(`/home`);
};
</script>

<template>
    <v-btn class="go-home-btn" @click="goHome">Home</v-btn>
</template>

<style scoped>
.go-home-btn {
  margin-top: 10px !important;
  margin-left: 50px !important;
  background-color: #e98616 !important;
  color: white !important;
  z-index: 15 !important;
}
</style>
