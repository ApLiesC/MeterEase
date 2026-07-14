<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

async function logout(): Promise<void> {
  authStore.logout()

  await router.push({
    name: 'login',
  })
}
</script>

<template>
  <header
    class="border-b border-black bg-neutral-100 px-4 py-4 font-mono uppercase tracking-wider sm:px-8"
  >
    <div
      class="mx-auto flex max-w-5xl flex-col gap-4 sm:flex-row sm:items-center sm:justify-between"
    >
      <!-- Left -->
      <div
        class="flex flex-col gap-3 sm:flex-row sm:items-center sm:gap-8"
      >
        <RouterLink
          to="/buildings"
          class="text-xl font-bold tracking-widest"
        >
          MeterEase
        </RouterLink>

        <nav
          class="flex flex-wrap gap-2"
        >
          <RouterLink
            to="/buildings"
            class="rounded-sm border border-transparent px-3 py-1 text-sm transition hover:border-black"
          >
            Buildings
          </RouterLink>

          <RouterLink
            to="/rooms"
            class="rounded-sm border border-transparent px-3 py-1 text-sm transition hover:border-black"
          >
            Rooms
          </RouterLink>
        </nav>
      </div>

      <!-- Right -->
      <div
        class="flex items-center justify-between gap-3 sm:justify-end"
      >
        <span
          v-if="authStore.manager"
          class="text-xs text-gray-600"
        >
          {{ authStore.manager.fullName }}
        </span>

        <button
          type="button"
          class="rounded-sm border border-black px-4 py-2 text-sm font-semibold transition hover:bg-black hover:text-white"
          @click="logout"
        >
          Logout
        </button>
      </div>
    </div>
  </header>
</template>