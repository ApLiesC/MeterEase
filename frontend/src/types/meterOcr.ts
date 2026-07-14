export interface MeterOcrResponse {
  extractedText: string
  detectedNumbers: number[]
  suggestedReading: number | null
}