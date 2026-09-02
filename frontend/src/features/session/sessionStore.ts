import { create } from "zustand";
import { initApi } from "../api/apiClient";

// ──────────────────────────────────────────────
// Tipos
// ──────────────────────────────────────────────

export type SesionClienteDTO = {
  sesionId: number;
  sessionCode: string;
  mesaId: number;
  numeroMesa: number;
  estadoMesa: string;
};

interface SessionState {
  sessionCode: string | null;
  sessionData: SesionClienteDTO | null;

  // Acciones
  initSession: (search: string) => void;
  actualizarEstadoMesa: (nuevoEstado: string) => void;
  clearSession: () => void;
}

// ──────────────────────────────────────────────
// Helper: obtiene el sessionCode de la URL o del localStorage
// ──────────────────────────────────────────────

function resolveSessionCode(search: string): string | null {
  const params = new URLSearchParams(search);
  const codeFromURL = params.get("sessionCode");
  if (codeFromURL) {
    localStorage.setItem("sessionCode", codeFromURL);
    return codeFromURL;
  }
  return localStorage.getItem("sessionCode");
}

// ──────────────────────────────────────────────
// Store
// ──────────────────────────────────────────────

export const useSessionStore = create<SessionState>((set) => ({
  sessionCode: null,
  sessionData: null,

  initSession: (search: string) => {
    const code = resolveSessionCode(search);
    set({ sessionCode: code });

    if (!code) return;

    initApi
      .get("/sesiones/init", {
        headers: { "X-Session-Code": code },
      })
      .then((res) => {
        set({ sessionData: res.data });
      })
      .catch((err) => {
        console.error("Error cargando sesión inicial:", err);
      });
  },

  actualizarEstadoMesa: (nuevoEstado: string) => {
    set((state) => ({
      sessionData: state.sessionData
        ? { ...state.sessionData, estadoMesa: nuevoEstado }
        : null,
    }));
  },

  clearSession: () => {
    set({ sessionCode: null, sessionData: null });
  },
}));

export default useSessionStore;
