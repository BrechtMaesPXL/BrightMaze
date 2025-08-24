<template>
  <div class="quiz-page">
    <router-link
      v-if="showStartScreen || (!showStartScreen && !showUsernameModal && !quizCompleted)"
      to="/home"
      class="home-button"
    >
      Home
    </router-link>

    <QuizStart v-if="showStartScreen" @join="showUsernameModal = true" />

    <QuizUsernameModal
      v-if="showUsernameModal"
      @start="startQuiz"
    />

    <QuizQuestion
      v-if="!showStartScreen && !showUsernameModal && !quizCompleted"
      :question="quizStore.currentQuestion"
      :selected-answers="selectedAnswers"
      :show-results="showResults"
      :is-last-question="quizStore.isLastQuestion"
      :current-index="quizStore.currentQuestionIndex"
      :total-questions="quizStore.totalQuestions"
      @check="checkAnswers"
      @select="toggleAnswer"
    />

    <QuizResults
      v-if="quizCompleted"
      :username="quizStore.username"
      :score="score"
      :total-questions="quizStore.totalQuestions"
      :highscores="quizStore.highscores"
      @restart="resetQuiz"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useQuizStore } from '@/stores/quizStore'
import QuizStart from '@/components/Quiz/QuizStart.vue'
import QuizUsernameModal from '@/components/Quiz/QuizUsernameModal.vue'
import QuizQuestion from '@/components/Quiz/QuizQuestion.vue'
import QuizResults from '@/components/Quiz/QuizResults.vue'

const quizStore = useQuizStore()

const showStartScreen = ref(true)
const showUsernameModal = ref(false)
const quizCompleted = ref(false)
const selectedAnswers = ref([])
const showResults = ref(false)

// Reset the quiz state when the component is mounted
onMounted(() => {
  console.log('QuizView mounted, resetting quiz state')
  quizStore.resetQuiz()
  showStartScreen.value = true
  showUsernameModal.value = false
  quizCompleted.value = false
  selectedAnswers.value = []
  showResults.value = false
})

const score = computed(() => {
  return quizStore.userAnswers.reduce((total, userAnswer, index) => {
    const question = quizStore.questions[index]
    const isCorrect =
      userAnswer.length === question.correctAnswers.length &&
      userAnswer.every(answer => question.correctAnswers.includes(answer))
    return isCorrect ? total + 1 : total
  }, 0)
})

const startQuiz = () => {
  console.log('Starting quiz with username:', quizStore.username); // Debug log
  showUsernameModal.value = false;
  showStartScreen.value = false;
};

const toggleAnswer = (index) => {
  const position = selectedAnswers.value.indexOf(index)
  if (position === -1) {
    selectedAnswers.value.push(index)
  } else {
    selectedAnswers.value.splice(position, 1)
  }
}

const checkAnswers = () => {
  if (!showResults.value) {
    quizStore.submitAnswer([...selectedAnswers.value])
    showResults.value = true
  } else {
    if (quizStore.isLastQuestion) {
      quizStore.saveHighscore()
      quizCompleted.value = true
    } else {
      quizStore.nextQuestion()
      selectedAnswers.value = []
      showResults.value = false
    }
  }
}

const resetQuiz = () => {
  console.log('Resetting quiz in QuizView')
  quizStore.resetQuiz()
  selectedAnswers.value = []
  showResults.value = false
  quizCompleted.value = false
  showStartScreen.value = true
}
</script>

<style scoped>
.quiz-page {
  min-height: 100vh;
  background-color: #000;
  padding: 20px;
  display: flex;
  flex-direction: column;
  position: relative;
}

.home-button {
  position: absolute;
  top: 20px;
  left: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background-color: #ff8c00;
  color: #fff;
  padding: 10px 20px;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: bold;
  cursor: pointer;
  text-decoration: none;
  transition: all 0.2s;
}

.home-button:hover {
  background-color: #444;
}
</style>
