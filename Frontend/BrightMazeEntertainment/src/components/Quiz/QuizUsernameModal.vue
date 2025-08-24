<template>
  <div class="modal-overlay" @click.self="emit('start')">
    <div class="username-modal quiz-card">
      <h2 class="question">Who is playing?</h2>

      <div class="button-group">
        <!-- Play as logged-in user -->
        <button
          :class="{ selected: selectedOption === 'user' }"
          @click="selectedOption = 'user'"
        >
          Play as <span>{{ userStore.username }}</span>
        </button>

        <!-- Play as anonymous player -->
        <button
          :class="{ selected: selectedOption === 'anonymous' }"
          @click="selectedOption = 'anonymous'"
        >
          Play as anonymous player
        </button>
      </div>

      <!-- Show selected username if 'user' is selected -->
      <div v-if="selectedOption === 'user'">
        <p class="selected-text">
          Playing as <span class="highlight">{{ userStore.username }}</span>
        </p>
      </div>

      <!-- Show 'Playing as Anonymous' if anonymous is selected -->
      <div v-if="selectedOption === 'anonymous'">
        <p class="selected-text">
          Playing as <span class="highlight">Anonymous</span>
        </p>
      </div>

      <div class="modal-buttons">
        <button 
          @click="handleStart" 
          class="check-button"
        >
          Start quiz
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useQuizStore } from '@/stores/quizStore'
import { useUserStore } from '@/stores/user'

const emit = defineEmits(['start'])

const quizStore = useQuizStore()
const userStore = useUserStore()

const selectedOption = ref('user')

const handleStart = () => {
  quizStore.username = selectedOption.value === 'user' ? userStore.username : 'Anonymous';
  console.log('Username set to:', quizStore.username); // Debug log
  emit('start');
}
</script>

<style scoped>
/* Modal Styling */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.username-modal {
  width: 90%;
  max-width: 500px;
  background-color: #222;
  padding: 30px;
  border-radius: 16px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.6);
  animation: fadeIn 0.3s ease;
}

.question {
  color: #fff;
  font-size: 1.5rem;
  margin-bottom: 20px;
  text-align: center;
}

/* Button Group Styling */
.button-group {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: 15px;
}

.button-group button {
  background-color: #333;
  color: #fff;
  border: 2px solid transparent;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: bold;
  cursor: pointer;
  width: 48%;
  transition: all 0.3s ease;
}

.button-group button:hover {
  background-color: #444;
}

.button-group button.selected {
  background-color: #ff8c00;
  color: #000;
  border: 2px solid #ff8c00;
}

.highlight {
  color: #ff8c00; /* Orange color for username or 'Anonymous' */
}

.selected-text {
  color: #fff;
  font-size: 1.2rem;
  text-align: center;
  margin-top: 15px;
}

.selected-text .highlight {
  color: #ff8c00;
}

/* Modal buttons */
.modal-buttons {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

.check-button {
  background-color: #ff8c00;
  color: #000;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.2s;
  width: 100%;
}

.check-button:hover {
  background-color: #ffa500;
}

.check-button:disabled {
  background-color: #555;
  color: #888;
  cursor: not-allowed;
}

/* Fade-in animation */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}
</style>
