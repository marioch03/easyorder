import { authApi } from "../api/apiClient";

export async function logout(): Promise<void> {
  const token = localStorage.getItem("accessToken");

  try {
    if (token) {
      await authApi.post("/logout", null, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
    }
  } catch (error) {
    console.error("Error al notificar el logout al servidor:", error);
  } finally {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
  }
}