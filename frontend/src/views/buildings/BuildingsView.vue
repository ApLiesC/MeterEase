<script setup lang="ts">
import { onMounted, ref } from 'vue'

import {
  deleteBuilding,
  getBuildings,
} from '@/services/buildingService'

import type { Building } from '@/types/building'
import AppNavBar from '@/components/layout/AppNavbar.vue'
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
  <AppNavBar />

  <main
    class="mx-auto max-w-5xl p-4 font-mono uppercase tracking-wider"
  >
    <!-- Header -->
    <header
      class="mb-8 flex flex-col gap-3 border-b border-black pb-5 sm:flex-row sm:items-center sm:justify-between"
    >
      <div>
        <h1 class="text-3xl font-bold">
          Buildings
        </h1>

        <p class="mt-1 text-s text-gray-500">
          Manage your apartment and dormitory buildings
        </p>
      </div>

      <button
        type="button"
        class="rounded-sm border border-black px-4 py-2 text-sm font-semibold transition hover:bg-black hover:text-white"
        @click="showCreateModal = true"
      >
        + Create Building
      </button>
    </header>

    <!-- Error -->
    <p
      v-if="error"
      class="mb-5 rounded-sm border border-black bg-neutral-100 p-3 text-sm text-red-600"
    >
      {{ error }}
    </p>


    <!-- Loading -->
    <p
      v-if="loading"
      class="border border-black p-4 text-sm text-gray-600"
    >
      Loading buildings...
    </p>

    <!-- Empty -->
    <p
      v-else-if="
        !error &&
        buildings.length === 0
      "
      class="border border-black p-4 text-sm text-gray-500"
    >
      No buildings found.
    </p>

    <!-- Building Cards -->
    <BuildingList
      v-else
      :buildings="buildings"
      @edit="openEdit"
      @delete="removeBuilding"
    />

    <!-- Modals -->
    <CreateBuildingModal
      v-if="showCreateModal"
      @close="showCreateModal = false"
      @created="openSettings"
    />

    <BuildingSettingsStep
      v-if="
        showSettings &&
        selectedBuilding
      "
      :building-id="selectedBuilding.buildingId!"
      @completed="finishCreation"
      @back="cancelSettings"
    />

    <EditBuildingModal
      v-if="
        showEditModal &&
        selectedBuilding
      "
      :building="selectedBuilding"
      @close="closeEdit"
      @updated="finishEdit"
    />
  </main>
</template>

<style scoped>
</style>