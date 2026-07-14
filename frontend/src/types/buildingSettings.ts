import { BillingMethod } from './billingMethod'

export interface BuildingSettings {
  settingId?: number
  buildingId: number

  electricityBillingMethod: BillingMethod
  electricityRatePerUnit?: number
  electricityFlatFeeAmount?: number

  waterBillingMethod: BillingMethod
  waterRatePerUnit?: number
  waterFlatFeeAmount?: number

  dueDatePeriodDays: number
  dailyLateFeeAmount: number
}