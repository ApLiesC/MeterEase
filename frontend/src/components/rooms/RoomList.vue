<script setup lang="ts">
import type { Room } from '@/types/room'
import RoomCard from './RoomCard.vue'

defineProps<{
  rooms: Room[]
}>()

const emit = defineEmits<{
  edit: [room: Room]
  delete: [roomId: number]
}>()
</script>

<template>
  <section class="room-grid">
    <RoomCard
      v-for="room in rooms"
      :key="room.roomId"
      :room="room"
      @edit="emit('edit', $event)"
      @delete="emit('delete', $event)"
    />
  </section>
</template>

<style scoped>
.room-grid {
  display: grid;
  grid-template-columns:
    repeat(auto-fit, minmax(220px, 1fr));
  gap: 1rem;
}
</style>