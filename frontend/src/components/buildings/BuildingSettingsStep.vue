<script setup lang="ts">
import { reactive, ref } from 'vue'
import { createBuildingSettings } from '@/services/buildingSettingsService'
import { BillingMethod } from '@/types/billingMethod'
import type { BuildingSettings } from '@/types/buildingSettings'

const props = defineProps<{
  buildingId: number
}>()

const emit = defineEmits<{
  completed: []
  back: []
}>()

const form = reactive<BuildingSettings>({
  settingId: 0,
  buildingId: props.buildingId,

  electricityBillingMethod:
    BillingMethod.METER_BASED,

  electricityRatePerUnit: undefined,
  electricityFlatFeeAmount: undefined,

  waterBillingMethod:
    BillingMethod.FLAT_FEE,

  waterRatePerUnit: undefined,
  waterFlatFeeAmount: undefined,

  dueDatePeriodDays: 7,
  dailyLateFeeAmount: 0,
})

const loading = ref(false)

async function submit() {
  loading.value = true

  try {
    await createBuildingSettings(
      props.buildingId,
      form,
    )

    emit('completed')

  } catch (err) {
    alert(
      err instanceof Error
        ? err.message
        : 'Failed to save settings.',
    )
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div
    class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4"
    @click.self="$emit('back')"
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
            Configure Billing Settings
          </h2>

          <p class="mt-1 text-[11px] text-gray-500">
            Default billing configuration for this building
          </p>
        </div>

        <button
          class="rounded-sm border border-black px-2 py-1 text-xs transition hover:bg-black hover:text-white"
          @click="$emit('back')"
        >
          ✕
        </button>
      </header>

      <form
        class="space-y-5"
        @submit.prevent="submit"
      >
        <!-- Utilities -->
        <section
          class="border-b border-black pb-4"
        >
          <h3
            class="mb-3 text-l font-bold"
          >
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
                v-model="form.electricityBillingMethod"
                class="h-10 rounded-sm border border-black bg-white px-3 text-sm normal-case"
              >
                <option :value="BillingMethod.METER_BASED">
                  Meter Based
                </option>

                <option :value="BillingMethod.FLAT_FEE">
                  Flat Fee
                </option>
              </select>

              <input
                v-if="form.electricityBillingMethod === BillingMethod.METER_BASED"
                v-model.number="form.electricityRatePerUnit"
                type="number"
                min="0"
                placeholder="Rate / Unit"
                class="h-10 rounded-sm border border-black bg-white px-3 text-sm"
              >

              <input
                v-else
                v-model.number="form.electricityFlatFeeAmount"
                type="number"
                min="0"
                placeholder="Flat Fee"
                class="h-10 rounded-sm border border-black bg-white px-3 text-sm"
              >
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
                v-model="form.waterBillingMethod"
                class="h-10 rounded-sm border border-black bg-white px-3 text-sm normal-case"
              >
                <option :value="BillingMethod.METER_BASED">
                  Meter Based
                </option>

                <option :value="BillingMethod.FLAT_FEE">
                  Flat Fee
                </option>
              </select>

              <input
                v-if="form.waterBillingMethod === BillingMethod.METER_BASED"
                v-model.number="form.waterRatePerUnit"
                type="number"
                min="0"
                placeholder="Rate / Unit"
                class="h-10 rounded-sm border border-black bg-white px-3 text-sm"
              >

              <input
                v-else
                v-model.number="form.waterFlatFeeAmount"
                type="number"
                min="0"
                placeholder="Flat Fee"
                class="h-10 rounded-sm border border-black bg-white px-3 text-sm"
              >
            </div>

          </div>
        </section>

        <!-- Payment -->
        <section
          class="border-b border-black pb-4"
        >
          <h3
            class="mb-3 text-l font-bold"
          >
            Payment Settings
          </h3>

          <div
            class="grid gap-3 sm:grid-cols-2"
          >
            <div>
              <label
                class="mb-1 block text-xs text-gray-600"
              >
                Due Date (Days)
              </label>

              <input
                v-model.number="form.dueDatePeriodDays"
                type="number"
                min="1"
                class="h-10 w-full rounded-sm border border-black bg-white px-3 text-sm"
              >
            </div>

            <div>
              <label
                class="mb-1 block text-xs text-gray-600"
              >
                Daily Late Fee
              </label>

              <input
                v-model.number="form.dailyLateFeeAmount"
                type="number"
                min="0"
                class="h-10 w-full rounded-sm border border-black bg-white px-3 text-sm"
              >
            </div>
          </div>
        </section>

        <!-- Actions -->
        <footer
          class="flex justify-end gap-2 pt-2"
        >
          <button
            type="button"
            class="rounded-sm border border-black px-5 py-2 text-sm transition hover:bg-neutral-200"
            @click="$emit('back')"
          >
            Back
          </button>

          <button
            type="submit"
            :disabled="loading"
            class="rounded-sm border border-black bg-black px-5 py-2 text-sm text-white transition hover:bg-white hover:text-black disabled:opacity-50"
          >
            {{
              loading
                ? 'Saving...'
                : 'Save Settings'
            }}
          </button>
        </footer>
      </form>
    </section>
  </div>
</template>