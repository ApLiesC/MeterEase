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

  electricityRatePerUnit: 0,
  electricityFlatFeeAmount: 0,

  waterBillingMethod:
    BillingMethod.FLAT_FEE,

  waterRatePerUnit: 0,
  waterFlatFeeAmount: 0,

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
  <div class="overlay">
    <div class="modal">

      <h2>
        Configure Billing Settings
      </h2>

      <section>
        <h3>Electricity</h3>

        <label>
          Billing Method

          <select
            v-model="form.electricityBillingMethod"
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
        </label>


        <label
          v-if="
            form.electricityBillingMethod ===
            BillingMethod.METER_BASED
          "
        >
          Rate Per Unit

          <input
            v-model.number="
              form.electricityRatePerUnit
            "
            type="number"
            min="0"
          />
        </label>


        <label v-else>
          Flat Fee Amount

          <input
            v-model.number="
              form.electricityFlatFeeAmount
            "
            type="number"
            min="0"
          />
        </label>

      </section>


      <section>
        <h3>Water</h3>

        <label>
          Billing Method

          <select
            v-model="form.waterBillingMethod"
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

        </label>


        <label
          v-if="
            form.waterBillingMethod ===
            BillingMethod.METER_BASED
          "
        >
          Rate Per Unit

          <input
            v-model.number="
              form.waterRatePerUnit
            "
            type="number"
            min="0"
          />
        </label>


        <label v-else>
          Flat Fee Amount

          <input
            v-model.number="
              form.waterFlatFeeAmount
            "
            type="number"
            min="0"
          />
        </label>

      </section>


      <section>

        <label>
          Due Date Period (Days)

          <input
            v-model.number="
              form.dueDatePeriodDays
            "
            type="number"
            min="1"
          />
        </label>


        <label>
          Daily Late Fee

          <input
            v-model.number="
              form.dailyLateFeeAmount
            "
            type="number"
            min="0"
          />
        </label>

      </section>


      <div class="buttons">

        <button @click="$emit('back')">
          Back
        </button>

        <button
          :disabled="loading"
          @click="submit"
        >
          {{ loading ? 'Saving...' : 'Save Settings' }}
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
  background:white;
  width:500px;
  padding:24px;
  border-radius:10px;
}

section {
  margin-top:16px;
}

label {
  display:block;
  margin-top:10px;
}

input,
select {
  width:100%;
  padding:8px;
}

.buttons {
  display:flex;
  justify-content:flex-end;
  gap:10px;
  margin-top:20px;
}
</style>