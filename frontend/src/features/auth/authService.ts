import { authApi } from "../api/apiClient";

export interface LoginRequest {
  nombre: string;
  clave: string;
}

export interface LoginResponse {
  access_token: string;
  refresh_token: string;
}

const BASE_URL = import.meta.env.VITE_BASE_URL;

export async function login(credentials: LoginRequest): Promise<LoginResponse> {
  try {
    const response = await authApi.post<LoginResponse>("/login", credentials);
    const data = response.data;

    localStorage.setItem("accessToken", data.access_token);
    localStorage.setItem("refreshToken", data.refresh_token);

    return data;
  } catch (error) {
    throw new Error("Credenciales incorrectas");
  }
}