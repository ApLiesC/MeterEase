<script setup lang="ts">
import { ref } from 'vue'

import { recordMeterReadings } from '@/services/meterReadingService'

import type {
  MeterReadingRequest,
  RoomReadingEntry,
} from '@/types/meterReading'

const props = defineProps<{
  buildingId: number
  buildingName: string
  entries: RoomReadingEntry[]
}>()

const emit = defineEmits<{
  cancel: []
  saved: []
}>()

const editableEntries = ref<RoomReadingEntry[]>(
  props.entries.map((entry) => ({
    ...entry,
  })),
)

const loading = ref(false)
const errorMessage = ref('')

async function submitReadings(): Promise<void> {
  errorMessage.value = ''

  const readings: MeterReadingRequest[] = []

  for (const entry of editableEntries.value) {
    if (entry.currentElectricity !== null) {
      if (
        entry.previousElectricity !== null &&
        entry.currentElectricity <
          entry.previousElectricity
      ) {
        errorMessage.value =
          `Electricity reading for room ${entry.roomName} cannot be lower than the previous reading.`
        return
      }

      readings.push({
        roomId: entry.roomId,
        utilityType: 'ELECTRICITY',
        meterReadingValue:
          entry.currentElectricity,
        meterImage: null,
      })
    }

    if (entry.currentWater !== null) {
      if (
        entry.previousWater !== null &&
        entry.currentWater <
          entry.previousWater
      ) {
        errorMessage.value =
          `Water reading for room ${entry.roomName} cannot be lower than the previous reading.`
        return
      }

      readings.push({
        roomId: entry.roomId,
        utilityType: 'WATER',
        meterReadingValue:
          entry.currentWater,
        meterImage: null,
      })
    }
  }

  if (readings.length === 0) {
    errorMessage.value =
      'Enter at least one meter reading.'
    return
  }

  loading.value = true

  try {
    await recordMeterReadings(
      props.buildingId,
      readings,
    )

    emit('saved')
  } catch (error) {
    errorMessage.value =
      error instanceof Error
        ? error.message
        : 'Failed to save meter readings.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <section class="recording-panel">
    <header class="recording-header">
      <div>
        <h2>Record Meter Readings</h2>
        <p>{{ buildingName }}</p>
      </div>

      <button
        type="button"
        @click="emit('cancel')"
      >
        Back
      </button>
    </header>

    <div class="table-wrapper">
      <table>
        <thead>
          <tr>
            <th>Room</th>
            <th>Electricity Previous</th>
            <th>Electricity Current</th>
            <th>Water Previous</th>
            <th>Water Current</th>
          </tr>
        </thead>

        <tbody>
          <tr
            v-for="entry in editableEntries"
            :key="entry.roomId"
          >
            <td>
              {{ entry.roomName }}
            </td>

            <td>
              {{
                entry.previousElectricity ??
                'No previous reading'
              }}
            </td>

            <td>
              <input
                v-model.number="
                  entry.currentElectricity
                "
                type="number"
                min="0"
                step="1"
                :placeholder="
                  entry.previousElectricity !== null
                    ? String(
                        entry.previousElectricity,
                      )
                    : 'Enter reading'
                "
              />
            </td>

            <td>
              {{
                entry.previousWater ??
                'No previous reading'
              }}
            </td>

            <td>
              <input
                v-model.number="entry.currentWater"
                type="number"
                min="0"
                step="1"
                :placeholder="
                  entry.previousWater !== null
                    ? String(entry.previousWater)
                    : 'Enter reading'
                "
              />
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <p
      v-if="errorMessage"
      class="error-message"
    >
      {{ errorMessage }}
    </p>

    <footer class="recording-actions">
      <button
        type="button"
        :disabled="loading"
        @click="emit('cancel')"
      >
        Cancel
      </button>

      <button
        type="button"
        :disabled="loading"
        @click="submitReadings"
      >
        {{
          loading
            ? 'Saving...'
            : 'Submit Readings'
        }}
      </button>
    </footer>
  </section>
</template>

<style scoped>
.recording-panel {
  padding: 1.5rem;
  border: 1px solid #cccccc;
  border-radius: 10px;
}

.recording-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.recording-header h2 {
  margin: 0;
}

.recording-header p {
  margin: 0.4rem 0 0;
}

.table-wrapper {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th,
td {
  padding: 0.8rem;
  border: 1px solid #dddddd;
  text-align: left;
}

th {
  background: #f4f4f4;
}

input {
  width: 100%;
  min-width: 120px;
  box-sizing: border-box;
  padding: 0.55rem;
}

.recording-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  margin-top: 1.5rem;
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
  margin-top: 1rem;
  color: #b00020;
}
</style>