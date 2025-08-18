<template>
  <v-btn
    class="home-btn"
    icon
    variant="plain"
    @click="goHome"
  >
    <v-icon icon="mdi-home" />
  </v-btn>
</template>

<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

const goHome = () => {
  router.push(`/home`);
};
</script>

<style scoped>
.home-btn {
  position: fixed !important;
  top: 20px !important;
  left: 20px !important;
  z-index: 999 !important;
  cursor: pointer !important;
  background: white;
  color: black;
}
</style>
