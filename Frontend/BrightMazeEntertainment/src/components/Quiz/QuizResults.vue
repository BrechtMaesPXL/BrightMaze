<template>
  <div class="quiz-completed">
    <div class="completion-card quiz-card">
      <h2 class="question">Quiz completed{{ username ? ', ' + username : '' }}!</h2>

      <div class="result-container">
        <div class="result-content">
          <div class="score-display">
            Your score: <span class="score">{{ score }}</span> / {{ totalQuestions }}
          </div>

          <div class="highscores" v-if="highscores.length > 0">
            <h3>Highscores</h3>
            <div class="highscore-list">
              <div
                v-for="(hs, index) in sortedHighscores"
                :key="index"
                class="highscore-item"
                :class="{ 'current-user': hs.username === (username || 'Anonymous') && hs.score === score }"
              >
                <span class="rank">{{ index + 1 }}.</span>
                <span class="name">{{ hs.username }}</span>
                <span class="score">{{ hs.score }}/{{ totalQuestions }}</span>
              </div>
            </div>
          </div>

          <div class="result-buttons">
            <button @click="$emit('restart')" class="check-button reset-button">
              Retake quiz
            </button>
            <router-link to="/home" class="home-button">
              Go Home
            </router-link>
          </div>
        </div>

        <div class="image-message-container">
          <img
            :src="score <= 5 ? samSad : samHappy"
            alt="Sam's mood"
            class="mood-image animated"
          />
          <div class="result-message">
            {{ score <= 5 ? 'Better luck next time!' : 'Well done!' }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import samSad from '@/assets/CardFotos/sam_sad.png'
import samHappy from '@/assets/CardFotos/sam_happy.png'

const props = defineProps({
  username: String,
  score: Number,
  totalQuestions: Number,
  highscores: Array
})

const emit = defineEmits(['restart'])

const sortedHighscores = computed(() => {
  return [...props.highscores].sort((a, b) => b.score - a.score)
})
</script>

<style scoped>
.quiz-completed {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.completion-card {
  max-width: 600px;
  width: 100%;
}

.result-container {
  display: flex;
  align-items: flex-start;
  gap: 20px;
}

.result-content {
  flex: 1;
}

.image-message-container {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  text-align: right;
  margin-top: 140px;
  margin-left: 30px;
  position: relative;
}

.mood-image {
  max-width: 600px;
  height: 400px;
  margin-right: 20px;
}

.animated {
  animation: bounceIn 0.8s ease-out forwards;
}

@keyframes bounceIn {
  0% {
    opacity: 0;
    transform: scale(0.3) translateY(20px);
  }
  50% {
    opacity: 1;
    transform: scale(1.1);
  }
  70% {
    transform: scale(0.9);
  }
  100% {
    transform: scale(1) translateY(0);
    opacity: 1;
  }
}

.result-message {
  font-size: 1.3rem;
  font-weight: bold;
  color: #ff8c00;
  margin-top: 10px;
  padding: 10px 18px;
  display: inline-block;
  box-shadow: 0 2px 8px rgba(255, 140, 0, 0.08);
  margin-right: 30px;
  margin-top: 20px;
  background: none;


}

.score-display {
  font-size: 1.5rem;
  margin: 10px 0;
  color: #ddd;
  margin-bottom: -40px;
  margin-top: 60px;
}

.score {
  font-weight: bold;
  color: #ff8c00;
  font-size: 2rem;
}

.highscores {
  margin: 30px 0;
}

.highscore-list {
  margin-top: 15px;
  width: 100%;
  max-width: 450px;
}

.highscore-item {
  display: grid;
  grid-template-columns: 40px 1fr 100px;
  gap: 15px;
  padding: 15px;
  margin: 5px 0;
  border-radius: 6px;
  background: #333;
  align-items: center;
  color: #ddd;
}

.highscore-item.current-user {
  background: #444;
  color: #ff8c00;
  font-weight: bold;
}

.rank {
  font-weight: bold;
  text-align: right;
  color: #ff8c00;
}

.name {
  text-align: left;
}

.score {
  text-align: right;
}

.result-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 30px;
}

.check-button,
.reset-button {
  background-color: #ff8c00 !important;
  color: #000 !important;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.2s;
  width: 100%;
  margin-bottom: 8px;
}

.check-button:hover,
.reset-button:hover {
  background-color: #ffa500 !important;
}

.check-button:disabled,
.reset-button:disabled {
  background-color: #555 !important;
  color: #888 !important;
  cursor: not-allowed;
}

.home-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background-color: #333;
  color: #ff8c00;
  border: 1px solid #ff8c00;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.2s;
  text-decoration: none;
  text-align: center;
}

.home-button:hover {
  background-color: #444;
}

.home-button svg {
  width: 20px;
  height: 20px;
}
</style>
