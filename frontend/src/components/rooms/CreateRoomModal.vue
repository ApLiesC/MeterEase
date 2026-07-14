<script setup lang="ts">
import { ref } from 'vue'

import SingleRoomForm from './SingleRoomForm.vue'
import MultipleRoomForm from './MultipleRoomForm.vue'

defineProps<{
  buildingId: number
}>()

const emit = defineEmits<{
  close: []
  created: []
}>()

type RoomMode = 'single' | 'multiple'

const mode = ref<RoomMode>('single')
</script>

<template>
  <div
    class="fixed inset-0 z-50 grid place-items-center bg-black/40 p-4"
    @click.self="emit('close')"
  >

    <section
      class="flex max-h-[85vh] w-full max-w-xl flex-col rounded-md border border-black bg-neutral-100 p-4 font-mono"
    >

      <!-- Header -->
      <header
        class="mb-4 flex shrink-0 items-center justify-between border-b border-black pb-3"
      >
        <div>
          <h2
            class="text-lg font-bold uppercase tracking-wider"
          >
            Create Room
          </h2>

          <p
            class="text-xs text-gray-600"
          >
            Add a new room to this building
          </p>
        </div>


        <button
          type="button"
          class="rounded-sm border border-black px-2 py-1 text-sm transition hover:bg-black hover:text-white"
          @click="emit('close')"
        >
          ×
        </button>

      </header>


      <!-- Mode Selection -->
      <section
        class="mb-4 grid shrink-0 grid-cols-2 gap-2"
      >

        <label
          class="cursor-pointer"
        >
          <input
            v-model="mode"
            type="radio"
            value="single"
            class="peer hidden"
          />

          <div
            class="rounded-sm border border-black px-3 py-2 text-center text-sm transition peer-checked:bg-black peer-checked:text-white"
          >
            Single Room
          </div>

        </label>


        <label
          class="cursor-pointer"
        >
          <input
            v-model="mode"
            type="radio"
            value="multiple"
            class="peer hidden"
          />

          <div
            class="rounded-sm border border-black px-3 py-2 text-center text-sm transition peer-checked:bg-black peer-checked:text-white"
          >
            Multiple Rooms
          </div>

        </label>

      </section>


      <!-- Scrollable Form Area -->
      <div
        class="min-h-0 overflow-y-auto pr-1"
      >

        <SingleRoomForm
          v-if="mode === 'single'"
          :building-id="buildingId"
          @created="emit('created')"
        />


        <MultipleRoomForm
          v-else
          :building-id="buildingId"
          @created="emit('created')"
        />

      </div>

    </section>

  </div>
</template>