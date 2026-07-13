import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/auth/LoginView.vue'
import RegisterView from '@/views/auth/RegisterView.vue'
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),

  routes: [
    {
      path: '/',
      redirect: '/login',
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: {
        guestOnly: true,
      },
    },
    {
      path: '/buildings',
      name: 'buildings',
      component: () =>
        import('@/views/buildings/BuildingsView.vue'),
      meta: {
        requiresAuth: true,
      },
    },
    {
  path: '/register',
  name: 'register',
  component: RegisterView,
  meta: {
    guestOnly: true,
  },
},
  ],
})

router.beforeEach((to) => {
  const token = localStorage.getItem('accessToken')

  if (to.meta.requiresAuth && !token) {
    return {
      name: 'login',
    }
  }

  if (to.meta.guestOnly && token) {
    return {
      name: 'buildings',
    }
  }
})

export default router