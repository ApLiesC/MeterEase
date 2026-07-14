<script setup lang="ts">
import { computed, ref } from 'vue'

import { generateRooms } from '@/services/roomService'

import type {
  AdditionalCharge,
  GenerateRoomsRequest,
  RoomNamePattern,
} from '@/types/room'

const props = defineProps<{
  buildingId: number
}>()

const emit = defineEmits<{
  created: []
}>()

const numberOfRooms = ref<number | null>(null)
const startingNumber = ref<number | null>(null)

const roomNamePattern = ref<RoomNamePattern>(
  'PREFIX_AND_NUMBER',
)

const prefix = ref('')
const rentAmount = ref<number | null>(null)

const additionalCharges = ref<AdditionalCharge[]>([])

const loading = ref(false)
const errorMessage = ref('')

const prefixRequired = computed(
  () =>
    roomNamePattern.value ===
    'PREFIX_AND_NUMBER',
)

const generatedPreview = computed(() => {
  if (
    numberOfRooms.value === null ||
    startingNumber.value === null
  ) {
    return []
  }

  return Array.from(
    {
      length: Math.min(
        numberOfRooms.value,
        5,
      ),
    },
    (_, index) => {
      const number =
        startingNumber.value! + index

      return roomNamePattern.value ===
        'NUMBER_ONLY'
        ? String(number)
        : `${prefix.value}${number}`
    },
  )
})

function addCharge(): void {
  additionalCharges.value.push({
    chargeName: '',
    chargeAmount: 0,
  })
}

function removeCharge(index: number): void {
  additionalCharges.value.splice(index, 1)
}

async function submitMultipleRooms(): Promise<void> {
  errorMessage.value = ''

  if (
    numberOfRooms.value === null ||
    numberOfRooms.value < 1
  ) {
    errorMessage.value =
      'Number of rooms must be at least 1.'
    return
  }

  if (
    startingNumber.value === null ||
    startingNumber.value < 1
  ) {
    errorMessage.value =
      'Starting number must be at least 1.'
    return
  }

  if (
    prefixRequired.value &&
    !prefix.value.trim()
  ) {
    errorMessage.value =
      'Prefix is required.'
    return
  }

  if (
    rentAmount.value === null ||
    rentAmount.value < 0
  ) {
    errorMessage.value =
      'Rent amount must be zero or greater.'
    return
  }

  const request: GenerateRoomsRequest = {
    buildingId: props.buildingId,
    numberOfRooms:
      numberOfRooms.value,
    roomNamePattern:
      roomNamePattern.value,
    startingNumber:
      startingNumber.value,
    rentAmount:
      rentAmount.value,
    additionalCharges:
      additionalCharges.value.map(
        (charge) => ({
          chargeName:
            charge.chargeName.trim(),
          chargeAmount:
            charge.chargeAmount,
        }),
      ),
  }

  if (prefixRequired.value) {
    request.prefix =
      prefix.value.trim()
  }

  loading.value = true

  try {
    await generateRooms(request)
    emit('created')

  } catch (error) {
    errorMessage.value =
      error instanceof Error
        ? error.message
        : 'Failed to generate rooms.'

  } finally {
    loading.value = false
  }
}
</script>

<template>
<form
  class="flex max-h-[75vh] flex-col gap-4 overflow-hidden font-mono"
  @submit.prevent="submitMultipleRooms"
>

    <!-- Generation Details -->
<section
  class="border-b border-black pb-4"
