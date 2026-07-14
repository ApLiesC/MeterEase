const API_BASE_URL = import.meta.env.VITE_API_BASE_URL

interface ApiRequestOptions extends RequestInit {
  authenticated?: boolean
}

export async function apiRequest<T>(
  path: string,
  options: ApiRequestOptions = {},
): Promise<T> {
  const {
    authenticated = true,
    ...fetchOptions
  } = options

  const headers = new Headers(fetchOptions.headers)

  if (
    fetchOptions.body &&
    !(fetchOptions.body instanceof FormData) &&
    !headers.has('Content-Type')
  ) {
    headers.set('Content-Type', 'application/json')
  }

  if (authenticated) {
    const token = localStorage.getItem('accessToken')

    if (token) {
      headers.set('Authorization', `Bearer ${token}`)
    }
  }

  let response: Response

  try {
    response = await fetch(`${API_BASE_URL}${path}`, {
      ...fetchOptions,
      headers,
    })
  } catch {
    throw new Error(
      'Unable to connect to the server. Make sure the backend is running.',
    )
  }

  const responseText = await response.text()

  if (!response.ok) {
    let message =
      `Request failed with status ${response.status}`

    if (responseText) {
      try {
        const body = JSON.parse(responseText)

        if (typeof body.message === 'string') {
          message = body.message
        } else if (typeof body.detail === 'string') {
          message = body.detail
        } else if (typeof body.error === 'string') {
          message = body.error
        } else if (body.errors) {
          if (Array.isArray(body.errors)) {
            message = body.errors.join(', ')
          } else {
            message = Object.values(
              body.errors,
            ).join(', ')
          }
        }
      } catch {
        message = responseText
      }
    }

    throw new Error(message)
  }

  if (!responseText) {
    return undefined as T
  }

  try {
    return JSON.parse(responseText) as T
  } catch {
    throw new Error(
      'The server returned an invalid JSON response.',
    )
  }
}