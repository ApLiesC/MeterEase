import { apiRequest } from './api'

import type {
  GenerateRoomsRequest,
  Room,
} from '@/types/room'

export function getRooms(
  buildingId?: number,
): Promise<Room[]> {
  const query =
    buildingId !== undefined
      ? `?buildingId=${buildingId}`
      : ''

  return apiRequest<Room[]>(
    `/api/rooms${query}`,
  )
}

export function createRoom(
  room: Room,
): Promise<Room> {
  return apiRequest<Room>('/api/rooms', {
    method: 'POST',
    body: JSON.stringify(room),
  })
}

export function generateRooms(
  request: GenerateRoomsRequest,
): Promise<Room[]> {
  return apiRequest<Room[]>(
    '/api/rooms/generate',
    {
      method: 'POST',
      body: JSON.stringify(request),
    },
  )
}

export function updateRoom(
  roomId: number,
  room: Room,
): Promise<Room> {
  return apiRequest<Room>(
    `/api/rooms/${roomId}`,
    {
      method: 'PUT',
      body: JSON.stringify(room),
    },
  )
}

export function deleteRoom(
  roomId: number,
): Promise<void> {
  return apiRequest<void>(
    `/api/rooms/${roomId}`,
    {
      method: 'DELETE',
    },
  )
}