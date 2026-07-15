<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'

import {
  updateBuilding,
} from '@/services/buildingService'

import {
  getBuildingSettings,
  updateBuildingSettings,
} from '@/services/buildingSettingsService'

import type { Building } from '@/types/building'
import type { BuildingSettings } from '@/types/buildingSettings'
import { BillingMethod } from '@/types/billingMethod'

const props = defineProps<{
  building: Building
}>()

const emit = defineEmits<{
  close: []
  updated: []
}>()

const loading = ref(false)
const error = ref('')

const buildingForm = reactive({
  buildingName: '',
  address: '',
})

const settingsForm = reactive<BuildingSettings>({
  settingId: 0,
  buildingId: props.building.buildingId!,

  electricityBillingMethod:
    BillingMethod.METER_BASED,

  electricityRatePerUnit: 0,
  electricityFlatFeeAmount: 0,

  waterBillingMethod:
    BillingMethod.FLAT_FEE,

  waterRatePerUnit: 0,
  waterFlatFeeAmount: 0,

  dueDatePeriodDays: 1,
  dailyLateFeeAmount: 0,
})


async function loadData() {
  loading.value = true
  error.value = ''

  try {
    buildingForm.buildingName =
      props.building.buildingName

    buildingForm.address =
      props.building.address

    const settings =
      await getBuildingSettings(
        props.building.buildingId!,
      )

    Object.assign(settingsForm, settings)

  } catch (err) {
    error.value =
      err instanceof Error
        ? err.message
        : 'Failed to load building information.'
  } finally {
    loading.value = false
  }
}


async function save() {
  loading.value = true
  error.value = ''

  try {
    await updateBuilding(
      props.building.buildingId!,
      {
        ...props.building,
        buildingName: buildingForm.buildingName,
        address: buildingForm.address,
      },
    )

    await updateBuildingSettings(
      props.building.buildingId!,
      settingsForm,
    )

    emit('updated')
    emit('close')

  } catch (err) {
    error.value =
      err instanceof Error
        ? err.message
        : 'Failed to update building.'
  } finally {
    loading.value = false
  }
}


onMounted(loadData)
</script>


