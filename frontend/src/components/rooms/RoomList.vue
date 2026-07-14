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
  <section
    class="grid items-start gap-4 sm:grid-cols-2 lg:grid-cols-3"
  >
    <RoomCard
      v-for="room in rooms"
      :key="room.roomId"
      :room="room"
      class="h-fit"
      @edit="emit('edit', $event)"
      @delete="emit('delete', $event)"
    />
  </section>
</template>