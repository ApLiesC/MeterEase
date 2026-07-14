<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'

import CreateRoomModal from '@/components/rooms/CreateRoomModal.vue'
import EditRoomModal from '@/components/rooms/EditRoomModal.vue'
import RoomList from '@/components/rooms/RoomList.vue'
import AppNavBar from '@/components/layout/AppNavbar.vue'

import { getBuildings } from '@/services/buildingService'
import {
  deleteRoom,
  getRooms,
} from '@/services/roomService'

import type { Building } from '@/types/building'
import type { Room } from '@/types/room'


const buildings = ref<Building[]>([])
const rooms = ref<Room[]>([])

const selectedBuildingId = ref<number | null>(null)

const loadingBuildings = ref(false)
const loadingRooms = ref(false)

const showCreateRoomModal = ref(false)
const showEditRoomModal = ref(false)

const errorMessage = ref('')

const selectedRoom = ref<Room | null>(null)


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
        buildings.value.at(0)?.buildingId ?? null
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


function openEditRoom(room: Room): void {
  selectedRoom.value = room
  showEditRoomModal.value = true
}


function closeEditRoom(): void {
  showEditRoomModal.value = false
  selectedRoom.value = null
}


async function handleRoomUpdated(): Promise<void> {
  closeEditRoom()
  await loadRooms()
}


async function removeRoom(roomId: number): Promise<void> {
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


async function handleRoomCreated(): Promise<void> {
  showCreateRoomModal.value = false
  await loadRooms()
}


watch(
  selectedBuildingId,
  loadRooms,
)


onMounted(async () => {
  await loadBuildings()
})
</script>


<template>
  <AppNavBar />

  <main class="rooms-page">

    <header class="page-header">
      <h1>
        Rooms
      </h1>
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


    <RoomList
      v-else
      :rooms="rooms"
      @edit="openEditRoom"
      @delete="removeRoom"
    />


    <CreateRoomModal
      v-if="
        showCreateRoomModal &&
        selectedBuildingId !== null
      "
      :building-id="selectedBuildingId"
      @close="showCreateRoomModal = false"
      @created="handleRoomCreated"
    />


    <EditRoomModal
      v-if="
        showEditRoomModal &&
        selectedRoom
      "
      :room="selectedRoom"
      @close="closeEditRoom"
      @updated="handleRoomUpdated"
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
.building-selection {
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


.error-message {
  color: #b00020;
}

</style>