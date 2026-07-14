<script setup lang="ts">
import { ref } from 'vue'

import { createRoom } from '@/services/roomService'
import type { AdditionalCharge, Room } from '@/types/room'

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

function addCharge(): void {
  additionalCharges.value.push({
    chargeName: '',
    chargeAmount: 0,
  })
}

function removeCharge(index: number): void {
  additionalCharges.value.splice(index, 1)
}

async function submitSingleRoom(): Promise<void> {
  errorMessage.value = ''

  if (!roomName.value.trim()) {
    errorMessage.value =
      'Room name is required.'
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
      'Each additional charge must have a name and valid amount.'
    return
  }

  const room: Room = {
    buildingId: props.buildingId,
    tenantId: null,
    roomName:
      roomName.value.trim(),
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
    class="flex max-h-[65vh] flex-col gap-4 overflow-hidden font-mono"
    @submit.prevent="submitSingleRoom"
  >

    <!-- Room Details -->
    <section
      class="border-b border-black pb-4"
    >
      <h3
        class="mb-3 text-sm font-bold uppercase"
      >
        Room Details
      </h3>


      <div
        class="grid gap-3 sm:grid-cols-2"
      >

        <div>
          <label
            class="mb-1 block text-xs text-gray-600"
          >
            Room Name
          </label>

          <input
            v-model="roomName"
            type="text"
            placeholder="A101"
            class="h-10 w-full rounded-sm border border-black bg-white px-3 text-sm outline-none focus:ring-1 focus:ring-black"
          />
        </div>


        <div>
          <label
            class="mb-1 block text-xs text-gray-600"
          >
            Rent Amount
          </label>

          <input
            v-model.number="rentAmount"
            type="number"
            min="0"
            step="0.01"
            placeholder="3500"
            class="h-10 w-full rounded-sm border border-black bg-white px-3 text-sm outline-none focus:ring-1 focus:ring-black"
          />
        </div>

      </div>
    </section>


    <!-- Charges -->
    <section
      class="flex min-h-0 flex-col"
    >

      <div>
        <h3
          class="text-sm font-bold uppercase"
        >
          Additional Charges
        </h3>

        <p
          class="mb-2 text-xs text-gray-600"
        >
          Optional monthly fees
        </p>
      </div>


      <div
        class="max-h-40 space-y-2 overflow-y-auto pr-1"
      >

        <div
          v-for="(charge, index) in additionalCharges"
          :key="index"
          class="grid grid-cols-[1fr_100px_36px] gap-2"
        >

          <input
            v-model="charge.chargeName"
            type="text"
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


      <button
        type="button"
        class="mt-3 w-fit rounded-sm border border-black px-3 py-2 text-xs transition hover:bg-black hover:text-white"
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


    <!-- Action -->
    <footer
      class="mt-auto flex justify-end border-t border-black pt-3"
    >
      <button
        type="submit"
        :disabled="loading"
        class="rounded-sm border border-black bg-black px-5 py-2 text-sm text-white transition hover:bg-white hover:text-black disabled:cursor-not-allowed disabled:opacity-50"
      >
        {{
          loading
            ? 'Creating...'
            : 'Create Room'
        }}
      </button>
    </footer>

  </form>
</template>