<script setup lang="ts">
import { computed, ref } from 'vue'
import { scanMeterImage } from '@/services/meterOcrService'
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
const electricityOcrLoading = ref(false)
const waterOcrLoading = ref(false)

const electricityImageName = ref('')
const waterImageName = ref('')

const electricityOcrMessage = ref('')
const waterOcrMessage = ref('')
const currentIndex = ref(0)
const electricityReading = ref<number | null>(null)
const waterReading = ref<number | null>(null)

const loading = ref(false)
const errorMessage = ref('')

const currentEntry = computed(
  () => props.entries[currentIndex.value],
)

const isFirstRoom = computed(
  () => currentIndex.value === 0,
)

const isLastRoom = computed(
  () =>
    props.entries.length === 0 ||
    currentIndex.value === props.entries.length - 1,
)

const progressText = computed(() => {
  if (props.entries.length === 0) {
    return '0 / 0'
  }

  return `${currentIndex.value + 1} / ${props.entries.length}`
})

function resetInputs(): void {
  electricityReading.value = null
  waterReading.value = null

  electricityImageName.value = ''
  waterImageName.value = ''

  electricityOcrMessage.value = ''
  waterOcrMessage.value = ''

  errorMessage.value = ''
}

function goToPreviousRoom(): void {
  if (isFirstRoom.value || loading.value) {
    return
  }

  currentIndex.value -= 1
  resetInputs()
}

function skipRoom(): void {
  if (isLastRoom.value || loading.value) {
    return
  }

  currentIndex.value += 1
  resetInputs()
}

async function scanElectricityMeter(
  event: Event,
): Promise<void> {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]

  if (!file) {
    return
  }

  electricityOcrLoading.value = true
  electricityOcrMessage.value = ''
  errorMessage.value = ''
  electricityImageName.value = file.name

  try {
    const result = await scanMeterImage(file)

    if (result.suggestedReading === null) {
      electricityOcrMessage.value =
        'No clear electricity reading was detected.'
      return
    }

    electricityReading.value =
      result.suggestedReading

    electricityOcrMessage.value =
      `Detected reading: ${result.suggestedReading}. Please confirm it before saving.`
  } catch (error) {
    errorMessage.value =
      error instanceof Error
        ? error.message
        : 'Failed to scan the electricity meter.'
  } finally {
    electricityOcrLoading.value = false
    input.value = ''
  }
}

async function scanWaterMeter(
  event: Event,
): Promise<void> {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]

  if (!file) {
    return
  }

  waterOcrLoading.value = true
  waterOcrMessage.value = ''
  errorMessage.value = ''
  waterImageName.value = file.name

  try {
    const result = await scanMeterImage(file)

    if (result.suggestedReading === null) {
      waterOcrMessage.value =
        'No clear water reading was detected.'
      return
    }

    waterReading.value =
      result.suggestedReading

    waterOcrMessage.value =
      `Detected reading: ${result.suggestedReading}. Please confirm it before saving.`
  } catch (error) {
    errorMessage.value =
      error instanceof Error
        ? error.message
        : 'Failed to scan the water meter.'
  } finally {
    waterOcrLoading.value = false
    input.value = ''
  }
}

