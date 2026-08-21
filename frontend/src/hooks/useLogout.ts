// src/hooks/useLogout.ts
import { useMutation } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import { publicAuthApi } from "../features/api/apiClient";
import queryClient from "../lib/queryClient";
import useAuthStore from "../store/authStore";
import { clearAccessToken } from "../utils/token";

export const useLogout = () => {
  const navigate = useNavigate();
  const clearAuth = useAuthStore((state) => state.clearAuth);

  const { mutate: logout, isPending } = useMutation({
    mutationFn: async () => {
      // 1. Llama al backend (/api/v1/auth/logout) para destruir la cookie HttpOnly
      await publicAuthApi.post("/logout");
    },
    // onSettled se ejecuta SIEMPRE (tanto si el servidor responde con éxito como con error)
    onSettled: () => {
      const tenantSlug = localStorage.getItem("tenant_slug") || "";
      // 2. Borra el token de la memoria RAM
      clearAccessToken();
      // 3. Limpia el estado del usuario en Zustand
      clearAuth();
      // 4. Borra toda la caché guardada por React Query (datos de tablas, clientes, etc.)
      queryClient.clear();
      // 5. Redirige al login
      navigate(`/${tenantSlug}/auth/login`, { replace: true });
    },
  });

  return { logout, isPending };
};

export default useLogout;
