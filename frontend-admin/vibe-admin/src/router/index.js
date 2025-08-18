import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import EventManagementView from '@/views/EventManagementView.vue'
import VueCookies from "vue-cookies";
import RegisterView from "@/views/RegisterView.vue";
import LogsView from "@/views/LogsView.vue";
import LogDetailView from "@/views/LogDetailView.vue";
import ConfigView from '@/views/ConfigView.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/home',
      name: 'home',
      component: HomeView,
      meta: { requiresAuth: true }
    },
    {
      path: '/',
      name: 'login',
      component: LoginView
    },
    {
      path: '/events',
      name: 'events',
      component: EventManagementView,
      meta: { requiresAuth: true }

    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView,
      meta: { requiresAuth: true }

    },
    {
      path: '/logs',
      name: 'logs',
      component: LogsView,
      meta: { requiresAuth: true }

    },
    {
      path: '/logs/:id',
      name: 'logsDetail',
      component: LogDetailView,
      meta: { requiresAuth: true }

    },
    {
      path: '/eventmanagement',
      name: 'eventmanagement',
      component: EventManagementView,
      meta: { requiresAuth: true }

    },
    {
      path: '/instellingen',
      name: 'instellingen',
      component: ConfigView,
      meta: { requiresAuth: true }

    },
  ],
})

router.beforeEach((to, from, next) => {
  const token = VueCookies.get('userToken')
  const isAuthenticated = !!token

  if (to.meta.requiresAuth && !isAuthenticated) {
    next({ name: 'login' });
  } else {
    next();
  }
});

export default router;
