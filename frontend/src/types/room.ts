export interface AdditionalCharge {
  additionalChargeId?: number
  roomId?: number
  chargeName: string
  chargeAmount: number
}

export interface Room {
  roomId?: number
  buildingId: number
  tenantId?: number | null
  roomName: string
  rentAmount: number
  additionalCharges: AdditionalCharge[]
}

export type RoomNamePattern =
  | 'NUMBER_ONLY'
  | 'PREFIX_AND_NUMBER'

export interface GenerateRoomsRequest {
  buildingId: number
  numberOfRooms: number
  roomNamePattern: RoomNamePattern
  prefix?: string
  startingNumber: number
  rentAmount: number
  additionalCharges: AdditionalCharge[]
}