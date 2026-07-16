<script setup lang="ts">
import {
  computed,
  onMounted,
  ref,
  watch,
} from 'vue'

import { getBuildings } from '@/services/buildingService'
import { getRooms } from '@/services/roomService'
import { getMeterReadingHistory } from '@/services/meterReadingService'

import type { Building } from '@/types/building'
import type { Room } from '@/types/room'
import type { RoomReadingEntry } from '@/types/meterReading'

import ListRecordingView from '@/components/meter-readings/ListRecordingView.vue'
import CardRecordingView from '@/components/meter-readings/CardRecordingView.vue'
import AppNavBar from '@/components/layout/AppNavbar.vue'

type RecordingMode = 'list' | 'card'

const buildings = ref<Building[]>([])
const rooms = ref<Room[]>([])
const roomEntries = ref<RoomReadingEntry[]>([])

const selectedBuildingId = ref<number | null>(
  null,
)

const mode = ref<RecordingMode>('list')
const recordingStarted = ref(false)

const loading = ref(false)
const errorMessage = ref('')

const selectedBuilding = computed(() =>
  buildings.value.find(
    (building) =>
      building.buildingId ===
      selectedBuildingId.value,
  ),
)

async function loadBuildings(): Promise<void> {
  loading.value = true
  errorMessage.value = ''

  try {
    buildings.value = await getBuildings()

    const firstBuilding = buildings.value.at(0)

    if (
      firstBuilding?.buildingId !== undefined
    ) {
      selectedBuildingId.value =
        firstBuilding.buildingId
    } else {
      selectedBuildingId.value = null
      rooms.value = []
      roomEntries.value = []
    }
  } catch (error) {
    errorMessage.value =
      error instanceof Error
        ? error.message
        : 'Failed to load buildings.'
  } finally {
    loading.value = false
  }
}

async function loadRoomsAndPreviousReadings(): Promise<void> {
  const buildingId =
    selectedBuildingId.value

  if (buildingId === null) {
    rooms.value = []
    roomEntries.value = []
    return
  }

  loading.value = true
  errorMessage.value = ''

  try {
    rooms.value = await getRooms(buildingId)

    const validRooms = rooms.value.filter(
      (
        room,
      ): room is Room & {
        roomId: number
      } => room.roomId !== undefined,
    )

    const entries = await Promise.all(
      validRooms.map(async (room) => {
        const history =
          await getMeterReadingHistory(
            room.roomId,
          )

        const electricityHistory =
          history.filter(
            (reading) =>
              reading.utilityType ===
              'ELECTRICITY',
          )

        const waterHistory =
          history.filter(
            (reading) =>
              reading.utilityType ===
              'WATER',
          )

        const previousElectricity =
          electricityHistory.at(0)
            ?.meterReadingValue ?? null

        const previousWater =
          waterHistory.at(0)
            ?.meterReadingValue ?? null

        return {
          roomId: room.roomId,
          roomName: room.roomName,

          previousElectricity,
          currentElectricity: null,

          previousWater,
          currentWater: null,

          electricityHistory,
          waterHistory,
        } satisfies RoomReadingEntry
      }),
    )

    roomEntries.value = entries
  } catch (error) {
    rooms.value = []
    roomEntries.value = []

    errorMessage.value =
      error instanceof Error
        ? error.message
        : 'Failed to load meter-reading data.'
  } finally {
    loading.value = false
  }
}

function startRecording(): void {
  errorMessage.value = ''

  if (roomEntries.value.length === 0) {
    errorMessage.value =
      'No rooms are available for recording.'
    return
  }

  recordingStarted.value = true
}

function stopRecording(): void {
  recordingStarted.value = false
}

async function handleSaved(): Promise<void> {
  recordingStarted.value = false
  await loadRoomsAndPreviousReadings()
}

watch(
  selectedBuildingId,
  async (
    newBuildingId,
    oldBuildingId,
  ) => {
    if (
      newBuildingId === oldBuildingId ||
      newBuildingId === null
    ) {
      return
    }

    recordingStarted.value = false
    await loadRoomsAndPreviousReadings()
  },
)

onMounted(loadBuildings)
</script>

