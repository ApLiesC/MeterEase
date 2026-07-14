<script setup lang="ts">
import { ref } from 'vue'

import { createRoom } from '@/services/roomService'
import type { AdditionalCharge, Room } from '@/types/room'

import AdditionalChargesTable from './AdditionalChargesTable.vue'

const props = defineProps<{
  buildingId: number
}>()

const emit = defineEmits<{
  created: []
}>()

const roomName = ref('')
const rentAmount = ref<number | null>(null)
const additionalCharges = ref<AdditionalCharge[]>([])

const loading = ref(false)
const errorMessage = ref('')

async function submitSingleRoom(): Promise<void> {
  errorMessage.value = ''

  if (!roomName.value.trim()) {
    errorMessage.value = 'Room name is required.'
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

  const room: Room = {
    buildingId: props.buildingId,
    tenantId: null,
    roomName: roomName.value.trim(),
    rentAmount: rentAmount.value,
    additionalCharges:
      additionalCharges.value.map((charge) => ({
        chargeName: charge.chargeName.trim(),
        chargeAmount: charge.chargeAmount,
      })),
  }

  loading.value = true

  try {
    await createRoom(room)
    emit('created')
  } catch (error) {
    errorMessage.value =
      error instanceof Error
        ? error.message
        : 'Failed to create room.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <form
    class="room-form"
    @submit.prevent="submitSingleRoom"
  >
    <div class="form-group">
      <label for="roomName">
        Room Name
      </label>

      <input
        id="roomName"
        v-model="roomName"
        type="text"
        placeholder="A101"
        required
      />
    </div>

    <div class="form-group">
      <label for="rentAmount">
        Rent Amount
      </label>

      <input
        id="rentAmount"
        v-model.number="rentAmount"
        type="number"
        min="0"
        step="0.01"
        placeholder="3500.00"
        required
      />
    </div>

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
            ? 'Creating...'
            : 'Create Room'
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
  padding: 0.7rem;
  box-sizing: border-box;
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