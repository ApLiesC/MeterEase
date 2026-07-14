<script setup lang="ts">
import { ref } from 'vue'
import {
  RouterLink,
  useRoute,
  useRouter,
} from 'vue-router'

import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const emailAddress = ref('')
const password = ref('')
const errorMessage = ref('')

async function submitLogin(): Promise<void> {
  errorMessage.value = ''

  try {
    await authStore.login({
      emailAddress: emailAddress.value.trim(),
      password: password.value,
    })

    await router.push({
      name: 'buildings',
    })
  } catch (error) {
    errorMessage.value =
      error instanceof Error
        ? error.message
        : 'Unable to log in'
  }
}
</script>

<template>
  <main class="auth-page">
    <section class="auth-card">
      <h1>Login to MeterEase</h1>

      <p
        v-if="route.query.registered === 'true'"
        class="success-message"
      >
        Account created successfully. You can now log in.
      </p>

      <form @submit.prevent="submitLogin">
        <div class="form-group">
          <label for="emailAddress">
            Email address
          </label>

          <input
            id="emailAddress"
            v-model="emailAddress"
            type="email"
            autocomplete="email"
            required
          />
        </div>

        <div class="form-group">
          <label for="password">
            Password
          </label>

          <input
            id="password"
            v-model="password"
            type="password"
            autocomplete="current-password"
            required
          />
        </div>

        <p
          v-if="errorMessage"
          class="error-message"
        >
          {{ errorMessage }}
        </p>

        <button
          type="submit"
          :disabled="authStore.loading"
        >
          {{
            authStore.loading
              ? 'Logging in...'
              : 'Login'
          }}
        </button>
      </form>

      <p>
        Don't have an account?
        <RouterLink to="/register">
          Register
        </RouterLink>
      </p>
    </section>
  </main>
</template>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 24px;
}

.auth-card {
  width: 100%;
  max-width: 420px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 16px;
}

input {
  padding: 10px;
}

button {
  width: 100%;
  padding: 10px;
  cursor: pointer;
}

button:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.error-message {
  color: #b00020;
}

.success-message {
  color: #087830;
}
</style>