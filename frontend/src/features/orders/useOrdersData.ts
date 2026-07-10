import { useEffect, useState } from "react";
import { useWS } from "../ws/useWS";
import type { PedidoDTO } from "./orders";
import { getPedidosAdmin } from "./orderService";

export function useOrdersData(token: string | null) {
  const [pedidos, setPedidos] = useState<PedidoDTO[]>([]);
  const { pedidos: pedidosWS } = useWS();
  const [loading, setLoading] = useState(!pedidosWS || pedidosWS.length === 0);

  useEffect(() => {
    if (!token) return;

    if (pedidosWS && pedidosWS.length > 0) {
      setLoading(false);
      return;
    }

    const cargarDatos = async () => {
      setLoading(true);
      try {
        const data = await getPedidosAdmin();
        setPedidos(data);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    cargarDatos();
  }, [token]);

  useEffect(() => {
    if (pedidosWS?.length) {
      setPedidos(pedidosWS);
      setLoading(false);
    }
  }, [pedidosWS]);

  return { pedidos, setPedidos, loading };
}
