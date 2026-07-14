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
  <div class="modal-backdrop">
    <div class="modal">

      <header class="modal-header">
        <h2>Create Room</h2>

        <button
          class="close"
          @click="emit('close')"
        >
          ✕
        </button>
      </header>

      <div class="mode-picker">

        <label>
          <input
            type="radio"
            value="single"
            v-model="mode"
          />

          Single Room
        </label>

        <label>
          <input
            type="radio"
            value="multiple"
            v-model="mode"
          />

          Multiple Rooms
        </label>

      </div>

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
  </div>
</template>