<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const emailAddress = ref('')
const password = ref('')
const errorMessage = ref('')

async function submitLogin() {
  errorMessage.value = ''

  try {
    await authStore.login({
      emailAddress: emailAddress.value,
      password: password.value,
    })

    await router.push('/buildings')
  } catch (error) {
    errorMessage.value =
      error instanceof Error
        ? error.message
        : 'Unable to log in'
  }
}
</script>

<template>
  <main>
    <h1>Login to MeterEase</h1>

    <form @submit.prevent="submitLogin">
      <div>
        <label for="emailAddress">Email address</label>
        <input
          id="emailAddress"
          v-model="emailAddress"
          type="email"
          required
        />
      </div>

      <div>
        <label for="password">Password</label>
        <input
          id="password"
          v-model="password"
          type="password"
          required
        />
      </div>

      <p v-if="errorMessage">
        {{ errorMessage }}
      </p>

      <button type="submit" :disabled="authStore.loading">
        {{ authStore.loading ? 'Logging in...' : 'Login' }}
      </button>
    </form>
  </main>
</template>