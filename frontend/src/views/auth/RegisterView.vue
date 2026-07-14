<script setup lang="ts">
import { ref } from 'vue'
import {
  RouterLink,
  useRouter,
} from 'vue-router'

import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const fullName = ref('')
const emailAddress = ref('')
const password = ref('')
const passwordConfirmation = ref('')
const errorMessage = ref('')

async function submitRegistration(): Promise<void> {
  errorMessage.value = ''

  if (
    password.value !==
    passwordConfirmation.value
  ) {
    errorMessage.value =
      'Passwords do not match'
    return
  }

  try {
    await authStore.register({
      fullName: fullName.value.trim(),
      emailAddress:
        emailAddress.value.trim(),
      password: password.value,
      passwordConfirmation:
        passwordConfirmation.value,
    })

    await router.push({
      name: 'login',
      query: {
        registered: 'true',
      },
    })
  } catch (error) {
    errorMessage.value =
      error instanceof Error
        ? error.message
        : 'Unable to register'
  }
}
</script>

<template>
  <main class="auth-page">
    <section class="auth-card">
      <h1>Create a MeterEase Account</h1>

      <form @submit.prevent="submitRegistration">
        <div class="form-group">
          <label for="fullName">
            Full name
          </label>

          <input
            id="fullName"
            v-model="fullName"
            type="text"
            autocomplete="name"
            required
          />
        </div>

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
            autocomplete="new-password"
            minlength="8"
            required
          />

          <small>
            At least 8 characters with uppercase,
            lowercase, number, and special character.
          </small>
        </div>

        <div class="form-group">
          <label for="passwordConfirmation">
            Confirm password
          </label>

          <input
            id="passwordConfirmation"
            v-model="passwordConfirmation"
            type="password"
            autocomplete="new-password"
            minlength="8"
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
              ? 'Creating account...'
              : 'Register'
          }}
        </button>
      </form>

      <p>
        Already have an account?
        <RouterLink to="/login">
          Login
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
</style>