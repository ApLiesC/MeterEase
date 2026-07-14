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
  <div class="overlay">
    <div class="modal">

      <h2>Edit Building</h2>

      <p v-if="error">
        {{ error }}
      </p>


      <section>
        <h3>Building Information</h3>

        <label>
          Building Name

          <input
            v-model="buildingForm.buildingName"
            type="text"
          />
        </label>


        <label>
          Address

          <textarea
            v-model="buildingForm.address"
            rows="3"
          />
        </label>
      </section>


      <section>
        <h3>Electricity Settings</h3>

        <label>
          Billing Method

          <select
            v-model="settingsForm.electricityBillingMethod"
          >
            <option
              :value="BillingMethod.METER_BASED"
            >
              Meter-Based
            </option>

            <option
              :value="BillingMethod.FLAT_FEE"
            >
              Flat Fee
            </option>
          </select>
        </label>


        <label
          v-if="
            settingsForm.electricityBillingMethod ===
            BillingMethod.METER_BASED
          "
        >
          Rate Per Unit

          <input
            v-model.number="
              settingsForm.electricityRatePerUnit
            "
            type="number"
          />
        </label>


        <label
          v-else
        >
          Flat Fee Amount

          <input
            v-model.number="
              settingsForm.electricityFlatFeeAmount
            "
            type="number"
          />
        </label>
      </section>


      <section>
        <h3>Water Settings</h3>

        <label>
          Billing Method

          <select
            v-model="settingsForm.waterBillingMethod"
          >
            <option
              :value="BillingMethod.METER_BASED"
            >
              Meter-Based
            </option>

            <option
              :value="BillingMethod.FLAT_FEE"
            >
              Flat Fee
            </option>
          </select>
        </label>


        <label
          v-if="
            settingsForm.waterBillingMethod ===
            BillingMethod.METER_BASED
          "
        >
          Rate Per Unit

          <input
            v-model.number="
              settingsForm.waterRatePerUnit
            "
            type="number"
          />
        </label>


        <label
          v-else
        >
          Flat Fee Amount

          <input
            v-model.number="
              settingsForm.waterFlatFeeAmount
            "
            type="number"
          />
        </label>
      </section>


      <section>
        <h3>Late Payment Settings</h3>

        <label>
          Due Date Period (Days)

          <input
            v-model.number="
              settingsForm.dueDatePeriodDays
            "
            type="number"
          />
        </label>


        <label>
          Daily Late Fee Amount

          <input
            v-model.number="
              settingsForm.dailyLateFeeAmount
            "
            type="number"
          />
        </label>
      </section>


      <div class="buttons">

        <button @click="$emit('close')">
          Cancel
        </button>

        <button
          :disabled="loading"
          @click="save"
        >
          {{ loading ? 'Saving...' : 'Save' }}
        </button>

      </div>

    </div>
  </div>
</template>


<style scoped>
.overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,.4);
  display:flex;
  justify-content:center;
  align-items:center;
}

.modal {
  width:550px;
  max-height:90vh;
  overflow-y:auto;
  background:white;
  padding:24px;
  border-radius:10px;
}

section {
  margin-top:20px;
}

label {
  display:block;
  margin-top:12px;
}

input,
textarea,
select {
  width:100%;
  margin-top:5px;
  padding:8px;
}

.buttons {
  display:flex;
  justify-content:flex-end;
  gap:12px;
  margin-top:20px;
}
</style>