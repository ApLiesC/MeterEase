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
  <div class="overlay">
    <div class="modal">
      <h2>Create Building</h2>

      <label>
        Building Name
        <input
          v-model="form.buildingName"
          type="text"
          placeholder="Building name"
        />
      </label>

      <label>
        Address
        <textarea
          v-model="form.address"
          rows="3"
          placeholder="Building address"
        />
      </label>

      <div class="buttons">
        <button @click="$emit('close')">
          Cancel
        </button>

        <button
          :disabled="loading"
          @click="submit"
        >
          {{ loading ? 'Creating...' : 'Next' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.overlay {
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
  background: white;
  border-radius: 10px;
  padding: 24px;
}

h2 {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-top: 16px;
}

input,
textarea {
  width: 100%;
  margin-top: 6px;
  padding: 8px;
  box-sizing: border-box;
}

.buttons {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}
</style>