<template>
  <AppNavBar />

  <main
    class="mx-auto min-h-screen max-w-7xl px-4 py-8 font-mono uppercase tracking-wider sm:px-6"
  >
    <header
      class="mb-8 flex flex-col gap-4 border-b border-black pb-5 sm:flex-row sm:items-end sm:justify-between"
    >
      <div>
        <p
          class="mb-2 text-xs font-semibold text-gray-500"
        >
          Meter Management
        </p>

        <h1
          class="text-3xl font-bold sm:text-4xl"
        >
          Meter Readings
        </h1>

        <p
          class="mt-2 max-w-2xl text-sm normal-case tracking-normal text-gray-500"
        >
          Record electricity and water readings
          and review previous meter history.
        </p>
      </div>

      <RouterLink
        to="/rooms"
        class="inline-flex w-fit items-center rounded-sm border border-black px-4 py-2 text-sm font-semibold transition hover:bg-black hover:text-white"
      >
        View Rooms →
      </RouterLink>
    </header>

    <div
      v-if="errorMessage"
      class="mb-6 border border-red-700 bg-red-50 px-4 py-3 text-sm text-red-700"
    >
      {{ errorMessage }}
    </div>

    <section
      v-if="!recordingStarted"
      class="mx-auto w-full max-w-2xl rounded-md border border-black bg-white p-6"
    >
      <header
        class="mb-6 border-b border-black pb-4"
      >
        <h2 class="text-2xl font-bold">
          Record Meter Readings
        </h2>

        <p
          class="mt-1 text-sm normal-case tracking-normal text-gray-500"
        >
          Choose a building and recording
          layout.
        </p>
      </header>

      <section class="mb-6">
        <div
          class="mb-3 flex items-center gap-2"
        >
          <span
            class="flex h-6 w-6 items-center justify-center rounded-full border border-black text-xs font-bold"
          >
            1
          </span>

          <h3 class="text-sm font-bold">
            Select Building
          </h3>
        </div>

        <select
          id="building"
          v-model="selectedBuildingId"
          :disabled="loading"
          class="w-full rounded-sm border border-black bg-neutral-100 px-3 py-2 text-sm outline-none transition hover:bg-white disabled:cursor-not-allowed disabled:border-gray-400 disabled:text-gray-400"
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
      </section>

      <section class="mb-6">
        <div
          class="mb-3 flex items-center gap-2"
        >
          <span
            class="flex h-6 w-6 items-center justify-center rounded-full border border-black text-xs font-bold"
          >
            2
          </span>

          <h3 class="text-sm font-bold">
            Choose Recording Layout
          </h3>
        </div>

        <div
          class="grid gap-3 sm:grid-cols-2"
        >
          <label class="cursor-pointer">
            <input
              v-model="mode"
              type="radio"
              value="list"
              class="peer sr-only"
            />

            <div
              class="h-full rounded-sm border border-black p-4 transition peer-checked:bg-black peer-checked:text-white"
            >
              <h4
                class="mb-1 text-sm font-bold"
              >
                List View
              </h4>

              <p
                class="text-xs normal-case tracking-normal"
              >
                Best for entering many rooms
                quickly in a table format.
              </p>
            </div>
          </label>

          <label class="cursor-pointer">
            <input
              v-model="mode"
              type="radio"
              value="card"
              class="peer sr-only"
            />

            <div
              class="h-full rounded-sm border border-black p-4 transition peer-checked:bg-black peer-checked:text-white"
            >
              <h4
                class="mb-1 text-sm font-bold"
              >
                Card View
              </h4>

              <p
                class="text-xs normal-case tracking-normal"
              >
                Best for reviewing individual
                rooms with more details and OCR.
              </p>
            </div>
          </label>
        </div>
      </section>

      <section
        class="mb-6 border border-black bg-neutral-50 p-4"
      >
        <p
          v-if="loading"
          class="text-sm"
        >
          Loading rooms and meter history...
        </p>

        <p
          v-else-if="
            selectedBuildingId !== null &&
            rooms.length === 0
          "
          class="text-sm"
        >
          This building has no rooms.
        </p>

        <div
          v-else-if="rooms.length > 0"
          class="flex items-center justify-between gap-4"
        >
          <span class="text-sm font-bold">
            Available Rooms
          </span>

          <span
            class="rounded-full border border-black px-3 py-1 text-xs font-bold"
          >
            {{ rooms.length }}
            {{
              rooms.length === 1
                ? 'Room'
                : 'Rooms'
            }}
          </span>
        </div>

        <p
          v-else
          class="text-sm text-gray-500"
        >
          Select a building to load rooms.
        </p>
      </section>

      <footer
        class="flex justify-end border-t border-black pt-4"
      >
        <button
          type="button"
          :disabled="
            selectedBuildingId === null ||
            loading ||
            roomEntries.length === 0
          "
          class="rounded-sm border border-black px-5 py-2 text-sm font-semibold transition hover:bg-black hover:text-white disabled:cursor-not-allowed disabled:border-gray-400 disabled:text-gray-400 disabled:hover:bg-white"
          @click="startRecording"
        >
          Start Recording →
        </button>
      </footer>
    </section>

    <ListRecordingView
      v-else-if="
        recordingStarted &&
        mode === 'list' &&
        selectedBuilding &&
        selectedBuildingId !== null
      "
      :building-id="selectedBuildingId"
      :building-name="
        selectedBuilding.buildingName
      "
      :entries="roomEntries"
      @cancel="stopRecording"
      @saved="handleSaved"
    />

    <CardRecordingView
      v-else-if="
        recordingStarted &&
        mode === 'card' &&
        selectedBuilding &&
        selectedBuildingId !== null
      "
      :building-id="selectedBuildingId"
      :building-name="
        selectedBuilding.buildingName
      "
      :entries="roomEntries"
      @cancel="stopRecording"
      @saved="handleSaved"
    />
  </main>
</template>