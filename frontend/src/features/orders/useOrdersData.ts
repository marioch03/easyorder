import { useEffect, useState } from "react";
import { useWS } from "../ws/useWS";
import type { PedidoDTO } from "./orders";
import { getPedidosAdmin } from "./orderService";

export function useOrdersData(token: string | null) {
  const [pedidos, setPedidos] = useState<PedidoDTO[]>([]);
  const { pedidos: pedidosWS } = useWS();

  useEffect(() => {
    if (!token) return;

    const cargarDatos = async () => {
      try {
        const data = await getPedidosAdmin();
        setPedidos(data);
      } catch (err) {
        console.error(err);
      }
    };

    cargarDatos();
  }, [token]);

  useEffect(() => {
    if (pedidosWS?.length) {
      setPedidos(pedidosWS);
    }
  }, [pedidosWS]);

  return { pedidos, setPedidos };
}