import { defineStore } from 'pinia'
import { apiRequest } from '@/services/api'

import type {
  AuthResponse,
  LoginRequest,
  Manager,
  RegisterRequest,
} from '@/types/auth'

interface AuthState {
  manager: Manager | null
  accessToken: string | null
  loading: boolean
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    manager: null,
    accessToken: localStorage.getItem('accessToken'),
    loading: false,
  }),

  getters: {
    isAuthenticated: (state) =>
      Boolean(state.accessToken),
  },

  actions: {
    async register(
  request: RegisterRequest,
): Promise<Manager> {
  this.loading = true

  try {
    return await apiRequest<Manager>(
      '/api/auth/register',
      {
        method: 'POST',
        authenticated: false,
        body: JSON.stringify(request),
      },
    )
  } finally {
    this.loading = false
  }
},

    async login(
      request: LoginRequest,
    ): Promise<void> {
      this.loading = true

      try {
        const response = await apiRequest<AuthResponse>(
  '/api/auth/login',
  {
    method: 'POST',
    authenticated: false,
    body: JSON.stringify(request),
  },
)

        this.accessToken = response.accessToken

        this.manager = {
          managerId: response.managerId,
          fullName: response.fullName,
          emailAddress: response.emailAddress,
        }

        localStorage.setItem(
          'accessToken',
          response.accessToken,
        )
      } finally {
        this.loading = false
      }
    },

    async fetchCurrentManager(): Promise<void> {
      if (!this.accessToken) {
        return
      }

      try {
        this.manager =
          await apiRequest<Manager>('/api/auth/me')
      } catch {
        this.logout()
      }
    },

    logout(): void {
      this.manager = null
      this.accessToken = null
      localStorage.removeItem('accessToken')
    },
  },
})