<template>
  <div
    class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4"
    @click.self="$emit('close')"
  >
    <section
      class="w-full max-w-2xl rounded-md border border-black bg-neutral-100 p-5 font-mono uppercase tracking-wider"
    >
      <!-- Header -->
      <header
        class="mb-5 flex items-center justify-between border-b border-black pb-3"
      >
        <div>
          <h2 class="text-lg font-bold">
            Edit Building
          </h2>

          <p class="mt-1 text-[11px] text-gray-500">
            Update building information and billing settings
          </p>
        </div>

        <button
          class="rounded-sm border border-black px-2 py-1 text-xs transition hover:bg-black hover:text-white"
          @click="$emit('close')"
        >
          ✕
        </button>
      </header>

      <p
        v-if="error"
        class="mb-4 rounded-sm border border-red-700 bg-red-50 p-3 text-xs text-red-700"
      >
        {{ error }}
      </p>

      <form
        class="space-y-5"
        @submit.prevent="save"
      >
        <!-- Building Information -->
        <section class="border-b border-black pb-4">
          <h3 class="mb-3 text-sm font-bold">
            Building Information
          </h3>

          <div class="space-y-3">
            <div>
              <label
                class="mb-1 block text-s text-gray-600"
              >
                Building Name
              </label>

              <input
                v-model="buildingForm.buildingName"
                type="text"
                class="h-10 w-full rounded-sm border border-black bg-white px-3 text-sm normal-case outline-none focus:ring-1 focus:ring-black"
              />
            </div>

            <div>
              <label
                class="mb-1 block text-s text-gray-600"
              >
                Address
              </label>

              <input
                v-model="buildingForm.address"
                type="text"
                class="h-10 w-full rounded-sm border border-black bg-white px-3 py-2 text-sm normal-case outline-none focus:ring-1 focus:ring-black"
              />
            </div>
          </div>
        </section>

        <!-- Utility Billing -->
        <section class="border-b border-black pb-4">
          <h3 class="mb-3 text-l font-bold">
            Utility Billing
          </h3>

          <div class="space-y-3">
            <!-- Electricity -->
            <div
              class="grid gap-3 sm:grid-cols-[120px_1fr_160px]"
            >
              <label
                class="self-center text-s text-gray-600"
              >
                Electricity
              </label>

              <select
                v-model="settingsForm.electricityBillingMethod"
                class="h-10 rounded-sm border border-black bg-white px-3 text-sm normal-case outline-none focus:ring-1 focus:ring-black"
              >
                <option
                  :value="BillingMethod.METER_BASED"
                >
                  Meter Based
                </option>

                <option
                  :value="BillingMethod.FLAT_FEE"
                >
                  Flat Fee
                </option>
              </select>

              <input
                v-if="
                  settingsForm.electricityBillingMethod ===
                  BillingMethod.METER_BASED
                "
                v-model.number="
                  settingsForm.electricityRatePerUnit
                "
                type="number"
                min="0"
                placeholder="Rate per unit"
                class="h-10 rounded-sm border border-black bg-white px-3 text-sm outline-none focus:ring-1 focus:ring-black"
              />

              <input
                v-else
                v-model.number="
                  settingsForm.electricityFlatFeeAmount
                "
                type="number"
                min="0"
                placeholder="Flat fee"
                class="h-10 rounded-sm border border-black bg-white px-3 text-sm outline-none focus:ring-1 focus:ring-black"
              />
            </div>

            <!-- Water -->
            <div
              class="grid gap-3 sm:grid-cols-[120px_1fr_160px]"
            >
              <label
                class="self-center text-s text-gray-600"
              >
                Water
              </label>

              <select
                v-model="settingsForm.waterBillingMethod"
                class="h-10 rounded-sm border border-black bg-white px-3 text-sm normal-case outline-none focus:ring-1 focus:ring-black"
              >
                <option
                  :value="BillingMethod.METER_BASED"
                >
                  Meter Based
                </option>

                <option
                  :value="BillingMethod.FLAT_FEE"
                >
                  Flat Fee
                </option>
              </select>

              <input
                v-if="
                  settingsForm.waterBillingMethod ===
                  BillingMethod.METER_BASED
                "
                v-model.number="
                  settingsForm.waterRatePerUnit
                "
                type="number"
                min="0"
                placeholder="Rate per unit"
                class="h-10 rounded-sm border border-black bg-white px-3 text-sm outline-none focus:ring-1 focus:ring-black"
              />

              <input
                v-else
                v-model.number="
                  settingsForm.waterFlatFeeAmount
                "
                type="number"
                min="0"
                placeholder="Flat fee"
                class="h-10 rounded-sm border border-black bg-white px-3 text-sm outline-none focus:ring-1 focus:ring-black"
              />
            </div>
          </div>
        </section>

        <!-- Payment Settings -->
        <section class="border-b border-black pb-4">
          <h3 class="mb-3 text-l font-bold">
            Payment Settings
          </h3>

          <div
            class="grid gap-3 sm:grid-cols-2"
          >
            <div>
              <label
                class="mb-1 block text-s text-gray-600"
              >
                Due Date (Days)
              </label>

              <input
                v-model.number="
                  settingsForm.dueDatePeriodDays
                "
                type="number"
                min="1"
                class="h-10 w-full rounded-sm border border-black bg-white px-3 text-sm outline-none focus:ring-1 focus:ring-black"
              />
            </div>

            <div>
              <label
                class="mb-1 block text-s text-gray-600"
              >
                Daily Late Fee
              </label>

              <input
                v-model.number="
                  settingsForm.dailyLateFeeAmount
                "
                type="number"
                min="0"
                placeholder="Daily late fee"
                class="h-10 w-full rounded-sm border border-black bg-white px-3 text-sm outline-none focus:ring-1 focus:ring-black"
              />
            </div>
          </div>
        </section>

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
                ? 'Saving...'
                : 'Save Changes'
            }}
          </button>
        </footer>
      </form>
    </section>
  </div>
</template>