import type { MeterOcrResponse } from '@/types/meterOcr'

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL ??
  'http://localhost:8080'

export async function scanMeterImage(
  image: File,
): Promise<MeterOcrResponse> {
  const token = localStorage.getItem('accessToken')

  const formData = new FormData()
  formData.append('image', image)

  const response = await fetch(
    `${API_BASE_URL}/api/meter-ocr/scan`,
    {
      method: 'POST',
      headers: token
        ? {
            Authorization: `Bearer ${token}`,
          }
        : {},
      body: formData,
    },
  )

  const responseText = await response.text()

  if (!response.ok) {
    let message =
      `OCR request failed with status ${response.status}.`

    if (responseText) {
      try {
        const body = JSON.parse(responseText)

        message =
          body.message ??
          body.detail ??
          body.error ??
          message
      } catch {
        message = responseText
      }
    }

    throw new Error(message)
  }

  if (!responseText) {
    throw new Error(
      'The OCR service returned an empty response.',
    )
  }

  return JSON.parse(responseText) as MeterOcrResponse
}