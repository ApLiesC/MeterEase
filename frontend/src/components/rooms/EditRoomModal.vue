<script setup lang="ts">
import { ref } from 'vue'

import { updateRoom } from '@/services/roomService'
import type { AdditionalCharge, Room } from '@/types/room'

import AdditionalChargesTable from './AdditionalChargesTable.vue'

const props = defineProps<{
  room: Room
}>()

const emit = defineEmits<{
  close: []
  updated: []
}>()

const roomName = ref(props.room.roomName)
const rentAmount = ref<number | null>(props.room.rentAmount)

const additionalCharges = ref<AdditionalCharge[]>(
  props.room.additionalCharges?.map((charge) => ({
    additionalChargeId: charge.additionalChargeId,
    roomId: charge.roomId,
    chargeName: charge.chargeName,
    chargeAmount: charge.chargeAmount,
  })) ?? [],
)

const loading = ref(false)
const errorMessage = ref('')

async function submitUpdate(): Promise<void> {
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

  const invalidCharge = additionalCharges.value.some(
    (charge) =>
      !charge.chargeName.trim() ||
      charge.chargeAmount < 0,
  )

  if (invalidCharge) {
    errorMessage.value =
      'Each additional charge must have a name and valid amount.'
    return
  }

  if (props.room.roomId === undefined) {
    errorMessage.value = 'Room ID is missing.'
    return
  }

  const updatedRoom: Room = {
    roomId: props.room.roomId,
    buildingId: props.room.buildingId,
    tenantId: props.room.tenantId ?? null,
    roomName: roomName.value.trim(),
    rentAmount: rentAmount.value,
    additionalCharges:
      additionalCharges.value.map((charge) => ({
        additionalChargeId:
          charge.additionalChargeId,
        roomId: charge.roomId,
        chargeName: charge.chargeName.trim(),
        chargeAmount: charge.chargeAmount,
      })),
  }

  loading.value = true

  try {
    await updateRoom(
      props.room.roomId,
      updatedRoom,
    )

    emit('updated')
  } catch (error) {
    errorMessage.value =
      error instanceof Error
        ? error.message
        : 'Failed to update room.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div
    class="modal-backdrop"
    @click.self="emit('close')"
  >
    <section class="modal">
      <header class="modal-header">
        <h2>Edit Room</h2>

        <button
          type="button"
          class="close-button"
          aria-label="Close"
          @click="emit('close')"
        >
          ×
        </button>
      </header>

      <form @submit.prevent="submitUpdate">
        <div class="form-group">
          <label for="editRoomName">
            Room Name
          </label>

          <input
            id="editRoomName"
            v-model="roomName"
            type="text"
            required
          />
        </div>

        <div class="form-group">
          <label for="editRentAmount">
            Rent Amount
          </label>

          <input
            id="editRentAmount"
            v-model.number="rentAmount"
            type="number"
            min="0"
            step="0.01"
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

        <footer class="modal-actions">
          <button
            type="button"
            @click="emit('close')"
          >
            Cancel
          </button>

          <button
            type="submit"
            :disabled="loading"
          >
            {{
              loading
                ? 'Saving...'
                : 'Save Changes'
            }}
          </button>
        </footer>
      </form>
    </section>
  </div>
</template>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: grid;
  place-items: center;
  padding: 1rem;
  background: rgba(0, 0, 0, 0.45);
}

.modal {
  width: 100%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  padding: 1.5rem;
  background: white;
  border-radius: 10px;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1.25rem;
}

.modal-header h2 {
  margin: 0;
}

.close-button {
  border: none;
  background: transparent;
  font-size: 1.5rem;
  cursor: pointer;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
  margin-bottom: 1rem;
}

.form-group input {
  width: 100%;
  box-sizing: border-box;
  padding: 0.7rem;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  margin-top: 1.5rem;
}

.modal-actions button {
  padding: 0.7rem 1rem;
  cursor: pointer;
}

.modal-actions button:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.error-message {
  color: #b00020;
}
</style>