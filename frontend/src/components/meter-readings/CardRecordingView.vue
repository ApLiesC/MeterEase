<script setup lang="ts">
import {
  computed,
  ref,
} from 'vue'

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

const HISTORY_PAGE_SIZE = 5

const electricityOcrLoading =
  ref(false)

const waterOcrLoading = ref(false)

const electricityImageName = ref('')
const waterImageName = ref('')

const electricityOcrMessage = ref('')
const waterOcrMessage = ref('')

const currentIndex = ref(0)

const electricityReading =
  ref<number | null>(null)

const waterReading =
  ref<number | null>(null)

const electricityHistoryPage =
  ref(1)

const waterHistoryPage = ref(1)

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
    currentIndex.value ===
      props.entries.length - 1,
)

const progressText = computed(() => {
  if (props.entries.length === 0) {
    return '0 / 0'
  }

  return `${currentIndex.value + 1} / ${props.entries.length}`
})

const electricityHistoryPageCount =
  computed(() =>
    Math.max(
      1,
      Math.ceil(
        (currentEntry.value
          ?.electricityHistory.length ??
          0) / HISTORY_PAGE_SIZE,
      ),
    ),
  )

const waterHistoryPageCount =
  computed(() =>
    Math.max(
      1,
      Math.ceil(
        (currentEntry.value
          ?.waterHistory.length ??
          0) / HISTORY_PAGE_SIZE,
      ),
    ),
  )

const paginatedElectricityHistory =
  computed(() => {
    const history =
      currentEntry.value
        ?.electricityHistory ?? []

    const startIndex =
      (electricityHistoryPage.value -
        1) *
      HISTORY_PAGE_SIZE

    return history.slice(
      startIndex,
      startIndex + HISTORY_PAGE_SIZE,
    )
  })

const paginatedWaterHistory =
  computed(() => {
    const history =
      currentEntry.value?.waterHistory ??
      []

    const startIndex =
      (waterHistoryPage.value - 1) *
      HISTORY_PAGE_SIZE

    return history.slice(
      startIndex,
      startIndex + HISTORY_PAGE_SIZE,
    )
  })

function formatRecordedDate(
  recordedDateTime: string,
): string {
  const date = new Date(recordedDateTime)

  if (Number.isNaN(date.getTime())) {
    return recordedDateTime
  }

  return new Intl.DateTimeFormat(
    'en-GB',
    {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    },
  ).format(date)
}

function resetInputs(): void {
  electricityReading.value = null
  waterReading.value = null

  electricityImageName.value = ''
  waterImageName.value = ''

  electricityOcrMessage.value = ''
  waterOcrMessage.value = ''

  electricityHistoryPage.value = 1
  waterHistoryPage.value = 1

  errorMessage.value = ''
}

function goToPreviousRoom(): void {
  if (
    isFirstRoom.value ||
    loading.value
  ) {
    return
  }

  currentIndex.value -= 1
  resetInputs()
}

function skipRoom(): void {
  if (
    isLastRoom.value ||
    loading.value
  ) {
    return
  }

  currentIndex.value += 1
  resetInputs()
}

function previousElectricityHistoryPage(): void {
  if (
    electricityHistoryPage.value > 1
  ) {
    electricityHistoryPage.value -= 1
  }
}

function nextElectricityHistoryPage(): void {
  if (
    electricityHistoryPage.value <
    electricityHistoryPageCount.value
  ) {
    electricityHistoryPage.value += 1
  }
}

function previousWaterHistoryPage(): void {
  if (waterHistoryPage.value > 1) {
    waterHistoryPage.value -= 1
  }
}

function nextWaterHistoryPage(): void {
  if (
    waterHistoryPage.value <
    waterHistoryPageCount.value
  ) {
    waterHistoryPage.value += 1
  }
}

