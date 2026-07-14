import { apiRequest } from './api'

import type {
  MeterReading,
  MeterReadingRequest,
} from '@/types/meterReading'

export function recordMeterReadings(
  buildingId: number,
  readings: MeterReadingRequest[],
): Promise<void> {
  return apiRequest<void>(
    `/api/buildings/${buildingId}/meter-readings`,
    {
      method: 'POST',
      body: JSON.stringify(readings),
    },
  )
}

export function getMeterReadingHistory(
  roomId: number,
): Promise<MeterReading[]> {
  return apiRequest<MeterReading[]>(
    `/api/rooms/${roomId}/meter-readings`,
  )
}