>
  <h3
    class="mb-3 text-sm font-bold uppercase"
  >
    Room Generation Details
  </h3>

  <div
    class="grid gap-3 sm:grid-cols-[100px_110px_1fr_120px]"
  >

    <!-- Number of Rooms -->
    <div>
      <label
        class="mb-1 block text-xs text-gray-600"
      >
        Rooms
      </label>

      <input
        v-model.number="numberOfRooms"
        type="number"
        min="1"
        placeholder="50"
        class="h-10 w-full rounded-sm border border-black bg-white px-3 text-sm outline-none focus:ring-1 focus:ring-black"
      />
    </div>


    <!-- Starting Number -->
    <div>
      <label
        class="mb-1 block text-xs text-gray-600"
      >
        Start
      </label>

      <input
        v-model.number="startingNumber"
        type="number"
        min="1"
        placeholder="101"
        class="h-10 w-full rounded-sm border border-black bg-white px-3 text-sm outline-none focus:ring-1 focus:ring-black"
      />
    </div>


    <!-- Pattern -->
    <div>
      <label
        class="mb-1 block text-xs text-gray-600"
      >
        Pattern
      </label>

      <select
        v-model="roomNamePattern"
        class="h-10 w-full rounded-sm border border-black bg-white px-3 text-sm outline-none focus:ring-1 focus:ring-black"
      >
        <option value="PREFIX_AND_NUMBER">
          Prefix + Number
        </option>

        <option value="NUMBER_ONLY">
          Number Only
        </option>
      </select>
    </div>


    <!-- Prefix -->
    <div
      v-if="prefixRequired"
    >
      <label
        class="mb-1 block text-xs text-gray-600"
      >
        Prefix
      </label>

      <input
        v-model="prefix"
        type="text"
        placeholder="A"
        class="h-10 w-full rounded-sm border border-black bg-white px-3 text-sm outline-none focus:ring-1 focus:ring-black"
      />
    </div>

  </div>


  <!-- Preview -->
  <div
    v-if="generatedPreview.length"
    class="mt-3 border border-black bg-neutral-50 p-3"
  >
    <p
      class="text-xs font-bold uppercase"
    >
      Room Generation Preview
    </p>

    <p
      class="mt-1 text-sm"
    >
      {{ generatedPreview.join(', ') }}

      <span
        v-if="
          numberOfRooms &&
          numberOfRooms >
            generatedPreview.length
        "
      >
        ...
      </span>
    </p>
  </div>

</section>


    <!-- Room Settings -->
    <section
      class="border-b border-black pb-4"
    >
      <h3
        class="mb-3 text-sm font-bold uppercase"
      >
        Default Room Settings
      </h3>

      <label class="mb-1 block text-xs text-gray-600">
        Rent Amount
      </label>

      <input
        v-model.number="rentAmount"
        type="number"
        min="0"
        step="0.01"
        placeholder="3500"
        class="h-10 w-full rounded-sm border border-black bg-white px-3 text-sm"
      />
    </section>


    <!-- Charges -->
    <section>
      <h3
        class="text-sm font-bold uppercase"
      >
        Additional Charges
      </h3>

      <p class="mb-2 text-xs text-gray-600">
        Applied to every generated room
      </p>


      <!-- Scrollable Charges List -->
      <div
        v-if="additionalCharges.length"
        class="max-h-48 space-y-2 overflow-y-auto pr-1"
      >
        <div
          v-for="(charge, index) in additionalCharges"
          :key="index"
          class="grid grid-cols-[1fr_120px_36px] gap-2"
        >
          <input
            v-model="charge.chargeName"
            placeholder="Parking"
            class="h-10 rounded-sm border border-black bg-white px-3 text-sm outline-none focus:ring-1 focus:ring-black"
          />

          <input
            v-model.number="charge.chargeAmount"
            type="number"
            min="0"
            placeholder="0"
            class="h-10 rounded-sm border border-black bg-white px-3 text-sm outline-none focus:ring-1 focus:ring-black"
          />

          <button
            type="button"
            class="h-10 rounded-sm border border-red-700 text-red-700 transition hover:bg-red-700 hover:text-white"
            @click="removeCharge(index)"
          >
            ×
          </button>
        </div>
      </div>


      <p
        v-else
        class="text-xs text-gray-600"
      >
        No additional charges.
      </p>


      <button
        type="button"
        class="mt-3 rounded-sm border border-black px-3 py-2 text-xs transition hover:bg-black hover:text-white"
        @click="addCharge"
      >
        + Add Charge
      </button>
    </section>


    <p
      v-if="errorMessage"
      class="border border-red-700 p-2 text-xs text-red-700"
    >
      {{ errorMessage }}
    </p>


    <footer
      class="flex justify-end border-t border-black pt-3"
    >
      <button
        type="submit"
        :disabled="loading"
        class="border border-black bg-black px-5 py-2 text-sm text-white hover:bg-white hover:text-black disabled:opacity-50"
      >
        {{
          loading
            ? 'Generating...'
            : 'Generate Rooms'
        }}
      </button>
    </footer>

  </form>
</template>