async function saveCurrentRoom(): Promise<void> {
  errorMessage.value = ''

  const entry = currentEntry.value

  if (!entry) {
    errorMessage.value =
      'Room information is unavailable.'
    return
  }

  if (
    electricityReading.value === null &&
    waterReading.value === null
  ) {
    errorMessage.value =
      'Enter at least one meter reading.'
    return
  }

  if (
    electricityReading.value !== null &&
    entry.previousElectricity !== null &&
    electricityReading.value <
      entry.previousElectricity
  ) {
    errorMessage.value =
      'Electricity reading cannot be lower than the previous reading.'
    return
  }

  if (
    waterReading.value !== null &&
    entry.previousWater !== null &&
    waterReading.value < entry.previousWater
  ) {
    errorMessage.value =
      'Water reading cannot be lower than the previous reading.'
    return
  }

  const readings: MeterReadingRequest[] = []

  if (electricityReading.value !== null) {
    readings.push({
      roomId: entry.roomId,
      utilityType: 'ELECTRICITY',
      meterReadingValue:
        electricityReading.value,
      meterImage: null,
    })
  }

  if (waterReading.value !== null) {
    readings.push({
      roomId: entry.roomId,
      utilityType: 'WATER',
      meterReadingValue:
        waterReading.value,
      meterImage: null,
    })
  }

  loading.value = true

  try {
    await recordMeterReadings(
      props.buildingId,
      readings,
    )

    if (isLastRoom.value) {
      emit('saved')
      return
    }

    currentIndex.value += 1
    resetInputs()
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
        :disabled="loading"
        @click="emit('cancel')"
      >
        Back
      </button>
    </header>

    <p
      v-if="entries.length === 0"
      class="empty-message"
    >
      No rooms found in this building.
    </p>

    <article
      v-else-if="currentEntry"
      class="room-card"
    >
      <header class="room-card-header">
        <div>
          <h3>
            Room {{ currentEntry.roomName }}
          </h3>

          <p>
            Room {{ progressText }}
          </p>
        </div>
      </header>

      <section class="reading-section">
        <h4>Electricity</h4>

        <p>
          Previous:
          {{
            currentEntry.previousElectricity ??
            'No previous reading'
          }}
        </p>

        <label for="electricityReading">
          Current reading
        </label>

        <input
          id="electricityReading"
          v-model.number="electricityReading"
          type="number"
          min="0"
          step="1"
          placeholder="Enter electricity reading"
        />
      </section>
<div class="ocr-control">
  <label for="electricityMeterImage">
    Electricity meter image
  </label>

  <input
    id="electricityMeterImage"
    type="file"
    accept="image/*"
    capture="environment"
    :disabled="electricityOcrLoading || loading"
    @change="scanElectricityMeter"
  />

  <p v-if="electricityImageName">
    Selected: {{ electricityImageName }}
  </p>

  <p v-if="electricityOcrLoading">
    Scanning electricity meter...
  </p>

  <p
    v-else-if="electricityOcrMessage"
    class="ocr-message"
  >
    {{ electricityOcrMessage }}
  </p>
</div>
      <section class="reading-section">
        <h4>Water</h4>

        <p>
          Previous:
          {{
            currentEntry.previousWater ??
            'No previous reading'
          }}
        </p>

        <label for="waterReading">
          Current reading
        </label>

        <input
          id="waterReading"
          v-model.number="waterReading"
          type="number"
          min="0"
          step="1"
          placeholder="Enter water reading"
        />
      </section>
<div class="ocr-control">
  <label for="waterMeterImage">
    Water meter image
  </label>

  <input
    id="waterMeterImage"
    type="file"
    accept="image/*"
    capture="environment"
    :disabled="waterOcrLoading || loading"
    @change="scanWaterMeter"
  />

  <p v-if="waterImageName">
    Selected: {{ waterImageName }}
  </p>

  <p v-if="waterOcrLoading">
    Scanning water meter...
  </p>

  <p
    v-else-if="waterOcrMessage"
    class="ocr-message"
  >
    {{ waterOcrMessage }}
  </p>
</div>

      <p
        v-if="errorMessage"
        class="error-message"
      >
        {{ errorMessage }}
      </p>

      <footer class="card-actions">
        <button
          type="button"
          :disabled="isFirstRoom || loading"
          @click="goToPreviousRoom"
        >
          Previous Room
        </button>

        <button
          type="button"
          :disabled="loading"
          @click="saveCurrentRoom"
        >
          {{
            loading
              ? 'Saving...'
              : isLastRoom
                ? 'Save and Finish'
                : 'Save and Next'
          }}
        </button>

        <button
          type="button"
          :disabled="isLastRoom || loading"
          @click="skipRoom"
        >
          Skip Room
        </button>
      </footer>
    </article>
  </section>
</template>

<style scoped>
.recording-panel {
  max-width: 680px;
  margin: 0 auto;
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

.room-card {
  border: 1px solid #dddddd;
  border-radius: 10px;
  padding: 1.25rem;
}

.room-card-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1.25rem;
}

.room-card-header h3 {
  margin: 0;
}

.room-card-header p {
  margin: 0.4rem 0 0;
}

.reading-section {
  margin-bottom: 1.5rem;
}

.reading-section h4 {
  margin-bottom: 0.5rem;
}

.reading-section label {
  display: block;
  margin-bottom: 0.4rem;
}

.reading-section input {
  width: 100%;
  box-sizing: border-box;
  padding: 0.7rem;
}

.ocr-placeholder {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
  margin-bottom: 1.5rem;
}

.card-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
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
  color: #b00020;
}

.empty-message {
  text-align: center;
}

.ocr-control {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
  margin-top: 0.8rem;
  padding: 0.9rem;
  border: 1px solid #dddddd;
  border-radius: 8px;
}

.ocr-control input[type='file'] {
  padding: 0.4rem 0;
}

.ocr-control p {
  margin: 0;
  font-size: 0.9rem;
}

.ocr-message {
  color: #087830;
}
</style>