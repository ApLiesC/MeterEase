export type UtilityType = 'ELECTRICITY' | 'WATER'

export interface MeterReading {
  meterReadingId: number
  roomId: number
  utilityType: UtilityType
  meterReadingValue: number
  meterImage: string | null
  recordedDateTime: string
}

export interface MeterReadingRequest {
  roomId: number
  utilityType: UtilityType
  meterReadingValue: number
  meterImage?: string | null
}

export interface RoomReadingEntry {
  roomId: number
  roomName: string

  previousElectricity: number | null
  currentElectricity: number | null

  previousWater: number | null
  currentWater: number | null
}