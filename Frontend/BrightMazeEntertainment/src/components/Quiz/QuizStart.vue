<template>
    <div class="start-screen">
      <div class="start-card quiz-card">
        <h1 class="title">Quiz Time!</h1>
        <p class="subtitle">Test your knowledge and compare your score with others</p>
        <button @click="$emit('join')" class="join-button">
          Join the quiz!
        </button>
      </div>
    </div>
  </template>
  
  <script setup>
  </script>
  
  <style scoped>
  .start-screen {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  .start-card {
    text-align: center;
    padding: 40px;
  }
  
  .title {
    color: #ff8c00;
    font-size: 2.5rem;
    margin-bottom: 20px;
  }
  
  .subtitle {
    color: #ddd;
    font-size: 1.2rem;
    margin-bottom: 40px;
  }
  
  .join-button {
    background-color: #ff8c00;
    color: #000;
    border: none;
    padding: 15px 30px;
    border-radius: 8px;
    font-size: 1.2rem;
    font-weight: bold;
    cursor: pointer;
    transition: all 0.3s;
  }
  
  .join-button:hover {
    background-color: #ffa500;
    transform: scale(1.05);
  }
  </style>