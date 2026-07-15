<script setup lang="ts">
import { reactive } from 'vue'
import { createBuilding } from '@/services/buildingService'
import type { Building } from '@/types/building'

const emit = defineEmits<{
  close: []
  created: [building: Building]
}>()

const form = reactive<Building>({
  managerId: 1,
  buildingName: '',
  address: '',
})

const loading = defineModel<boolean>('loading', { default: false })

async function submit() {
  if (!form.buildingName.trim() || !form.address.trim()) {
    alert('Please complete all fields.')
    return
  }

  loading.value = true

  try {
    const building = await createBuilding(form)

    emit('created', building)
  } catch (err) {
    alert(
      err instanceof Error
        ? err.message
        : 'Failed to create building.',
    )
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div
    class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4"
    @click.self="$emit('close')"
  >
    <section
      class="w-full max-w-md rounded-md border border-black bg-neutral-100 p-5 font-mono uppercase tracking-wider"
    >
      <!-- Header -->
      <header
        class="mb-4 flex items-center justify-between border-b border-black pb-3"
      >
        <div>
          <h2 class="text-lg font-bold">
            Create Building
          </h2>

          <p class="mt-1 text-[11px] text-gray-500">
            Basic building information
          </p>
        </div>

        <button
          class="rounded-sm border border-black px-2 py-1 text-xs transition hover:bg-black hover:text-white"
          @click="$emit('close')"
        >
          ✕
        </button>
      </header>

      <form
        class="space-y-4"
        @submit.prevent="submit"
      >
        <!-- Building Name -->
        <div>
          <label
            class="mb-1 block text-s text-gray-600"
          >
            Building Name
          </label>

          <input
            v-model="form.buildingName"
            type="text"
            placeholder="Building A"
            class="h-10 w-full rounded-sm border border-black bg-white px-3 text-sm normal-case outline-none focus:ring-1 focus:ring-black"
          />
        </div>

        <!-- Address -->
        <div>
          <label
            class="mb-1 block text-s text-gray-600"
          >
            Address
          </label>

          <textarea
            v-model="form.address"
            rows="3"
            placeholder="Building address"
            class="w-full resize-none rounded-sm border border-black bg-white px-3 py-2 text-sm normal-case outline-none focus:ring-1 focus:ring-black"
          />
        </div>

        <!-- Actions -->
        <footer
          class="flex justify-end gap-2 border-t border-black pt-4"
        >
          <button
            type="button"
            class="rounded-sm border border-black px-5 py-2 text-sm transition hover:bg-neutral-200"
            @click="$emit('close')"
          >
            Cancel
          </button>

          <button
            type="submit"
            :disabled="loading"
            class="rounded-sm border border-black bg-black px-5 py-2 text-sm text-white transition hover:bg-white hover:text-black disabled:cursor-not-allowed disabled:opacity-50"
          >
            {{
              loading
                ? 'Creating...'
                : 'Next'
            }}
          </button>
        </footer>
      </form>
    </section>
  </div>
</template>