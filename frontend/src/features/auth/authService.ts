import { privateAuthApi, publicAuthApi } from "../api/apiClient";

export interface LoginRequest {
  nombre: string;
  clave: string;
}

export interface LoginResponse {
  access_token: string;
  refresh_token: string;
}

export async function login(credentials: LoginRequest): Promise<LoginResponse> {
  try {
    const response = await publicAuthApi.post<LoginResponse>(
      "/login",
      credentials,
    );
    const data = response.data;

    localStorage.setItem("accessToken", data.access_token);
    localStorage.setItem("refreshToken", data.refresh_token);

    return data;
  } catch (error) {
    throw new Error("Credenciales incorrectas");
  }
}

export const logout = async (): Promise<void> => {
  try {
    await privateAuthApi.post("/logout");
  } catch (error) {
    console.error("Error al revocar la sesión en el servidor:", error);
  } finally {
    // Limpieza local
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
  }
};
