<!-- CardComponent.vue -->
<template>
  <v-card class="card" max-width="344">
    <v-img
      :src="img"
      height="200px"
      cover
    ></v-img>
    <v-card-title>{{ title }}</v-card-title>
    <v-card-subtitle>{{ description }}</v-card-subtitle>
    <v-card-actions>
      <v-btn
        color="orange-lighten-2"
        @click="goToRoute"
        text
      >
        Explore
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script setup>
import { defineProps } from 'vue';
import router from '@/router/index.js';

const props = defineProps({
  route: {
    type: String,
    required: true,
  },
  img: {
    type: String,
    required: true,
  },
  title: {
    type: String,
    required: true,
  },
  description: {
    type: String,
    required: true,
  }
});

const goToRoute = () => {
  router.push(props.route);
};
</script>

<style scoped>
.card {
  width: 344px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  background-color: rgba(0, 0, 0, 0.75);
  color: #e68416;
}

.card-actions {
  display: flex;
  justify-content: center;
  padding: 10px;
}

.v-btn {
  background-color: #e68416;
  color: white;
}
</style>
