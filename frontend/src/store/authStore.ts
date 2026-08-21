import { create } from "zustand";
import { clearAccessToken, setAccessToken } from "../utils/token";

export interface ApiUser {
  id: number;
  nombre: string;
  rol: string;
  tenantId: number;
}

interface AuthState {
  user: ApiUser | null;
  isAuthenticated: boolean;
  isInitializing: boolean; // Vital para evitar parpadeos al presionar F5

  // Acciones
  setAuth: (user: ApiUser, accessToken: string) => void;
  clearAuth: () => void;
  setInitializing: (isInitializing: boolean) => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  isAuthenticated: false,
  isInitializing: true, // Empieza en true hasta que finalice el intento de /refresh inicial

  setAuth: (user, accessToken) => {
    setAccessToken(accessToken); // Sincroniza el token con Axios
    set({
      user,
      isAuthenticated: true,
      isInitializing: false,
    });
  },

  clearAuth: () => {
    clearAccessToken(); // Limpia el token de Axios
    set({
      user: null,
      isAuthenticated: false,
      isInitializing: false,
    });
  },

  setInitializing: (isInitializing) => set({ isInitializing }),
}));

export default useAuthStore;
