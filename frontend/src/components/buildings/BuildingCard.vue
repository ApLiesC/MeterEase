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
  <article
    class="rounded-md border border-black bg-neutral-100 p-4 font-mono"
  >
    <div
      class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between"
    >
      <!-- Building Info -->
      <div class="min-w-0 flex-1">
        <h2
          class="text-lg font-bold uppercase tracking-wider"
        >
          {{ building.buildingName }}
        </h2>

        <p
          class="mt-1 text-sm text-gray-600"
        >
          {{ building.address }}
        </p>
      </div>

      <!-- Actions -->
      <div
        class="flex gap-2 md:flex-shrink-0"
      >
        <button
          class="rounded-sm border border-black px-4 py-2 text-sm transition hover:bg-neutral-200"
          @click="emit('edit')"
        >
          Edit
        </button>

        <button
          class="rounded-sm border border-red-700 px-4 py-2 text-sm text-red-700 transition hover:bg-red-700 hover:text-white"
          @click="emit('delete')"
        >
          Delete
        </button>
      </div>
    </div>
  </article>
</template>