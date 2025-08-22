<template>
    <div class="quiz-card">
      <h2 class="question">{{ question.question }}</h2>
      
      <div class="options-container">
        <div 
          v-for="(option, index) in question.options" 
          :key="index"
          class="option"
          :class="{
            'selected': selectedAnswers.includes(index),
            'correct': showResults && question.correctAnswers.includes(index),
            'incorrect': showResults && selectedAnswers.includes(index) && !question.correctAnswers.includes(index)
          }"
          @click="handleOptionClick(index)"
        >
          {{ option }}
        </div>
      </div>
      
      <button 
        class="check-button" 
        @click="$emit('check')"
        :disabled="selectedAnswers.length === 0"
      >
        {{ showResults ? (isLastQuestion ? 'View score' : 'Next question') : 'Check answer' }}
      </button>
      
      <div v-if="showResults" class="results">
        <p class="explanation">{{ question.explanation }}</p>
        <div class="progress">
          Question {{ currentIndex + 1 }} of {{ totalQuestions }}
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
const props = defineProps({
  question: Object,
  selectedAnswers: Array,
  showResults: Boolean,
  isLastQuestion: Boolean,
  currentIndex: Number,
  totalQuestions: Number
})

const emit = defineEmits(['check', 'select'])

const handleOptionClick = (index) => {
  if (!props.showResults) {
    emit('select', index)
  }
}
</script>
  
  <style scoped>
  .quiz-card {
    background: #222;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(255, 140, 0, 0.2);
    padding: 30px;
    margin: 20px auto;
    border: 1px solid #ff8c00;
    max-width: 800px;
    width: 100%;
  }
  
  .question {
    color: #ff8c00;
    margin-bottom: 25px;
    font-size: 1.4rem;
  }
  
  .options-container {
    display: grid;
    grid-template-columns: 1fr;
    gap: 12px;
    margin-bottom: 25px;
  }
  
  .option {
    padding: 15px;
    border: 2px solid #444;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s ease;
    font-size: 1.1rem;
    color: #fff;
    background-color: #333;
  }
  
  .option:hover {
    background-color: #444;
    border-color: #ff8c00;
  }
  
  .option.selected {
    background-color: #333;
    border-color: #ff8c00;
    color: #ff8c00;
  }
  
  .option.correct {
    background-color: #2e7d32;
    border-color: #4caf50;
    color: #fff;
  }
  
  .option.incorrect {
    background-color: #c62828;
    border-color: #f44336;
    color: #fff;
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
  
  .results {
    margin-top: 25px;
    padding-top: 20px;
    border-top: 1px solid #444;
  }
  
  .explanation {
    color: #ddd;
    font-style: italic;
    margin-bottom: 15px;
  }
  
  .progress {
    text-align: center;
    color: #ff8c00;
    font-weight: bold;
  }
  </style>