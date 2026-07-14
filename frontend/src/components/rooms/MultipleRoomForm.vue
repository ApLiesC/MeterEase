<script setup lang="ts">
import { computed, ref } from 'vue'

import { generateRooms } from '@/services/roomService'

import type {
  AdditionalCharge,
  GenerateRoomsRequest,
  RoomNamePattern,
} from '@/types/room'

import AdditionalChargesTable from './AdditionalChargesTable.vue'

const props = defineProps<{
  buildingId: number
}>()

const emit = defineEmits<{
  created: []
}>()

const numberOfRooms = ref<number | null>(null)
const roomNamePattern = ref<RoomNamePattern>(
  'PREFIX_AND_NUMBER',
)
const prefix = ref('')
const startingNumber = ref<number | null>(null)
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
    numberOfRooms.value < 1 ||
    startingNumber.value === null
  ) {
    return []
  }

  const previewCount = Math.min(
    numberOfRooms.value,
    5,
  )

  return Array.from(
    { length: previewCount },
    (_, index) => {
      const currentNumber =
        startingNumber.value! + index

      if (
        roomNamePattern.value ===
        'NUMBER_ONLY'
      ) {
        return String(currentNumber)
      }

      return `${prefix.value.trim()}${currentNumber}`
    },
  )
})

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
      'Prefix is required for Prefix + Number.'
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

  const invalidCharge =
    additionalCharges.value.some(
      (charge) =>
        !charge.chargeName.trim() ||
        charge.chargeAmount < 0,
    )

  if (invalidCharge) {
    errorMessage.value =
      'Each additional charge must have a name and a valid amount.'
    return
  }

  const request: GenerateRoomsRequest = {
    buildingId: props.buildingId,
    numberOfRooms: numberOfRooms.value,
    roomNamePattern:
      roomNamePattern.value,
    startingNumber:
      startingNumber.value,
    rentAmount: rentAmount.value,
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
    request.prefix = prefix.value.trim()
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
    class="room-form"
    @submit.prevent="submitMultipleRooms"
  >
    <div class="form-group">
      <label for="numberOfRooms">
        Number of Rooms to Create
      </label>

      <input
        id="numberOfRooms"
        v-model.number="numberOfRooms"
        type="number"
        min="1"
        step="1"
        placeholder="50"
        required
      />
    </div>

    <fieldset class="pattern-group">
      <legend>
        Room Name Pattern
      </legend>

      <label>
        <input
          v-model="roomNamePattern"
          type="radio"
          value="NUMBER_ONLY"
        />

        Number Only
      </label>

      <label>
        <input
          v-model="roomNamePattern"
          type="radio"
          value="PREFIX_AND_NUMBER"
        />

        Prefix + Number
      </label>
    </fieldset>

    <div
      v-if="prefixRequired"
      class="form-group"
    >
      <label for="prefix">
        Prefix
      </label>

      <input
        id="prefix"
        v-model="prefix"
        type="text"
        placeholder="A"
        required
      />
    </div>

    <div class="form-group">
      <label for="startingNumber">
        Starting Number
      </label>

      <input
        id="startingNumber"
        v-model.number="startingNumber"
        type="number"
        min="1"
        step="1"
        placeholder="101"
        required
      />
    </div>

    <div class="form-group">
      <label for="multipleRentAmount">
        Rent Amount
      </label>

      <input
        id="multipleRentAmount"
        v-model.number="rentAmount"
        type="number"
        min="0"
        step="0.01"
        placeholder="3500.00"
        required
      />
    </div>

    <section
      v-if="generatedPreview.length > 0"
      class="preview"
    >
      <strong>Room name preview</strong>

      <p>
        {{ generatedPreview.join(', ') }}

        <span
          v-if="
            numberOfRooms !== null &&
            numberOfRooms >
              generatedPreview.length
          "
        >
          ...
        </span>
      </p>
    </section>

    <AdditionalChargesTable
      v-model:charges="additionalCharges"
    />

    <p
      v-if="errorMessage"
      class="error-message"
    >
      {{ errorMessage }}
    </p>

    <div class="form-actions">
      <button
        type="submit"
        :disabled="loading"
      >
        {{
          loading
            ? 'Generating...'
            : 'Generate Rooms'
        }}
      </button>
    </div>
  </form>
</template>

<style scoped>
.room-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.form-group input {
  width: 100%;
  box-sizing: border-box;
  padding: 0.7rem;
}

.pattern-group {
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
  padding: 1rem;
}

.pattern-group label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.preview {
  padding: 0.9rem;
  border: 1px solid #cccccc;
  border-radius: 8px;
}

.preview p {
  margin-bottom: 0;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 1rem;
}

.form-actions button {
  padding: 0.7rem 1rem;
  cursor: pointer;
}

.form-actions button:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.error-message {
  color: #b00020;
}
</style>