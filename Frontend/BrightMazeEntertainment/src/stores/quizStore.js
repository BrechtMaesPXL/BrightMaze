import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

const initialQuestions = [
  {
    id: 1,
    question: "Which factor contributes the most to the rapid spread of wildfires?",
    options: ["High humidity", "Strong winds", "Cold temperatures", "Sandy soil"],
    correctAnswers: [1],
    explanation: "Strong winds can carry embers over long distances and spread wildfires rapidly."
  },
  {
    id: 2,
    question: "What should you do if you are caught in a wildfire while driving?",
    options: [
      "Keep driving and try to outrun the fire",
      "Abandon your vehicle and run",
      "Park in a clearing, stay inside, and cover yourself with a blanket",
      "Drive into the nearest body of water"
    ],
    correctAnswers: [2],
    explanation: "If trapped, parking in a clearing and staying inside your vehicle can provide some protection."
  },
  {
    id: 3,
    question: "Which of the following materials is the safest to use for building in wildfire-prone areas?",
    options: ["Wood shingles", "Vinyl siding", "Stucco and metal roofing", "Thatched roofing"],
    correctAnswers: [2],
    explanation: "Stucco and metal roofing are fire-resistant materials that help prevent a fire from spreading."
  },
  {
    id: 4,
    question: "What is the best way to protect your home from embers during a wildfire?",
    options: [
      "Leaving windows open to prevent heat buildup",
      "Removing dry leaves and debris from gutters",
      "Placing wooden furniture close to the house",
      "Stacking firewood against exterior walls"
    ],
    correctAnswers: [1],
    explanation: "Embers can ignite dry leaves and debris in gutters, so keeping them clean is crucial."
  },
  {
    id: 5,
    question: "Which of the following is NOT recommended when evacuating from a wildfire?",
    options: [
      "Wearing synthetic clothing",
      "Keeping your car fuel tank full",
      "Following official evacuation routes",
      "Bringing an emergency kit"
    ],
    correctAnswers: [0],
    explanation: "Synthetic clothing can melt in extreme heat, causing severe burns."
  },
  {
    id: 6,
    question: "Why is it dangerous to return to a burned area immediately after a wildfire?",
    options: [
      "The ground may still be hot and unstable",
      "Wild animals may have moved in",
      "The air may contain high levels of oxygen",
      "Firefighters do not allow anyone to return"
    ],
    correctAnswers: [0],
    explanation: "Burned areas can have hidden hotspots and weakened structures that pose a danger."
  },
  {
    id: 7,
    question: "What is the safest way to extinguish a small campfire before leaving a campsite?",
    options: [
      "Throwing dry leaves over it",
      "Pouring water and stirring the ashes",
      "Covering it with a plastic bag",
      "Leaving it to burn out on its own"
    ],
    correctAnswers: [1],
    explanation: "Pouring water and stirring ensures all embers are completely extinguished."
  },
  {
    id: 8,
    question: "What is a 'defensible space' around a home in a wildfire-prone area?",
    options: [
      "A space where firefighters can camp",
      "A cleared area that slows down wildfires",
      "A hidden bunker to escape to",
      "A storage area for fire extinguishers"
    ],
    correctAnswers: [1],
    explanation: "Creating a defensible space by clearing vegetation helps slow down wildfires and protect homes."
  },
  {
    id: 9,
    question: "Which of the following actions can unintentionally start a wildfire?",
    options: [
      "Driving a car over dry grass",
      "Using a gas stove indoors",
      "Keeping firewood dry",
      "Watering plants in the evening"
    ],
    correctAnswers: [0],
    explanation: "Hot car exhausts can ignite dry grass, leading to a wildfire."
  },
  {
    id: 10,
    question: "What should you do if your clothes catch fire in a wildfire situation?",
    options: [
      "Run as fast as possible",
      "Drop to the ground and roll",
      "Wave your arms for help",
      "Pat yourself with dry leaves"
    ],
    correctAnswers: [1],
    explanation: "Stop, drop, and roll helps smother the flames and prevent severe burns."
  }
]

export const useQuizStore = defineStore('quiz', () => {
  // Quiz state
  const currentQuestionIndex = ref(0)
  const questions = ref([...initialQuestions]) // Initialize with a copy of initialQuestions
  const userAnswers = ref([])
  
  // User & highscore state
  const username = ref('')
  const highscores = ref([])

  // Getters
  const currentQuestion = computed(() => questions.value[currentQuestionIndex.value])
  const totalQuestions = computed(() => questions.value.length)
  const isLastQuestion = computed(() => {
    console.log('isLastQuestion:', currentQuestionIndex.value, questions.value.length)
    return currentQuestionIndex.value === questions.value.length - 1
  })
  const sortedHighscores = computed(() => [...highscores.value].sort((a, b) => b.score - a.score))

  // Initialize highscores
  const loadHighscores = () => {
    const saved = localStorage.getItem('quizHighscores')
    if (saved) highscores.value = JSON.parse(saved)
  }

  // Actions
  const submitAnswer = (selectedAnswers) => {
    userAnswers.value[currentQuestionIndex.value] = selectedAnswers
  }

  const nextQuestion = () => {
    if (currentQuestionIndex.value < questions.value.length - 1) {
      currentQuestionIndex.value++
    }
  }

  const saveHighscore = () => {
    const score = userAnswers.value.reduce((total, userAnswer, index) => {
      const question = questions.value[index]
      const isCorrect = 
        userAnswer.length === question.correctAnswers.length &&
        userAnswer.every(answer => question.correctAnswers.includes(answer))
      return isCorrect ? total + 1 : total
    }, 0)

    highscores.value.push({
      username: username.value || 'Anonymous',
      score,
      date: new Date().toISOString()
    })

    highscores.value.sort((a, b) => b.score - a.score)
    highscores.value = highscores.value.slice(0, 5)
    localStorage.setItem('quizHighscores', JSON.stringify(highscores.value))
  }

  const resetQuiz = () => {
  console.log('Resetting quiz state');
  currentQuestionIndex.value = 0;
  userAnswers.value = [];
  username.value = '';
  questions.value = [...initialQuestions];
  console.log('Questions after reset:', questions.value.length);
  };

  // Initialize
  loadHighscores()

  return {
    // Quiz state
    currentQuestionIndex,
    questions,
    userAnswers,
    currentQuestion,
    totalQuestions,
    isLastQuestion,
    
    // User & highscore state
    username,
    highscores,
    sortedHighscores,
    
    // Actions
    submitAnswer,
    nextQuestion,
    saveHighscore,
    resetQuiz,
    loadHighscores
  }
})