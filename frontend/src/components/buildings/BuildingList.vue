<script setup lang="ts">
import type { Building } from '@/types/building'
import BuildingCard from './BuildingCard.vue'

defineProps<{
  buildings: Building[]
}>()

const emit = defineEmits<{
  edit: [building: Building]
  delete: [id: number]
}>()
</script>

<template>
  <section class="building-list">
    <BuildingCard
      v-for="building in buildings"
      :key="building.buildingId"
      :building="building"
      @edit="emit('edit', building)"
      @delete="emit('delete', building.buildingId!)"
    />
  </section>
</template>

<style scoped>
.building-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-top: 2rem;
}
</style>