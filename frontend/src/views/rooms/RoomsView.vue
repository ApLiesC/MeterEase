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

  <main
    class="mx-auto max-w-5xl p-4 font-mono uppercase tracking-wider"
  >
    <!-- Page Header -->
    <header
      class="mb-8 flex flex-col gap-3 border-b border-black pb-5 sm:flex-row sm:items-center sm:justify-between"
    >
      <div>
        <h1 class="text-3xl font-bold">
          Rooms
        </h1>

        <p class="mt-1 text-s text-gray-500">
          Manage room information and charges
        </p>
      </div>
    </header>

    <!-- Error -->
    <p
      v-if="errorMessage"
      class="mb-5 rounded-sm border border-black bg-neutral-100 p-3 text-sm text-red-600"
    >
      {{ errorMessage }}
    </p>

    <!-- Controls -->
    <section
      class="mb-8 flex flex-col gap-4 rounded-md border border-black bg-neutral-100 p-4 sm:flex-row sm:items-center"
    >
      <label
        for="building"
        class="text-sm font-semibold"
      >
        Select Building
      </label>

      <select
        id="building"
        v-model="selectedBuildingId"
        :disabled="loadingBuildings"
        class="w-full rounded-sm border border-black bg-neutral-100 px-3 py-2 text-sm outline-none transition hover:bg-white sm:w-auto"
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
        class="rounded-sm border border-black px-4 py-2 text-sm font-semibold transition hover:bg-black hover:text-white disabled:cursor-not-allowed disabled:border-gray-400 disabled:text-gray-400"
        @click="showCreateRoomModal = true"
      >
        + Create Room
      </button>
    </section>

    <!-- Loading -->
    <p
      v-if="loadingRooms"
      class="border border-black p-4 text-sm text-gray-600"
    >
      Loading rooms...
    </p>

    <!-- Empty -->
    <p
      v-else-if="
        selectedBuildingId !== null &&
        rooms.length === 0
      "
      class="border border-black p-4 text-sm text-gray-500"
    >
      No rooms found in this building.
    </p>

    <!-- Room Cards -->
    <RoomList
      v-else
      :rooms="rooms"
      @edit="openEditRoom"
      @delete="removeRoom"
    />

    <!-- Modals -->
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