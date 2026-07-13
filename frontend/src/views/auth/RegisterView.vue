<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const fullName = ref('')
const emailAddress = ref('')
const password = ref('')
const passwordConfirmation = ref('')
const errorMessage = ref('')

async function submitRegistration() {
  errorMessage.value = ''

  try {
    await authStore.register({
      fullName: fullName.value,
      emailAddress: emailAddress.value,
      password: password.value,
      passwordConfirmation: passwordConfirmation.value,
    })

    await router.push('/login')
  } catch (error) {
    errorMessage.value =
      error instanceof Error
        ? error.message
        : 'Unable to register'
  }
}
</script>

<template>
  <main>
    <h1>Create a MeterEase Account</h1>

    <form @submit.prevent="submitRegistration">
      <div>
        <label for="fullName">Full name</label>
        <input
          id="fullName"
          v-model="fullName"
          type="text"
          required
        />
      </div>

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

      <div>
        <label for="passwordConfirmation">
          Confirm password
        </label>

        <input
          id="passwordConfirmation"
          v-model="passwordConfirmation"
          type="password"
          required
        />
      </div>

      <p v-if="errorMessage">
        {{ errorMessage }}
      </p>

      <button type="submit" :disabled="authStore.loading">
        {{ authStore.loading ? 'Creating account...' : 'Register' }}
      </button>
    </form>

    <p>
      Already have an account?
      <RouterLink to="/login">Login</RouterLink>
    </p>
  </main>
</template>