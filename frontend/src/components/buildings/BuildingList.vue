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
  <section class="flex flex-col gap-4">
    <BuildingCard
      v-for="building in buildings"
      :key="building.buildingId"
      :building="building"
      @edit="emit('edit', building)"
      @delete="emit('delete', building.buildingId!)"
    />
  </section>
</template>