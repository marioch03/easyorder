import { useEffect, useState } from "react";
import { useSession } from "../../session/useSession";
import { getCuenta } from "../services/billService";
import type { Cuenta } from "../types/bill";

export function useBillData(sessionCode: string | null) {
  const [cuenta, setCuenta] = useState<Cuenta | null>(null);
  const [loading, setLoading] = useState(true);
  const sessionData = useSession();
  const idMesa = sessionData.sessionData?.mesaId ?? null;
  const estadoMesa = sessionData.sessionData?.estadoMesa ?? null;

  useEffect(() => {
    if (!sessionCode) return;

    const fetchData = async () => {
      try {
        setLoading(true);
        const [cuenta] = await Promise.all([getCuenta()]);
        setCuenta(cuenta);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [sessionCode, estadoMesa]);

  return {
    cuenta,
    loading,
    idMesa,
    estadoMesa,
  };
}
