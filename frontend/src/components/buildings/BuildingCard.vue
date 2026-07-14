<script setup lang="ts">
import type { Building } from '@/types/building'

defineProps<{
  building: Building
}>()

const emit = defineEmits<{
  edit: []
  delete: []
}>()
</script>

<template>
  <article class="building-card">

    <h2>
      {{ building.buildingName }}
    </h2>

    <p>
      {{ building.address }}
    </p>

    <div class="actions">

      <button @click="emit('edit')">
        Edit
      </button>

      <button @click="emit('delete')">
        Delete
      </button>

    </div>

  </article>
</template>


<style scoped>
.building-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 16px;
}

.actions {
  margin-top: 16px;
  display:flex;
  gap:12px;
}
</style>