<script setup lang="ts">
import type { AdditionalCharge } from '@/types/room'

const props = defineProps<{
  charges: AdditionalCharge[]
}>()

const emit = defineEmits<{
  'update:charges': [charges: AdditionalCharge[]]
}>()

function addCharge(): void {
  emit('update:charges', [
    ...props.charges,
    {
      chargeName: '',
      chargeAmount: 0,
    },
  ])
}

function updateChargeName(
  index: number,
  value: string,
): void {
  const updatedCharges = props.charges.map(
    (charge, chargeIndex) =>
      chargeIndex === index
        ? {
            ...charge,
            chargeName: value,
          }
        : charge,
  )

  emit('update:charges', updatedCharges)
}

function updateChargeAmount(
  index: number,
  value: string,
): void {
  const amount = Number(value)

  const updatedCharges = props.charges.map(
    (charge, chargeIndex) =>
      chargeIndex === index
        ? {
            ...charge,
            chargeAmount:
              Number.isNaN(amount) ? 0 : amount,
          }
        : charge,
  )

  emit('update:charges', updatedCharges)
}

function removeCharge(index: number): void {
  emit(
    'update:charges',
    props.charges.filter(
      (_, chargeIndex) => chargeIndex !== index,
    ),
  )
}
</script>

<template>
  <section class="charges-section">
    <div class="section-header">
      <h3>Additional Charges</h3>

      <button
        type="button"
        class="add-button"
        @click="addCharge"
      >
        + Add Charge
      </button>
    </div>

    <p v-if="charges.length === 0">
      No additional charges.
    </p>

    <div
      v-else
      class="charges-table"
    >
      <div class="table-header">
        <span>Charge Name</span>
        <span>Amount</span>
        <span></span>
      </div>

      <div
        v-for="(charge, index) in charges"
        :key="index"
        class="charge-row"
      >
        <input
          :value="charge.chargeName"
          type="text"
          placeholder="Parking"
          required
          @input="
            updateChargeName(
              index,
              ($event.target as HTMLInputElement).value,
            )
          "
        />

        <input
          :value="charge.chargeAmount"
          type="number"
          min="0"
          step="0.01"
          placeholder="0.00"
          required
          @input="
            updateChargeAmount(
              index,
              ($event.target as HTMLInputElement).value,
            )
          "
        />

        <button
          type="button"
          class="remove-button"
          aria-label="Remove charge"
          @click="removeCharge(index)"
        >
          Remove
        </button>
      </div>
    </div>
  </section>
</template>

<style scoped>
.charges-section {
  margin-top: 1.5rem;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 0.75rem;
}

.section-header h3 {
  margin: 0;
}

.add-button,
.remove-button {
  cursor: pointer;
}

.charges-table {
  border: 1px solid #cccccc;
  border-radius: 8px;
  overflow: hidden;
}

.table-header,
.charge-row {
  display: grid;
  grid-template-columns: 1fr 140px auto;
  gap: 0.75rem;
  align-items: center;
  padding: 0.75rem;
}

.table-header {
  font-weight: 600;
  background: #f4f4f4;
}

.charge-row {
  border-top: 1px solid #dddddd;
}

.charge-row input {
  width: 100%;
  padding: 0.6rem;
  box-sizing: border-box;
}

.remove-button {
  padding: 0.55rem 0.75rem;
}

@media (max-width: 600px) {
  .table-header {
    display: none;
  }

  .charge-row {
    grid-template-columns: 1fr;
  }
}
</style>