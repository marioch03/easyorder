import { useEffect, useState } from "react";
import { getCuenta, getMesa } from "../services/billService";
import type { Cuenta } from "../types/bill";

export function useBillData(sessionCode: string | null) {
const [cuenta, setCuenta] = useState<Cuenta | null>(null);
  const [loading, setLoading] = useState(true);

  const [idMesa, setIdMesa] = useState<number | null>(null);
  const [estadoMesa, setEstadoMesa] = useState("");

  useEffect(() => {
    if (!sessionCode) return;

    const fetchData = async () => {
      try {
        const [mesa, cuenta] = await Promise.all([
          getMesa(),
          getCuenta(),
        ]);

        setIdMesa(mesa.id);
        setEstadoMesa(mesa.estado.nombre);
        setCuenta(cuenta);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [sessionCode]);

  return {
    cuenta,
    loading,
    idMesa,
    estadoMesa,
    setEstadoMesa,
  };
}