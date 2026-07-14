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
          type="button"
          @click="emit('close')"
        >
          ✕
        </button>
      </header>


      <div class="mode-picker">

        <label>
          <input
            v-model="mode"
            type="radio"
            value="single"
          />

          Single Room
        </label>


        <label>
          <input
            v-model="mode"
            type="radio"
            value="multiple"
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


<style scoped>

.modal-backdrop {
  position: fixed;
  inset: 0;

  background: rgba(0, 0, 0, 0.4);

  display: flex;
  justify-content: center;
  align-items: center;

  z-index: 1000;
}


.modal {
  width: 500px;
  max-height: 90vh;

  overflow-y: auto;

  background: white;

  border-radius: 10px;

  padding: 24px;

  z-index: 1001;
}


.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  margin-bottom: 20px;
}


.close {
  border: none;
  background: transparent;

  font-size: 20px;
  cursor: pointer;
}


.mode-picker {
  display: flex;
  gap: 20px;

  margin-bottom: 20px;
}


label {
  cursor: pointer;
}

</style>