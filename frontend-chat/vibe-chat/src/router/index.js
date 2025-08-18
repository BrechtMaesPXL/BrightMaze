import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/homepage'
    },
    {
      path: '/homepage',
      name: 'homepage',
      component: () => import('../views/HomePageView.vue'),
    },
    // {
    //   path: '/about',
    //   name: 'about',
    //   component: () => import('@/views/AboutView.vue'),
    // },
    {
      path: '/events',
      name: 'events',
      component: () => import('@/views/EventOverview.vue'),
    },
    {
      path: '/events/:id',
      name: 'eventDetails',
      component: () => import('@/views/EventDetails.vue'),
      props: true
    }
  ],
})

export default router
