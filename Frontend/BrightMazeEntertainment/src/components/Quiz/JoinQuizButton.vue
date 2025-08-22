<template>
    <div>
      <button @click="modalOpen = true" class="join-button">
        Join Quiz
      </button>
  
      <UsernameModal v-if="modalOpen" @start="modalOpen = false" />
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue'
  import UsernameModal from './UsernameModal.vue'
  
  const modalOpen = ref(false)
  </script>
  
  <style scoped>
  .join-button {
    background-color: #ff8c00;
    color: #000;
    padding: 12px 24px;
    border: none;
    border-radius: 8px;
    font-weight: bold;
    font-size: 1.1rem;
    cursor: pointer;
    transition: background-color 0.3s ease;
  }
  
  .join-button:hover {
    background-color: #ffa500;
  }
  </style>
  