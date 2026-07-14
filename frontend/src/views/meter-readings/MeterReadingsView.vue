<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'

import { getBuildings } from '@/services/buildingService'
import { getRooms } from '@/services/roomService'
import { getMeterReadingHistory } from '@/services/meterReadingService'

import type { Building } from '@/types/building'
import type { Room } from '@/types/room'
import type { RoomReadingEntry } from '@/types/meterReading'

import ListRecordingView from '@/components/meter-readings/ListRecordingView.vue'
import CardRecordingView from '@/components/meter-readings/CardRecordingView.vue'

type RecordingMode = 'list' | 'card'

const buildings = ref<Building[]>([])
const rooms = ref<Room[]>([])
const roomEntries = ref<RoomReadingEntry[]>([])

const selectedBuildingId = ref<number | null>(null)
const mode = ref<RecordingMode>('list')
const recordingStarted = ref(false)

const loading = ref(false)
const errorMessage = ref('')

const selectedBuilding = computed(() =>
  buildings.value.find(
    (building) =>
      building.buildingId === selectedBuildingId.value,
  ),
)

async function loadBuildings(): Promise<void> {
  loading.value = true
  errorMessage.value = ''

  try {
    buildings.value = await getBuildings()

    const firstBuilding = buildings.value.at(0)

    if (firstBuilding?.buildingId !== undefined) {
      /*
       * The watcher below will automatically load the rooms
       * when this value changes.
       */
      selectedBuildingId.value = firstBuilding.buildingId
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
  const buildingId = selectedBuildingId.value

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
      (room): room is Room & { roomId: number } =>
        room.roomId !== undefined,
    )

    const entries = await Promise.all(
      validRooms.map(async (room) => {
        const history = await getMeterReadingHistory(
          room.roomId,
        )

        /*
         * The backend returns the newest readings first.
         * find() therefore gives us the latest reading
         * for each utility type.
         */
        const previousElectricity =
          history.find(
            (reading) =>
              reading.utilityType === 'ELECTRICITY',
          )?.meterReadingValue ?? null

        const previousWater =
          history.find(
            (reading) =>
              reading.utilityType === 'WATER',
          )?.meterReadingValue ?? null

        return {
          roomId: room.roomId,
          roomName: room.roomName,
          previousElectricity,
          currentElectricity: null,
          previousWater,
          currentWater: null,
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
  async (newBuildingId, oldBuildingId) => {
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
  <main class="meter-readings-page">
    <header class="page-header">
      <div>
        <h1>Meter Readings</h1>

        <p>
          Record electricity and water readings.
        </p>
      </div>

      <RouterLink to="/rooms">
        Rooms
      </RouterLink>
    </header>

    <p
      v-if="errorMessage"
      class="error-message"
    >
      {{ errorMessage }}
    </p>

    <section
      v-if="!recordingStarted"
      class="setup-card"
    >
      <div class="form-group">
        <label for="building">
          Building
        </label>

        <select
          id="building"
          v-model="selectedBuildingId"
          :disabled="loading"
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
      </div>

      <fieldset class="view-picker">
        <legend>Recording View</legend>

        <label>
          <input
            v-model="mode"
            type="radio"
            value="list"
          />
          List View
        </label>

        <label>
          <input
            v-model="mode"
            type="radio"
            value="card"
          />
          Card View
        </label>
      </fieldset>

      <p v-if="loading">
        Loading rooms...
      </p>

      <p
        v-else-if="
          selectedBuildingId !== null &&
          rooms.length === 0
        "
      >
        This building has no rooms.
      </p>

      <p
        v-else-if="rooms.length > 0"
        class="room-count"
      >
        {{ rooms.length }}
        {{ rooms.length === 1 ? 'room' : 'rooms' }}
        available.
      </p>

      <button
        type="button"
        :disabled="
          selectedBuildingId === null ||
          loading ||
          roomEntries.length === 0
        "
        @click="startRecording"
      >
        Start Recording
      </button>
    </section>

    <ListRecordingView
      v-else-if="
        recordingStarted &&
        mode === 'list' &&
        selectedBuilding &&
        selectedBuildingId !== null
      "
      :building-id="selectedBuildingId"
      :building-name="selectedBuilding.buildingName"
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
      :building-name="selectedBuilding.buildingName"
      :entries="roomEntries"
      @cancel="stopRecording"
      @saved="handleSaved"
    />
  </main>
</template>

<style scoped>
.meter-readings-page {
  max-width: 1100px;
  margin: 2rem auto;
  padding: 1rem;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 2rem;
}

.page-header h1 {
  margin-bottom: 0.25rem;
}

.page-header p {
  margin-top: 0;
}

.setup-card {
  max-width: 560px;
  padding: 1.5rem;
  border: 1px solid #cccccc;
  border-radius: 10px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
  margin-bottom: 1rem;
}

.form-group select {
  padding: 0.7rem;
}

.view-picker {
  display: flex;
  gap: 1.5rem;
  margin-bottom: 1.25rem;
  padding: 1rem;
}

.view-picker label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

button {
  padding: 0.7rem 1rem;
  cursor: pointer;
}

button:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.error-message {
  color: #b00020;
}

.room-count {
  margin-bottom: 1rem;
}
</style>