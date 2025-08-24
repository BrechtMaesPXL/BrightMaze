<template>
  <div class="results-page">
    <div class="result-container">
      <h1>Fire Prediction Results</h1>

      <div v-if="loading" class="loading">
        Predicting...
      </div>

      <div v-else class="result-card">
        <h2>
          Your fire is predicted as class
          <span :class="['fire-class', result.fireClass?.toLowerCase()]">
            {{ result.fireClass }}
          </span>
        </h2>
        <p>
          Estimated duration: <strong>{{ formattedDuration }}</strong>
        </p>

        <div class="actions">
          <button @click="restart">New Prediction</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useFiresStore } from '@/stores/firesStore.js'

const fireStore = useFiresStore()
const emit = defineEmits(['restart'])

const loading = computed(() => fireStore.loading)
const result = computed(() => fireStore.result || { fireClass: '-', durationMinutes: 0 })

const formattedDuration = computed(() => {
  const mins = result.value.durationMinutes
  if (!mins || mins < 60) return `${mins} minutes`
  const hrs = Math.floor(mins / 60)
  const rem = mins % 60
  return `${hrs}h ${rem}m`
})

function restart() {
  emit('restart')
}
</script>

<style scoped>
.results-page {
  position: fixed;
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: rgba(0, 0, 0, 0.85);
  z-index: 1000;
}

.result-container {
  max-width: 800px;
  width: 90%;
  padding: 30px;
  text-align: center;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 12px;
  color: #fff;
  border: 2px solid #e98616;
}

h1 {
  color: #e98616;
  margin-bottom: 1.5rem;
  font-size: 2.2rem;
}

.loading {
  font-size: 1.2rem;
  color: #aaa;
}

.result-card {
  background: #111;
  padding: 30px;
  border-radius: 12px;
  margin-top: 20px;
}

h2 {
  margin-bottom: 1rem;
  font-size: 1.8rem;
  color: #fff;
}

.fire-class {
  font-size: 2rem;
  font-weight: bold;
  margin-left: 8px;
}

.fire-class.a { color: #4caf50; }
.fire-class.b { color: #8bc34a; }
.fire-class.c { color: #ffeb3b; }
.fire-class.d { color: #ffc107; }
.fire-class.e { color: #ff9800; }
.fire-class.f { color: #f44336; }
.fire-class.g { color: #d32f2f; }

.actions {
  margin-top: 30px;
}

.actions button {
  background: #e98616;
  color: #000;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  font-size: 1rem;
  cursor: pointer;
  transition: background 0.2s;
}

.actions button:hover {
  background: #ffab40;
}
</style>
