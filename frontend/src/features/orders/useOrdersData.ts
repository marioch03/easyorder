import { useCallback, useEffect, useState } from "react";
import { useSseSubscription } from "../../common/useSseSuscription";
import type { PedidoDTO } from "./orders";
import { getPedidosAdmin } from "./orderService";

export function useOrdersData() {
  const [pedidos, setPedidos] = useState<PedidoDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const cargar = useCallback(async () => {
    try {
      const data = await getPedidosAdmin();
      setPedidos(data);
      setError(null);
    } catch (err) {
      console.error(err);
      setError("No se han podido cargar los pedidos. Revisa tu conexión.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    console.log("🌀 Ejecutando useEffect de SSE para el topic: pedidos");
    setLoading(true);
    cargar();
  }, [cargar]);

  useSseSubscription({
    topic: "pedidos",
    onRefresh: cargar,
  });

  return {
    pedidos,
    loading,
    error,
    recargar: cargar,
  };
}
