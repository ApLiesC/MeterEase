import { apiRequest } from './api'
import type { BuildingSettings } from '@/types/buildingSettings'

export function createBuildingSettings(
  buildingId: number,
  settings: BuildingSettings,
) {
  return apiRequest<BuildingSettings>(
    `/buildings/${buildingId}/settings`,
    {
      method: 'POST',
      body: JSON.stringify(settings),
    },
  )
}

export function getBuildingSettings(
  buildingId: number,
) {
  return apiRequest<BuildingSettings>(
    `/buildings/${buildingId}/settings`,
  )
}

export function updateBuildingSettings(
  buildingId: number,
  settings: BuildingSettings,
) {
  return apiRequest<BuildingSettings>(
    `/buildings/${buildingId}/settings`,
    {
      method: 'PUT',
      body: JSON.stringify(settings),
    },
  )
}