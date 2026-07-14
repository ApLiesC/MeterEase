<script setup lang="ts">
import { onMounted, ref } from 'vue'
import {
  deleteBuilding,
  getBuildings,
} from '@/services/buildingService'

import type { Building } from '@/types/building'

import BuildingList from '@/components/buildings/BuildingList.vue'
import CreateBuildingModal from '@/components/buildings/CreateBuildingModal.vue'
import BuildingSettingsStep from '@/components/buildings/BuildingSettingsStep.vue'
import EditBuildingModal from '@/components/buildings/EditBuildingModal.vue'

const buildings = ref<Building[]>([])
const loading = ref(false)
const error = ref('')

const showCreateModal = ref(false)
const showSettings = ref(false)
const showEditModal = ref(false)

const selectedBuilding = ref<Building | null>(null)

async function loadBuildings() {
  loading.value = true
  error.value = ''

  try {
    buildings.value = await getBuildings()
  } catch (err) {
    error.value =
      err instanceof Error
        ? err.message
        : 'Failed to load buildings.'
  } finally {
    loading.value = false
  }
}

function openSettings(building: Building) {
  selectedBuilding.value = building
  showCreateModal.value = false
  showSettings.value = true
}

function openEdit(building: Building) {
  selectedBuilding.value = building
  showEditModal.value = true
}

function closeEdit() {
  showEditModal.value = false
  selectedBuilding.value = null
}

async function finishEdit() {
  showEditModal.value = false
  selectedBuilding.value = null

  await loadBuildings()
}

async function finishCreation() {
  showSettings.value = false
  selectedBuilding.value = null

  await loadBuildings()
}

function cancelSettings() {
  showSettings.value = false
  selectedBuilding.value = null
}

async function removeBuilding(id: number) {
  if (!confirm('Delete this building?')) {
    return
  }

  try {
    await deleteBuilding(id)
    await loadBuildings()
  } catch (err) {
    alert(
      err instanceof Error
        ? err.message
        : 'Delete failed.',
    )
  }
}

onMounted(loadBuildings)
</script>

<template>
  <main class="buildings">

    <header class="page-header">
      <h1>
        Buildings
      </h1>

      <button
        @click="showCreateModal = true"
      >
        + Create Building
      </button>
    </header>


    <p v-if="loading">
      Loading...
    </p>

    <p v-else-if="error">
      {{ error }}
    </p>

    <p v-else-if="buildings.length === 0">
      No buildings found.
    </p>


    <BuildingList
      v-else
      :buildings="buildings"
      @edit="openEdit"
      @delete="removeBuilding"
    />


    <CreateBuildingModal
      v-if="showCreateModal"
      @close="showCreateModal = false"
      @created="openSettings"
    />


    <BuildingSettingsStep
      v-if="showSettings && selectedBuilding"
      :building-id="selectedBuilding.buildingId!"
      @completed="finishCreation"
      @back="cancelSettings"
    />


    <EditBuildingModal
      v-if="showEditModal && selectedBuilding"
      :building="selectedBuilding"
      @close="closeEdit"
      @updated="finishEdit"
    />

  </main>
</template>

<style scoped>
.buildings {
  max-width: 900px;
  margin: 2rem auto;
  padding: 1rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>