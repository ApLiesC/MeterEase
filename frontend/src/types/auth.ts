export interface Manager {
  managerId: number
  fullName: string
  emailAddress: string
}

export interface LoginRequest {
  emailAddress: string
  password: string
}

export interface RegisterRequest {
  fullName: string
  emailAddress: string
  password: string
  passwordConfirmation: string
}

export interface AuthResponse extends Manager {
  accessToken: string
  tokenType: string
  expiresIn: number
}