async function scanElectricityMeter(
  event: Event,
): Promise<void> {
  const input =
    event.target as HTMLInputElement

  const file = input.files?.[0]

  if (!file) {
    return
  }

  electricityOcrLoading.value = true
  electricityOcrMessage.value = ''
  errorMessage.value = ''
  electricityImageName.value = file.name

  try {
    const result =
      await scanMeterImage(file)

    if (
      result.suggestedReading === null
    ) {
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
  const input =
    event.target as HTMLInputElement

  const file = input.files?.[0]

  if (!file) {
    return
  }

  waterOcrLoading.value = true
  waterOcrMessage.value = ''
  errorMessage.value = ''
  waterImageName.value = file.name

  try {
    const result =
      await scanMeterImage(file)

    if (
      result.suggestedReading === null
    ) {
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
    electricityReading.value > 99999
  ) {
    errorMessage.value =
      'Electricity reading cannot exceed 99,999.'
    return
  }

  if (
    waterReading.value !== null &&
    waterReading.value > 99999
  ) {
    errorMessage.value =
      'Water reading cannot exceed 99,999.'
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
    waterReading.value <
      entry.previousWater
  ) {
    errorMessage.value =
      'Water reading cannot be lower than the previous reading.'
    return
  }

  const readings: MeterReadingRequest[] =
    []

  if (
    electricityReading.value !== null
  ) {
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
  <section
    class="mx-auto w-full max-w-3xl rounded-md border border-black bg-white p-4 font-mono uppercase tracking-wider sm:p-6"
  >
    <header
      class="mb-6 flex items-start justify-between gap-4 border-b border-black pb-4"
    >
      <div>
        <p
          class="mb-1 text-xs font-semibold text-gray-500"
        >
          Card Recording View
        </p>

        <h2 class="text-2xl font-bold">
          Record Meter Readings
        </h2>

        <p
          class="mt-1 text-sm normal-case tracking-normal text-gray-500"
        >
          {{ buildingName }}
        </p>
      </div>

      <button
        type="button"
        :disabled="loading"
        class="rounded-sm border border-black px-4 py-2 text-sm font-semibold transition hover:bg-red-400 disabled:cursor-not-allowed disabled:border-gray-400 disabled:text-gray-400"
        @click="emit('cancel')"
      >
        ← Back
      </button>
    </header>

    <div
      v-if="entries.length === 0"
      class="border border-black bg-neutral-100 p-6 text-center text-sm"
    >
      No rooms found in this building.
    </div>

    <article
      v-else-if="currentEntry"
      class="border border-black"
    >
      <header
        class="flex items-center justify-between gap-4 border-b border-black bg-black px-4 py-3 text-white"
      >
        <div>
          <h3 class="text-xl font-bold">
            Room {{ currentEntry.roomName }}
          </h3>

          <p
            class="mt-1 text-xs normal-case tracking-normal text-gray-300"
          >
            Enter or scan the current meter
            readings.
          </p>
        </div>

        <span
          class="rounded-full border border-white px-3 py-1 text-xs font-bold"
        >
          {{ progressText }}
        </span>
      </header>

      <div
        class="grid gap-0 md:grid-cols-2"
      >
        <section
          class="border-b border-black p-4 md:border-b-0 md:border-r"
        >
          <div
            class="mb-4 flex items-center justify-between gap-3 border-b border-black pb-2"
          >
            <h4 class="font-bold">
              Electricity
            </h4>

            <span
              class="text-xs normal-case tracking-normal text-gray-500"
            >
              Previous:
              {{
                currentEntry.previousElectricity ??
                'None'
              }}
            </span>
          </div>

          <label
            for="electricityReading"
            class="mb-2 block text-xs font-bold"
          >
            Current Reading
          </label>

          <input
            id="electricityReading"
            v-model.number="
              electricityReading
            "
            type="number"
            min="0"
            max="99999"
            step="1"
            class="w-full rounded-sm border border-black bg-neutral-100 px-3 py-2 text-sm outline-none transition focus:bg-white"
            placeholder="Enter electricity reading"
          />

          <div
            class="mt-4 border border-black bg-neutral-50 p-3"
          >
            <label
              for="electricityMeterImage"
              class="mb-2 block text-xs font-bold"
            >
              Electricity Meter Image
            </label>

            <input
              id="electricityMeterImage"
              type="file"
              accept="image/*"
              capture="environment"
              :disabled="
                electricityOcrLoading ||
                loading
              "
              class="w-full text-xs file:mr-3 file:border file:border-black file:bg-white file:px-3 file:py-1 file:font-mono file:text-xs file:font-bold file:uppercase file:transition hover:file:bg-black hover:file:text-white disabled:cursor-not-allowed"
              @change="
                scanElectricityMeter
              "
            />

            <p
              v-if="electricityImageName"
              class="mt-2 text-xs normal-case tracking-normal text-gray-500"
            >
              Selected:
              {{ electricityImageName }}
            </p>

            <p
              v-if="electricityOcrLoading"
              class="mt-2 text-xs"
            >
              Scanning electricity meter...
            </p>

            <p
              v-else-if="
                electricityOcrMessage
              "
              class="mt-2 border-l-4 border-green-700 pl-2 text-xs normal-case tracking-normal text-green-700"
            >
              {{ electricityOcrMessage }}
            </p>
          </div>
        </section>

        <section class="p-4">
          <div
            class="mb-4 flex items-center justify-between gap-3 border-b border-black pb-2"
          >
            <h4 class="font-bold">
              Water
            </h4>

            <span
              class="text-xs normal-case tracking-normal text-gray-500"
            >
              Previous:
              {{
                currentEntry.previousWater ??
                'None'
              }}
            </span>
          </div>

          <label
            for="waterReading"
            class="mb-2 block text-xs font-bold"
          >
            Current Reading
          </label>

          <input
            id="waterReading"
            v-model.number="waterReading"
            type="number"
            min="0"
            max="99999"
            step="1"
            class="w-full rounded-sm border border-black bg-neutral-100 px-3 py-2 text-sm outline-none transition focus:bg-white"
            placeholder="Enter water reading"
          />

          <div
            class="mt-4 border border-black bg-neutral-50 p-3"
          >
            <label
              for="waterMeterImage"
              class="mb-2 block text-xs font-bold"
            >
              Water Meter Image
            </label>

            <input
              id="waterMeterImage"
              type="file"
              accept="image/*"
              capture="environment"
              :disabled="
                waterOcrLoading || loading
              "
              class="w-full text-xs file:mr-3 file:border file:border-black file:bg-white file:px-3 file:py-1 file:font-mono file:text-xs file:font-bold file:uppercase file:transition hover:file:bg-black hover:file:text-white disabled:cursor-not-allowed"
              @change="scanWaterMeter"
            />

            <p
              v-if="waterImageName"
              class="mt-2 text-xs normal-case tracking-normal text-gray-500"
            >
              Selected:
              {{ waterImageName }}
            </p>

            <p
              v-if="waterOcrLoading"
              class="mt-2 text-xs"
            >
              Scanning water meter...
            </p>

            <p
              v-else-if="waterOcrMessage"
              class="mt-2 border-l-4 border-green-700 pl-2 text-xs normal-case tracking-normal text-green-700"
            >
              {{ waterOcrMessage }}
            </p>
          </div>
        </section>
      </div>

      <details
        class="border-t border-black"
      >
        <summary
          class="cursor-pointer px-4 py-3 text-sm font-bold transition hover:bg-black hover:text-white"
        >
          View Previous Meter History
        </summary>

        <div
          class="grid gap-4 border-t border-black bg-neutral-50 p-4 md:grid-cols-2"
        >
          <section
            class="border border-black bg-white p-3"
          >
            <h4
              class="mb-3 border-b border-black pb-2 text-sm font-bold"
            >
              Electricity History
            </h4>

            <p
              v-if="
                currentEntry
                  .electricityHistory
                  .length === 0
              "
              class="text-xs normal-case tracking-normal text-gray-500"
            >
              No previous electricity
              readings.
            </p>

            <template v-else>
              <ul class="space-y-1">
                <li
                  v-for="
                    reading in
                      paginatedElectricityHistory
                  "
                  :key="
                    reading.meterReadingId
                  "
                  class="flex items-center justify-between gap-3 border-b border-gray-300 py-2 text-xs"
                >
                  <span
                    class="normal-case tracking-normal text-gray-600"
                  >
                    {{
                      formatRecordedDate(
                        reading.recordedDateTime,
                      )
                    }}
                  </span>

                  <strong>
                    {{
                      reading.meterReadingValue
                    }}
                  </strong>
                </li>
              </ul>

              <div
                v-if="
                  currentEntry
                    .electricityHistory
                    .length >
                  HISTORY_PAGE_SIZE
                "
                class="mt-4 flex items-center justify-between gap-2"
              >
                <button
                  type="button"
                  :disabled="
                    electricityHistoryPage ===
                    1
                  "
                  class="border border-black px-3 py-1 text-xs font-bold transition hover:bg-black hover:text-white disabled:cursor-not-allowed disabled:border-gray-400 disabled:text-gray-400"
                  @click="
                    previousElectricityHistoryPage
                  "
                >
                  Previous
                </button>

                <span
                  class="text-xs font-bold"
                >
                  Page
                  {{ electricityHistoryPage }}
                  /
                  {{
                    electricityHistoryPageCount
                  }}
                </span>

                <button
                  type="button"
                  :disabled="
                    electricityHistoryPage ===
                    electricityHistoryPageCount
                  "
                  class="border border-black px-3 py-1 text-xs font-bold transition hover:bg-black hover:text-white disabled:cursor-not-allowed disabled:border-gray-400 disabled:text-gray-400"
                  @click="
                    nextElectricityHistoryPage
                  "
                >
                  Next
                </button>
              </div>
            </template>
          </section>

          <section
            class="border border-black bg-white p-3"
          >
            <h4
              class="mb-3 border-b border-black pb-2 text-sm font-bold"
            >
              Water History
            </h4>

            <p
              v-if="
                currentEntry.waterHistory
                  .length === 0
              "
              class="text-xs normal-case tracking-normal text-gray-500"
            >
              No previous water readings.
            </p>

            <template v-else>
              <ul class="space-y-1">
                <li
                  v-for="
                    reading in
                      paginatedWaterHistory
                  "
                  :key="
                    reading.meterReadingId
                  "
                  class="flex items-center justify-between gap-3 border-b border-gray-300 py-2 text-xs"
                >
                  <span
                    class="normal-case tracking-normal text-gray-600"
                  >
                    {{
                      formatRecordedDate(
                        reading.recordedDateTime,
                      )
                    }}
                  </span>

                  <strong>
                    {{
                      reading.meterReadingValue
                    }}
                  </strong>
                </li>
              </ul>

              <div
                v-if="
                  currentEntry.waterHistory
                    .length >
                  HISTORY_PAGE_SIZE
                "
                class="mt-4 flex items-center justify-between gap-2"
              >
                <button
                  type="button"
                  :disabled="
                    waterHistoryPage === 1
                  "
                  class="border border-black px-3 py-1 text-xs font-bold transition hover:bg-black hover:text-white disabled:cursor-not-allowed disabled:border-gray-400 disabled:text-gray-400"
                  @click="
                    previousWaterHistoryPage
                  "
                >
                  Previous
                </button>

                <span
                  class="text-xs font-bold"
                >
                  Page
                  {{ waterHistoryPage }}
                  /
                  {{
                    waterHistoryPageCount
                  }}
                </span>

                <button
                  type="button"
                  :disabled="
                    waterHistoryPage ===
                    waterHistoryPageCount
                  "
                  class="border border-black px-3 py-1 text-xs font-bold transition hover:bg-black hover:text-white disabled:cursor-not-allowed disabled:border-gray-400 disabled:text-gray-400"
                  @click="
                    nextWaterHistoryPage
                  "
                >
                  Next
                </button>
              </div>
            </template>
          </section>
        </div>
      </details>

      <p
        v-if="errorMessage"
        class="m-4 border border-red-700 bg-red-50 px-4 py-3 text-sm text-red-700"
      >
        {{ errorMessage }}
      </p>

      <footer
        class="flex flex-col gap-3 border-t border-black p-4 sm:flex-row sm:items-center sm:justify-between"
      >
        <button
          type="button"
          :disabled="
            isFirstRoom || loading
          "
          class="rounded-sm border border-black px-4 py-2 text-sm font-semibold transition hover:bg-black hover:text-white disabled:cursor-not-allowed disabled:border-gray-400 disabled:text-gray-400"
          @click="goToPreviousRoom"
        >
          ← Previous Room
        </button>

        <button
          type="button"
          :disabled="loading"
          class="rounded-sm border border-black bg-black px-5 py-2 text-sm font-semibold text-white transition hover:bg-white hover:text-black disabled:cursor-not-allowed disabled:border-gray-400 disabled:bg-white disabled:text-gray-400"
          @click="saveCurrentRoom"
        >
          {{
            loading
              ? 'Saving...'
              : isLastRoom
                ? 'Save and Finish'
                : 'Save and Next →'
          }}
        </button>

        <button
          type="button"
          :disabled="
            isLastRoom || loading
          "
          class="rounded-sm border border-black px-4 py-2 text-sm font-semibold transition hover:bg-neutral-200 disabled:cursor-not-allowed disabled:border-gray-400 disabled:text-gray-400"
          @click="skipRoom"
        >
          Skip Room →
        </button>
      </footer>
    </article>
  </section>
</template>