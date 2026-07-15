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

        <p class="mt-1 text-xs text-gray-500">
          Create Manager Account
        </p>
      </header>

      <form
        class="space-y-5"
        @submit.prevent="submitRegistration"
      >
        <div>
          <label
            for="fullName"
            class="mb-1 block text-s font-semibold"
          >
            Full Name
          </label>

          <input
            id="fullName"
            v-model="fullName"
            type="text"
            autocomplete="name"
            required
            placeholder="John Smith"
            class="h-10 w-full rounded-sm border border-black bg-white px-3 text-sm normal-case outline-none focus:ring-1 focus:ring-black"
          />
        </div>

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
            autocomplete="new-password"
            minlength="8"
            required
            class="h-10 w-full rounded-sm border border-black bg-white px-3 text-sm normal-case outline-none focus:ring-1 focus:ring-black"
          />

          <p
            class="mt-1 text-xs normal-case text-gray-800"
          >
            Minimum 8 characters with uppercase,
            lowercase, number, and special character.
          </p>
        </div>

        <div>
          <label
            for="passwordConfirmation"
            class="mb-1 block text-xs font-semibold"
          >
            Confirm Password
          </label>

          <input
            id="passwordConfirmation"
            v-model="passwordConfirmation"
            type="password"
            autocomplete="new-password"
            minlength="8"
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
              ? 'Creating Account...'
              : 'Create Account'
          }}
        </button>
      </form>

      <footer
        class="mt-6 border-t border-black pt-4 text-center text-s text-gray-600 normal-case"
      >
        Already have an account?

        <RouterLink
          to="/login"
          class="font-semibold text-black underline underline-offset-2 transition hover:text-gray-600"
        >
          Login
        </RouterLink>
      </footer>
    </section>
  </main>
</template>