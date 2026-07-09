import { useEffect, useMemo, useState } from "react";
import { useCart } from "../../cart/useCart";
import { crearPedido, getEstadoMesa } from "../services/cartService";
import type { OrderDTO, OrderItemDTO } from "../types/menu";

export function useCartData(sessionCode: string | null) {
  const { items: cart, addItem, decreaseItem, clearCart } = useCart();

  const [estadoMesa, setEstadoMesa] = useState("");
  const [loadingEstado, setLoadingEstado] = useState(true);

  useEffect(() => {
    if (!sessionCode) return;

    const fetchEstado = async () => {
      try {
        const data = await getEstadoMesa();

        setEstadoMesa(data.estado.nombre);
      } catch (err) {
        console.error(err);
      } finally {
        setLoadingEstado(false);
      }
    };

    fetchEstado();
  }, [sessionCode]);

  const mesaBloqueada = estadoMesa === "ESPERANDO_CUENTA";

  const total = useMemo(
    () => cart.reduce((sum, item) => sum + item.price * item.quantity, 0),
    [cart],
  );

  const increaseQuantity = (id: number) => {
    const item = cart.find((i) => i.id === id);

    if (item) {
      addItem({
        ...item,
        quantity: 1,
      });
    }
  };

  const decreaseQuantity = (id: number) => {
    decreaseItem(id);
  };

  const realizarPedido = async () => {
    if (!sessionCode) return;

    const order: OrderDTO = {
      items: cart.map(
        (item): OrderItemDTO => ({
          idProducto: item.id,
          cantidad: item.quantity,
          precioUnitario: item.price,
          nota: item.note || "",
        }),
      ),
    };

    await crearPedido(order);

    clearCart();
  };

  return {
    cart,
    total,
    mesaBloqueada,
    loadingEstado,
    increaseQuantity,
    decreaseQuantity,
    realizarPedido,
  };
}
