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
  <main
    class="flex min-h-screen items-center justify-center bg-neutral-50 p-6"
  >
    <section
      class="w-full max-w-md rounded-md border border-black bg-neutral-100 p-8 font-mono uppercase tracking-wider"
    >
      <!-- Header -->
      <header class="mb-6 border-b border-black pb-4">
        <h1 class="text-3xl font-bold">
          MeterEase
        </h1>

        <p class="mt-1 text-s text-gray-500">
          Manager Login
        </p>
      </header>

      <p
        v-if="route.query.registered === 'true'"
        class="mb-5 rounded-sm border border-green-700 bg-green-50 p-3 text-xs text-green-700 normal-case"
      >
        Account created successfully. You can now log in.
      </p>

      <form
        class="space-y-5"
        @submit.prevent="submitLogin"
      >
        <div>
          <label
            for="emailAddress"
            class="mb-1 block text-s font-semibold"
          >
            Email Address
          </label>

          <input
            id="emailAddress"
            v-model="emailAddress"
            type="email"
            autocomplete="email"
            required
            placeholder="manager@email.com"
            class="h-10 w-full rounded-sm border border-black bg-white px-3 text-sm normal-case outline-none focus:ring-1 focus:ring-black"
          />
        </div>

        <div>
          <label
            for="password"
            class="mb-1 block text-s font-semibold"
          >
            Password
          </label>

          <input
            id="password"
            v-model="password"
            type="password"
            autocomplete="current-password"
            required
            class="h-10 w-full rounded-sm border border-black bg-white px-3 text-sm normal-case outline-none focus:ring-1 focus:ring-black"
          />
        </div>

        <p
          v-if="errorMessage"
          class="rounded-sm border border-red-700 bg-red-50 p-3 text-xs text-red-700 normal-case"
        >
          {{ errorMessage }}
        </p>

        <button
          type="submit"
          :disabled="authStore.loading"
          class="h-10 w-full rounded-sm border border-black bg-black text-l font-semibold text-white transition hover:bg-white hover:text-black disabled:cursor-not-allowed disabled:opacity-50"
        >
          {{
            authStore.loading
              ? 'Logging In...'
              : 'Login'
          }}
        </button>
      </form>

      <footer
        class="mt-6 border-t border-black pt-4 text-center text-s text-gray-600 normal-case"
      >
        Don't have an account?

        <RouterLink
          to="/register"
          class="font-semibold text-black underline underline-offset-2 transition hover:text-gray-600"
        >
          Register
        </RouterLink>
      </footer>
    </section>
  </main>
</template>