<script setup lang="ts">
defineProps<{
  buildingId: number
}>()

const emit = defineEmits<{
  close: []
  created: []
}>()
</script>

<template>
  <div class="modal-backdrop">
    <div class="modal">
      <h2>Create Room</h2>

      <p>
        Selected building ID: {{ buildingId }}
      </p>

      <button
        type="button"
        @click="emit('close')"
      >
        Close
      </button>
    </div>
  </div>
</template>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  display: grid;
  place-items: center;
  background: rgba(0, 0, 0, 0.4);
}

.modal {
  width: 100%;
  max-width: 500px;
  padding: 24px;
  background: white;
  border-radius: 10px;
}
</style>