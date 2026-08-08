import { useMemo } from "react";
import type { OrderDTO, OrderItemDTO } from "../../../common/types";
import { getCartLineId } from "../../cart/CartProvider";
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

  const increaseQuantity = (cartLineId: string) => {
    const item = cart.find((i) => getCartLineId(i) === cartLineId);

    if (item) {
      addItem({
        ...item,
        quantity: 1,
      });
    }
  };

  const decreaseQuantity = (cartLineId: string) => {
    decreaseItem(cartLineId);
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
          modificadores: item.modifiers?.map((m) => m.id) || [],
        }),
      ),
      total: total,
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
