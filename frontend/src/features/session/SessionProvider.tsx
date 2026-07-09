import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { initApi } from "../api/apiClient";
import { SessionContext } from "./SessionContext";
import type { SesionClienteDTO } from "./session";

export function SessionProvider({ children }: { children: React.ReactNode }) {
  const location = useLocation();

  const [sessionCode] = useState<string | null>(() => {
    const params = new URLSearchParams(location.search);
    const codeFromURL = params.get("sessionCode");
    const codeFromStorage = localStorage.getItem("sessionCode");

    if (codeFromURL) {
      localStorage.setItem("sessionCode", codeFromURL);
      return codeFromURL;
    }
    return codeFromStorage;
  });

  const [sessionData, setSessionData] = useState<SesionClienteDTO | null>(null);

  useEffect(() => {
    if (!sessionCode) return;

    const cargarSesion = async () => {
      try {
        const response = await initApi.get("/sesiones/init", {
          headers: {
            "X-Session-Code": sessionCode,
          },
        });
        setSessionData(response.data);
      } catch (err) {
        console.error("Error cargando sesión inicial:", err);
      }
    };

    cargarSesion();
  }, [sessionCode]);

  const actualizarEstadoMesa = (nuevoEstado: string) => {
    setSessionData((prev) => (prev ? { ...prev, estadoMesa: nuevoEstado } : null));
  };

  const contextValue = {
    sessionCode,
    sessionData,
    actualizarEstadoMesa
  };

  return (
    <SessionContext.Provider value={contextValue}>
      {children}
    </SessionContext.Provider>
  );
}