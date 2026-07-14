import { apiRequest } from './api'
import type { Building } from '@/types/building'

export function getBuildings() {
  return apiRequest<Building[]>('/buildings')
}

export function createBuilding(building: Building) {
  return apiRequest<Building>('/buildings', {
    method: 'POST',
    body: JSON.stringify(building),
  })
}

export function updateBuilding(
  buildingId: number,
  building: Building,
) {
  return apiRequest<Building>(`/buildings/${buildingId}`, {
    method: 'PUT',
    body: JSON.stringify(building),
  })
}

export function deleteBuilding(buildingId: number) {
  return apiRequest<void>(`/buildings/${buildingId}`, {
    method: 'DELETE',
  })
}