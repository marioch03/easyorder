import {
  privateAuthApi,
  publicAuthApi,
  requestNewToken,
} from "../api/apiClient";

export interface LoginRequest {
  nombre: string;
  clave: string;
  tenantSlug: string;
}

export interface LoginResponse {
  access_token: string;
}

export async function login(credentials: LoginRequest): Promise<LoginResponse> {
  try {
    const response = await publicAuthApi.post<LoginResponse>(
      "/login",
      credentials,
    );
    return response.data;
  } catch (error) {
    throw new Error("Credenciales incorrectas");
  }
}

export async function refreshToken(): Promise<LoginResponse> {
  const token = await requestNewToken();
  return { access_token: token };
}

export const logout = async (): Promise<void> => {
  try {
    await privateAuthApi.post("/logout");
  } catch (error) {
    console.error("Error al revocar la sesión en el servidor:", error);
  }
};
