<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'

import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

onMounted(async () => {
  await authStore.fetchCurrentManager()
})

async function logout(): Promise<void> {
  authStore.logout()

  await router.push({
    name: 'login',
  })
}
</script>

<template>
  <main>
    <header>
      <div>
        <h1>Buildings</h1>

        <p v-if="authStore.manager">
          Logged in as
          {{ authStore.manager.fullName }}
        </p>
      </div>

      <button @click="logout">
        Logout
      </button>
    </header>

    <p>
      Building management will be added next.
    </p>
  </main>
</template>

<style scoped>
main {
  padding: 24px;
}

header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

button {
  padding: 8px 16px;
  cursor: pointer;
}
</style>