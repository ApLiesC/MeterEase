<script setup lang="ts">
import { ref } from 'vue'

import { recordMeterReadings } from '@/services/meterReadingService'

import type {
  MeterReading,
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

const editableEntries = ref<
  RoomReadingEntry[]
>(
  props.entries.map((entry) => ({
    ...entry,
    electricityHistory: [
      ...entry.electricityHistory,
    ],
    waterHistory: [
      ...entry.waterHistory,
    ],
  })),
)

const electricityHistoryPages = ref<
  Record<number, number>
>({})

const waterHistoryPages = ref<
  Record<number, number>
>({})

const loading = ref(false)
const errorMessage = ref('')

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

function getElectricityHistoryPage(
  roomId: number,
): number {
  return (
    electricityHistoryPages.value[
      roomId
    ] ?? 1
  )
}

function getWaterHistoryPage(
  roomId: number,
): number {
  return (
    waterHistoryPages.value[roomId] ?? 1
  )
}

function getElectricityPageCount(
  entry: RoomReadingEntry,
): number {
  return Math.max(
    1,
    Math.ceil(
      entry.electricityHistory.length /
        HISTORY_PAGE_SIZE,
    ),
  )
}

function getWaterPageCount(
  entry: RoomReadingEntry,
): number {
  return Math.max(
    1,
    Math.ceil(
      entry.waterHistory.length /
        HISTORY_PAGE_SIZE,
    ),
  )
}

function getPaginatedElectricityHistory(
  entry: RoomReadingEntry,
): MeterReading[] {
  const page =
    getElectricityHistoryPage(
      entry.roomId,
    )

  const startIndex =
    (page - 1) * HISTORY_PAGE_SIZE

  return entry.electricityHistory.slice(
    startIndex,
    startIndex + HISTORY_PAGE_SIZE,
  )
}

function getPaginatedWaterHistory(
  entry: RoomReadingEntry,
): MeterReading[] {
  const page = getWaterHistoryPage(
    entry.roomId,
  )

  const startIndex =
    (page - 1) * HISTORY_PAGE_SIZE

  return entry.waterHistory.slice(
    startIndex,
    startIndex + HISTORY_PAGE_SIZE,
  )
}

function changeElectricityHistoryPage(
  entry: RoomReadingEntry,
  requestedPage: number,
): void {
  const pageCount =
    getElectricityPageCount(entry)

  electricityHistoryPages.value[
    entry.roomId
  ] = Math.min(
    Math.max(requestedPage, 1),
    pageCount,
  )
}

function changeWaterHistoryPage(
  entry: RoomReadingEntry,
  requestedPage: number,
): void {
  const pageCount =
    getWaterPageCount(entry)

  waterHistoryPages.value[
    entry.roomId
  ] = Math.min(
    Math.max(requestedPage, 1),
    pageCount,
  )
}

async function submitReadings(): Promise<void> {
  errorMessage.value = ''

  const readings: MeterReadingRequest[] =
    []

  for (
    const entry of editableEntries.value
  ) {
    if (
      entry.currentElectricity !== null
    ) {
      if (
        entry.currentElectricity > 99999
      ) {
        errorMessage.value =
          `Electricity reading for room ${entry.roomName} cannot exceed 99,999.`
        return
      }

      if (
        entry.previousElectricity !==
          null &&
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
      if (entry.currentWater > 99999) {
        errorMessage.value =
          `Water reading for room ${entry.roomName} cannot exceed 99,999.`
        return
      }

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
  <section
    class="rounded-md border border-black bg-white p-4 font-mono uppercase tracking-wider sm:p-6"
  >
    <header
      class="mb-6 flex flex-col gap-4 border-b border-black pb-4 sm:flex-row sm:items-start sm:justify-between"
    >
      <div>
        <p
          class="mb-1 text-xs font-semibold text-gray-500"
        >
          List Recording View
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
        class="w-fit rounded-sm border border-black px-4 py-2 text-sm font-semibold transition hover:bg-red-400 disabled:cursor-not-allowed disabled:border-gray-400 disabled:text-gray-400"
        @click="emit('cancel')"
      >
        ← Back
      </button>
    </header>

    <div
      class="overflow-x-auto border border-black"
    >
      <table
        class="min-w-[1100px] w-full border-collapse text-left text-sm"
      >
        <thead class="bg-black text-white">
          <tr>
            <th
              class="border-r border-white/40 px-3 py-3"
            >
              Room
            </th>

            <th
              class="border-r border-white/40 px-3 py-3"
            >
              Electricity Previous
            </th>

            <th
              class="border-r border-white/40 px-3 py-3"
            >
              Electricity Current
            </th>

            <th
              class="border-r border-white/40 px-3 py-3"
            >
              Water Previous
            </th>

            <th
              class="border-r border-white/40 px-3 py-3"
            >
              Water Current
            </th>

            <th class="px-3 py-3">
              History
            </th>
          </tr>
        </thead>

        <tbody>
          <tr
            v-for="entry in editableEntries"
            :key="entry.roomId"
            class="border-b border-black last:border-b-0"
          >
            <td
              class="border-r border-black bg-neutral-100 px-3 py-3 font-bold"
            >
              {{ entry.roomName }}
            </td>

            <td
              class="border-r border-black px-3 py-3"
            >
              <span
                v-if="
                  entry.previousElectricity !==
                  null
                "
                class="font-bold"
              >
                {{
                  entry.previousElectricity
                }}
              </span>

              <span
                v-else
                class="text-xs normal-case tracking-normal text-gray-500"
              >
                No previous reading
              </span>
            </td>

            <td
              class="border-r border-black px-3 py-3"
            >
              <input
                v-model.number="
                  entry.currentElectricity
                "
                type="number"
                min="0"
                max="99999"
                step="1"
                class="w-full min-w-32 rounded-sm border border-black bg-neutral-100 px-3 py-2 outline-none transition focus:bg-white"
                :placeholder="
                  entry.previousElectricity !==
                  null
                    ? String(
                        entry.previousElectricity,
                      )
                    : 'Enter reading'
                "
              />
            </td>

            <td
              class="border-r border-black px-3 py-3"
            >
              <span
                v-if="
                  entry.previousWater !== null
                "
                class="font-bold"
              >
                {{ entry.previousWater }}
              </span>

              <span
                v-else
                class="text-xs normal-case tracking-normal text-gray-500"
              >
                No previous reading
              </span>
            </td>

            <td
              class="border-r border-black px-3 py-3"
            >
              <input
                v-model.number="
                  entry.currentWater
                "
                type="number"
                min="0"
                max="99999"
                step="1"
                class="w-full min-w-32 rounded-sm border border-black bg-neutral-100 px-3 py-2 outline-none transition focus:bg-white"
                :placeholder="
                  entry.previousWater !== null
                    ? String(
                        entry.previousWater,
                      )
                    : 'Enter reading'
                "
              />
            </td>

            <td class="px-3 py-3">
              <details
                class="min-w-72 border border-black bg-white"
              >
                <summary
                  class="cursor-pointer px-3 py-2 text-xs font-bold transition hover:bg-black hover:text-white"
                >
                  View Previous Meter History
                </summary>

                <div
                  v-if="
                    entry.electricityHistory
                      .length === 0 &&
                    entry.waterHistory.length ===
                      0
                  "
                  class="border-t border-black p-3 text-xs normal-case tracking-normal text-gray-500"
                >
                  No previous meter readings.
                </div>

                <div
                  v-else
                  class="grid gap-4 border-t border-black p-3"
                >
                  <section>
                    <h4
                      class="mb-2 border-b border-black pb-1 text-xs font-bold"
                    >
                      Electricity
                    </h4>

                    <p
                      v-if="
                        entry
                          .electricityHistory
                          .length === 0
                      "
                      class="text-xs normal-case tracking-normal text-gray-500"
                    >
                      No electricity history.
                    </p>

                    <template v-else>
                      <ul class="space-y-1">
                        <li
                          v-for="
                            reading in
                              getPaginatedElectricityHistory(
                                entry,
                              )
                          "
                          :key="
                            reading.meterReadingId
                          "
                          class="flex items-center justify-between gap-3 border-b border-gray-300 py-1 text-xs"
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
                          entry
                            .electricityHistory
                            .length >
                          HISTORY_PAGE_SIZE
                        "
                        class="mt-3 flex items-center justify-between gap-2"
                      >
                        <button
                          type="button"
                          :disabled="
                            getElectricityHistoryPage(
                              entry.roomId,
                            ) === 1
                          "
                          class="border border-black px-2 py-1 text-[10px] font-bold transition hover:bg-black hover:text-white disabled:cursor-not-allowed disabled:border-gray-400 disabled:text-gray-400"
                          @click="
                            changeElectricityHistoryPage(
                              entry,
                              getElectricityHistoryPage(
                                entry.roomId,
                              ) - 1,
                            )
                          "
                        >
                          Previous
                        </button>

                        <span
                          class="text-[10px] font-bold"
                        >
                          Page
                          {{
                            getElectricityHistoryPage(
                              entry.roomId,
                            )
                          }}
                          /
                          {{
                            getElectricityPageCount(
                              entry,
                            )
                          }}
                        </span>

                        <button
                          type="button"
                          :disabled="
                            getElectricityHistoryPage(
                              entry.roomId,
                            ) ===
                            getElectricityPageCount(
                              entry,
                            )
                          "
                          class="border border-black px-2 py-1 text-[10px] font-bold transition hover:bg-black hover:text-white disabled:cursor-not-allowed disabled:border-gray-400 disabled:text-gray-400"
                          @click="
                            changeElectricityHistoryPage(
                              entry,
                              getElectricityHistoryPage(
                                entry.roomId,
                              ) + 1,
                            )
                          "
                        >
                          Next
                        </button>
                      </div>
                    </template>
                  </section>

                  <section>
                    <h4
                      class="mb-2 border-b border-black pb-1 text-xs font-bold"
                    >
                      Water
                    </h4>

                    <p
                      v-if="
                        entry.waterHistory
                          .length === 0
                      "
                      class="text-xs normal-case tracking-normal text-gray-500"
                    >
                      No water history.
                    </p>

                    <template v-else>
                      <ul class="space-y-1">
                        <li
                          v-for="
                            reading in
                              getPaginatedWaterHistory(
                                entry,
                              )
                          "
                          :key="
                            reading.meterReadingId
                          "
                          class="flex items-center justify-between gap-3 border-b border-gray-300 py-1 text-xs"
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
                          entry.waterHistory
                            .length >
                          HISTORY_PAGE_SIZE
                        "
                        class="mt-3 flex items-center justify-between gap-2"
                      >
                        <button
                          type="button"
                          :disabled="
                            getWaterHistoryPage(
                              entry.roomId,
                            ) === 1
                          "
                          class="border border-black px-2 py-1 text-[10px] font-bold transition hover:bg-black hover:text-white disabled:cursor-not-allowed disabled:border-gray-400 disabled:text-gray-400"
                          @click="
                            changeWaterHistoryPage(
                              entry,
                              getWaterHistoryPage(
                                entry.roomId,
                              ) - 1,
                            )
                          "
                        >
                          Previous
                        </button>

                        <span
                          class="text-[10px] font-bold"
                        >
                          Page
                          {{
                            getWaterHistoryPage(
                              entry.roomId,
                            )
                          }}
                          /
                          {{
                            getWaterPageCount(entry)
                          }}
                        </span>

                        <button
                          type="button"
                          :disabled="
                            getWaterHistoryPage(
                              entry.roomId,
                            ) ===
                            getWaterPageCount(
                              entry,
                            )
                          "
                          class="border border-black px-2 py-1 text-[10px] font-bold transition hover:bg-black hover:text-white disabled:cursor-not-allowed disabled:border-gray-400 disabled:text-gray-400"
                          @click="
                            changeWaterHistoryPage(
                              entry,
                              getWaterHistoryPage(
                                entry.roomId,
                              ) + 1,
                            )
                          "
                        >
                          Next
                        </button>
                      </div>
                    </template>
                  </section>
                </div>
              </details>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <p
      v-if="errorMessage"
      class="mt-4 border border-red-700 bg-red-50 px-4 py-3 text-sm text-red-700"
    >
      {{ errorMessage }}
    </p>

    <footer
      class="mt-6 flex flex-col-reverse gap-3 border-t border-black pt-4 sm:flex-row sm:justify-end"
    >
      <button
        type="button"
        :disabled="loading"
        class="rounded-sm border border-black px-4 py-2 text-sm font-semibold transition hover:bg-red-400 disabled:cursor-not-allowed disabled:border-gray-400 disabled:text-gray-400"
        @click="emit('cancel')"
      >
        Cancel
      </button>

      <button
        type="button"
        :disabled="loading"
        class="rounded-sm border border-black px-5 py-2 text-sm font-semibold transition hover:bg-black hover:text-white disabled:cursor-not-allowed disabled:border-gray-400 disabled:text-gray-400"
        @click="submitReadings"
      >
        {{
          loading
            ? 'Saving...'
            : 'Submit Readings →'
        }}
      </button>
    </footer>
  </section>
</template>