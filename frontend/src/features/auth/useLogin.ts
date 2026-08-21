import { useMutation } from "@tanstack/react-query";
import { jwtDecode } from "jwt-decode";
import { useNavigate } from "react-router-dom";
import useAuthStore from "../../store/authStore";
import type { LoginRequest, LoginResponse } from "./authService";
import { login } from "./authService";
import { getRolesFromToken } from "./jwtService";

export const useLogin = () => {
  const navigate = useNavigate();
  const setAuth = useAuthStore((state) => state.setAuth);

  return useMutation<LoginResponse, Error, LoginRequest>({
    mutationFn: async (credentials) => {
      return await login(credentials);
    },
    onSuccess: (data, variables) => {
      const decoded: any = jwtDecode(data.access_token);
      const user = {
        id: decoded.id,
        nombre: decoded.nombre,
        rol: decoded.roles,
        tenantId: decoded.tenantId,
      };

      setAuth(user, data.access_token);

      const roles = getRolesFromToken(data.access_token);
      const tenantSlug = variables.tenantSlug;

      if (roles.includes("ADMIN")) {
        navigate(`/${tenantSlug}/select-interface`, { replace: true });
      } else if (roles.includes("KDS")) {
        navigate(`/${tenantSlug}/kds`, { replace: true });
      } else if (roles.includes("PERSONAL")) {
        navigate(`/${tenantSlug}/staff`, { replace: true });
      } else {
        navigate("/forbidden", { replace: true });
      }
    },
    onError: (error) => {
      console.error("Error al iniciar sesión:", error.message);
    },
  });
};
