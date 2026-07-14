<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import CreateRoomModal from '@/components/rooms/CreateRoomModal.vue'
import { getBuildings } from '@/services/buildingService'
import {
  deleteRoom,
  getRooms,
} from '@/services/roomService'

import { useAuthStore } from '@/stores/auth'

import type { Building } from '@/types/building'
import type { Room } from '@/types/room'

const router = useRouter()
const authStore = useAuthStore()

const buildings = ref<Building[]>([])
const rooms = ref<Room[]>([])

const selectedBuildingId = ref<number | null>(null)

const loadingBuildings = ref(false)
const loadingRooms = ref(false)
const showCreateRoomModal = ref(false)
const errorMessage = ref('')

async function loadBuildings(): Promise<void> {
  loadingBuildings.value = true
  errorMessage.value = ''

  try {
    buildings.value = await getBuildings()

    if (
      buildings.value.length > 0 &&
      selectedBuildingId.value === null
    ) {
      selectedBuildingId.value =
        buildings.value[0].buildingId ?? null
    }
  } catch (error) {
    errorMessage.value =
      error instanceof Error
        ? error.message
        : 'Failed to load buildings.'
  } finally {
    loadingBuildings.value = false
  }
}

async function loadRooms(): Promise<void> {
  if (selectedBuildingId.value === null) {
    rooms.value = []
    return
  }

  loadingRooms.value = true
  errorMessage.value = ''

  try {
    rooms.value = await getRooms(
      selectedBuildingId.value,
    )
  } catch (error) {
    errorMessage.value =
      error instanceof Error
        ? error.message
        : 'Failed to load rooms.'
  } finally {
    loadingRooms.value = false
  }
}

async function removeRoom(roomId?: number): Promise<void> {
  if (roomId === undefined) {
    return
  }

  if (!confirm('Delete this room?')) {
    return
  }

  try {
    await deleteRoom(roomId)
    await loadRooms()
  } catch (error) {
    alert(
      error instanceof Error
        ? error.message
        : 'Failed to delete room.',
    )
  }
}

async function logout(): Promise<void> {
  authStore.logout()

  await router.push({
    name: 'login',
  })
}

watch(selectedBuildingId, loadRooms)

onMounted(async () => {
  await authStore.fetchCurrentManager()
  await loadBuildings()
})
</script>

<template>
  <main class="rooms-page">
    <header class="page-header">
      <div>
        <h1>Rooms</h1>

        <p v-if="authStore.manager">
          Logged in as
          {{ authStore.manager.fullName }}
        </p>
      </div>

      <div class="header-actions">
        <RouterLink to="/buildings">
          Buildings
        </RouterLink>

        <button
          type="button"
          @click="logout"
        >
          Logout
        </button>
      </div>
    </header>

    <p
      v-if="errorMessage"
      class="error-message"
    >
      {{ errorMessage }}
    </p>

    <section class="building-selection">
      <label for="building">
        Select building
      </label>

      <select
        id="building"
        v-model="selectedBuildingId"
        :disabled="loadingBuildings"
      >
        <option
          :value="null"
          disabled
        >
          Select a building
        </option>

        <option
          v-for="building in buildings"
          :key="building.buildingId"
          :value="building.buildingId"
        >
          {{ building.buildingName }}
        </option>
      </select>

      <button
  type="button"
  :disabled="selectedBuildingId === null"
  @click="showCreateRoomModal = true"
>
  + Create Room
</button>
    </section>

    <p v-if="loadingRooms">
      Loading rooms...
    </p>

    <p
      v-else-if="
        selectedBuildingId !== null &&
        rooms.length === 0
      "
    >
      No rooms found in this building.
    </p>

    <section
      v-else
      class="room-grid"
    >
      <article
        v-for="room in rooms"
        :key="room.roomId"
        class="room-card"
      >
        <h2>{{ room.roomName }}</h2>

        <p>
          Rent: {{ room.rentAmount }}
        </p>

        <div>
          <strong>Additional charges</strong>

          <p
            v-if="
              !room.additionalCharges ||
              room.additionalCharges.length === 0
            "
          >
            None
          </p>

          <ul v-else>
            <li
              v-for="charge in room.additionalCharges"
              :key="
                charge.additionalChargeId ??
                `${charge.chargeName}-${charge.chargeAmount}`
              "
            >
              {{ charge.chargeName }}:
              {{ charge.chargeAmount }}
            </li>
          </ul>
        </div>

        <div class="room-actions">
          <button type="button">
            Edit
          </button>

          <button
            type="button"
            @click="removeRoom(room.roomId)"
          >
            Delete
          </button>
        </div>
      </article>
    </section>
    <CreateRoomModal
  v-if="showCreateRoomModal && selectedBuildingId !== null"
  :building-id="selectedBuildingId"
  @close="showCreateRoomModal = false"
/>
  </main>
</template>

<style scoped>
.rooms-page {
  max-width: 1000px;
  margin: 2rem auto;
  padding: 1rem;
}

.page-header,
.building-selection,
.header-actions,
.room-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.page-header {
  justify-content: space-between;
  margin-bottom: 2rem;
}

.building-selection {
  margin-bottom: 2rem;
}

.room-grid {
  display: grid;
  grid-template-columns:
    repeat(auto-fit, minmax(220px, 1fr));
  gap: 1rem;
}

.room-card {
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 1rem;
}

.error-message {
  color: #b00020;
}
</style>