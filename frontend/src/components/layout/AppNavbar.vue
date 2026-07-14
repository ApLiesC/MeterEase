<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

async function logout() {
  authStore.logout()

  await router.push({
    name: 'login',
  })
}
</script>

<template>
  <header class="navbar">

    <div class="left">

      <RouterLink
        to="/buildings"
        class="logo"
      >
        MeterEase
      </RouterLink>

      <nav>

        <RouterLink
          to="/buildings"
          class="nav-link"
        >
          Buildings
        </RouterLink>

        <RouterLink
          to="/rooms"
          class="nav-link"
        >
          Rooms
        </RouterLink>
        
<RouterLink to="/meter-readings">
  Meter Readings
</RouterLink>
      </nav>

    </div>


    <div class="right">

      <span
        v-if="authStore.manager"
        class="manager-name"
      >
        {{ authStore.manager.fullName }}
      </span>

      <button
        type="button"
        @click="logout"
      >
        Logout
      </button>

    </div>

  </header>
</template>

<style scoped>
.navbar {
  display:flex;
  justify-content:space-between;
  align-items:center;
  padding:1rem 2rem;
  border-bottom:1px solid #ddd;
  background:white;
}

.left {
  display:flex;
  align-items:center;
  gap:2rem;
}

nav {
  display:flex;
  gap:1rem;
}

.logo {
  font-size:1.2rem;
  font-weight:bold;
  color:inherit;
  text-decoration:none;
}

.nav-link {
  color:inherit;
  text-decoration:none;
}

.router-link-active {
  font-weight:bold;
}

.right {
  display:flex;
  align-items:center;
  gap:1rem;
}

button {
  cursor:pointer;
  padding:.6rem 1rem;
}
</style>