import { createRouter, createWebHistory } from 'vue-router';
import AuthenticationView from '../views/AuthenticationView.vue';
import HomeView from '../views/HomeView.vue';
import InsightsView from '../views/InsightsView.vue';
import TableView from '../views/TableView.vue';
import MapView from "@/views/Maps/TotalWildfiresView.vue";
import safetyView from "@/views/SafetyView.vue";
import MapChangingView from "@/views/MapChangingView.vue";
import ErrorPage from "@/components/Menu/ErrorPage.vue";
import QuizView from '@/views/QuizView.vue';
import FireSizeGraphView from '@/views/FireSizeView.vue';
import FireCausePredictionView from '@/views/Predictions/FireCausePredictionView.vue'
import AverageFireSizeComponent from '@/components/Graphs/LineGraphFireSizeComponent.vue';
import { useQuizStore } from '@/stores/quizStore';
import WeatherPredictionView from '@/views/Predictions/WeatherPredictionView.vue';
import FireSizePredictionView from '@/views/Predictions/FireSizePredictionView.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'authentication',
      component: AuthenticationView
    },
    {
      path: '/home',
      name: 'home',
      component: HomeView
    },
    {
      path: '/insights',
      name: 'insights',
      component: InsightsView
    },
    {
      path: '/table',
      name: 'table',
      component: TableView
    },
    {
      path: '/map',
      name: 'map',
      component: MapView
    },
    {
      path: '/safety',
      name: 'safety',
      component: safetyView
    },
    {
      path: '/map/:Component',
      name: 'Changing',
      component: MapChangingView
    },
    {
      path: '/quiz',
      name: 'Quiz',
      component: QuizView
    },
    {
      path: '/fire-size',
      name: 'Fire-size',
      component: FireSizeGraphView
    },
    {
      path: '/charts',
      name: 'charts',
      component: AverageFireSizeComponent
    },
    {
      path: '/map/predictions/fire-cause',
      name: 'fire-cause',
      component: FireCausePredictionView
    },
    {
      path: '/map/predictions/weather',
      name: 'weather',
      component: WeatherPredictionView
    },
    {
      path: '/map/FireSizePrediction',
      name: 'fire-size',
      component: FireSizePredictionView
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'error',
      component: ErrorPage
    }
  ],
});

router.beforeEach((to, from, next) => {
  const publicPages = ['authentication', 'error'];
  const authRequired = !publicPages.includes(to.name);
  const token = localStorage.getItem('token');

  if (to.name === 'Quiz') {
    const quizStore = useQuizStore();
    quizStore.resetQuiz();
  }

  if (authRequired && !token) {
    return next({ name: 'authentication' });
  }

  next();
});


export default router;
