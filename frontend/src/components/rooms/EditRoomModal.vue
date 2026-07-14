<script setup lang="ts">
import { ref } from 'vue'

import { updateRoom } from '@/services/roomService'
import type { AdditionalCharge, Room } from '@/types/room'

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

function addCharge(): void {
  additionalCharges.value.push({
    chargeName: '',
    chargeAmount: 0,
  })
}

function removeCharge(index: number): void {
  additionalCharges.value.splice(index, 1)
}

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
        ...charge,
        chargeName: charge.chargeName.trim(),
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
    class="fixed inset-0 z-50 grid place-items-center bg-black/40 p-4"
    @click.self="emit('close')"
  >
    <section
      class="w-full max-w-2xl rounded-md border border-black bg-neutral-100 p-4 font-mono"
    >

      <!-- Header -->
      <header
        class="mb-4 flex items-center justify-between border-b border-black pb-3"
      >
        <h2
          class="text-lg font-bold uppercase tracking-wider"
        >
          Edit Room
        </h2>

        <button
          type="button"
          class="rounded-sm border border-black px-2 py-1 text-sm transition hover:bg-black hover:text-white"
          @click="emit('close')"
        >
          ×
        </button>
      </header>


      <form
        class="space-y-4"
        @submit.prevent="submitUpdate"
      >

        <!-- Room Information -->
        <section
          class="grid gap-3 sm:grid-cols-[1fr_160px]"
        >
          <div>
            <label class="mb-1 block text-xs text-gray-600">
              Room Name
            </label>

            <input
              v-model="roomName"
              type="text"
              required
              class="h-10 w-full rounded-sm border border-black bg-white px-3 text-sm outline-none focus:ring-1 focus:ring-black"
            />
          </div>

          <div>
            <label class="mb-1 block text-xs text-gray-600">
              Rent Amount
            </label>

            <input
              v-model.number="rentAmount"
              type="number"
              min="0"
              step="0.01"
              required
              class="h-10 w-full rounded-sm border border-black bg-white px-3 text-sm outline-none focus:ring-1 focus:ring-black"
            />
          </div>
        </section>


        <!-- Charges -->
        <section
          class="border-t border-black pt-3"
        >
          <div class="mb-2">
            <h3
              class="text-sm font-bold uppercase"
            >
              Additional Charges
            </h3>

            <p class="text-xs text-gray-600">
              Optional monthly fees
            </p>
          </div>


          <div
            v-if="additionalCharges.length"
            class="space-y-2"
          >
            <div
              v-for="(charge, index) in additionalCharges"
              :key="index"
              class="grid grid-cols-[1fr_120px_36px] gap-2"
            >
              <input
                v-model="charge.chargeName"
                type="text"
                placeholder="Charge name"
                class="h-10 w-full rounded-sm border border-black bg-white px-3 text-sm outline-none focus:ring-1 focus:ring-black"
              />

              <input
                v-model.number="charge.chargeAmount"
                type="number"
                min="0"
                step="0.01"
                placeholder="0.00"
                class="h-10 w-full rounded-sm border border-black bg-white px-3 text-sm outline-none focus:ring-1 focus:ring-black"
              />

              <button
                type="button"
                class="h-10 rounded-sm border border-red-700 text-sm text-red-700 transition hover:bg-red-700 hover:text-white"
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


        <!-- Actions -->
        <footer
          class="flex justify-end gap-2 border-t border-black pt-3"
        >
          <button
            type="button"
            class="rounded-sm border border-black px-5 py-2 text-sm transition hover:bg-neutral-200"
            @click="emit('close')"
          >
            Cancel
          </button>

          <button
            type="submit"
            :disabled="loading"
            class="rounded-sm border border-black bg-black px-5 py-2 text-sm text-white transition hover:bg-white hover:text-black disabled:cursor-not-allowed disabled:opacity-50"
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