import { useMemo } from "react";
import type { OrderDTO, OrderItemDTO } from "../../../common/types";
import { useCart } from "../../cart/useCart";
import { useSession } from "../../session/useSession";
import { crearPedido } from "../services/cartService";

export function useCartData(sessionCode: string | null) {
  const { items: cart, addItem, decreaseItem, clearCart } = useCart();

  const sessionData = useSession();
  const estadoMesa = sessionData.sessionData?.estadoMesa;
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
    increaseQuantity,
    decreaseQuantity,
    realizarPedido,
  };
}
