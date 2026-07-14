<script setup lang="ts">
import { computed, ref } from 'vue'
import type { Room } from '@/types/room'

const props = defineProps<{
  room: Room
}>()

const emit = defineEmits<{
  edit: [room: Room]
  delete: [roomId: number]
}>()

const showCharges = ref(false)

const hasCharges = computed(() =>
  props.room.additionalCharges &&
  props.room.additionalCharges.length > 0
)

// Temporary until tenant entity exists
const roomStatus = 'Vacant'
</script>

<template>
  <article
    class="flex h-fit flex-col rounded-md border border-black bg-neutral-100 p-4 font-mono uppercase tracking-wider"
  >
    <!-- Header -->
    <div
      class="flex flex-col gap-3 border-b border-black pb-4 sm:flex-row sm:items-start sm:justify-between"
    >
      <div>
        <h2 class="text-xl font-bold">
          {{ room.roomName }}
        </h2>

        <p class="mt-1 text-xs text-gray-500">
          Room
        </p>
      </div>

      <span
        class="w-fit rounded-sm border border-black px-3 py-1 text-xs font-semibold"
      >
        {{ roomStatus }}
      </span>
    </div>

    <!-- Summary -->
    <div class="mt-4">
      <div
        class="grid grid-cols-1 gap-3 border-b border-black pb-3 text-sm sm:grid-cols-2"
      >
        <!-- Rent -->
        <div>
          <p class="text-xs text-gray-500">
            Rent
          </p>

          <p class="mt-1 text-base">
            ฿ {{ room.rentAmount }}
          </p>
        </div>

        <!-- Additional Charges -->
        <div>
          <p class="text-xs text-gray-500">
            Additional Charges
          </p>

          <button
            v-if="hasCharges"
            class="mt-1 flex w-full items-center justify-between rounded-sm border border-black px-2 py-1 text-left text-xs transition hover:bg-black hover:text-white"
            @click="showCharges = !showCharges"
          >
            <span>
              {{ room.additionalCharges.length }} charge(s)
            </span>

            <span>
              {{ showCharges ? '−' : '+' }}
            </span>
          </button>

          <p
            v-else
            class="mt-1 text-sm text-gray-500"
          >
            None
          </p>
        </div>
      </div>

      <!-- Expandable Charges -->
      <Transition
        enter-active-class="transition-all duration-300 ease-out overflow-hidden"
        leave-active-class="transition-all duration-300 ease-in overflow-hidden"
        enter-from-class="max-h-0 opacity-0"
        enter-to-class="max-h-96 opacity-100"
        leave-from-class="max-h-96 opacity-100"
        leave-to-class="max-h-0 opacity-0"
      >
        <div
          v-if="showCharges"
          class="overflow-hidden border-b border-black"
        >
          <div
            v-for="charge in room.additionalCharges"
            :key="charge.additionalChargeId"
            class="flex flex-col justify-between gap-1 border-t border-black px-3 py-2 text-xs sm:flex-row"
          >
            <span>
              {{ charge.chargeName }}
            </span>

            <span>
              ฿ {{ charge.chargeAmount }}
            </span>
          </div>
        </div>
      </Transition>
    </div>

    <!-- Footer -->
    <div
      class="flex flex-col gap-2 border-t border-black pt-4 sm:flex-row"
    >
      <button
        class="flex-1 rounded-sm border border-black py-2 text-sm transition hover:bg-black hover:text-white"
        @click="emit('edit', room)"
      >
        Edit
      </button>

      <button
        class="flex-1 rounded-sm border border-black py-2 text-sm transition hover:bg-black hover:text-white"
        @click="emit('delete', room.roomId!)"
      >
        Delete
      </button>
    </div>
  </article>
</template>