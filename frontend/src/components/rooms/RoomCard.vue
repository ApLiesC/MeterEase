<script setup lang="ts">
import type { Room } from '@/types/room'

defineProps<{
  room: Room
}>()

const emit = defineEmits<{
  edit: [room: Room]
  delete: [roomId: number]
}>()
</script>

<template>
  <article class="room-card">
    <h2>{{ room.roomName }}</h2>

    <p>
      Rent:
      {{ room.rentAmount }}
    </p>

    <div>
      <strong>Additional Charges</strong>

      <p
        v-if="
          !room.additionalCharges ||
          room.additionalCharges.length === 0
        "
      >
        None
      </p>

      <ul v-else>
        <li
          v-for="charge in room.additionalCharges"
          :key="
            charge.additionalChargeId ??
            `${charge.chargeName}-${charge.chargeAmount}`
          "
        >
          {{ charge.chargeName }}:
          {{ charge.chargeAmount }}
        </li>
      </ul>
    </div>

    <div class="actions">
      <button
        @click="emit('edit', room)"
      >
        Edit
      </button>

      <button
        @click="emit('delete', room.roomId!)"
      >
        Delete
      </button>
    </div>
  </article>
</template>

<style scoped>
.room-card {
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 1rem;
}

.actions {
  display: flex;
  gap: 10px;
  margin-top: 1rem;
}
</style>