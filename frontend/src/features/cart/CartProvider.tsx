import { useEffect, useState } from "react";
import { useSession } from "../session/useSession";
import { CartContext } from "./CartContext";
import type { CartItem } from "./cart";

export const getCartLineId = (item: CartItem) => {
  const modsString =
    item.modifiers
      ?.map((m) => m.id)
      .sort()
      .join(",") || "";
  return `${item.id}-${item.note || ""}-${modsString}`;
};

export function CartProvider({ children }: { children: React.ReactNode }) {
  const { sessionCode } = useSession();
  const [items, setItems] = useState<CartItem[]>(() => {
    const stored = localStorage.getItem("cart");
    if (!stored) return [];
    try {
      return JSON.parse(stored);
    } catch {
      return [];
    }
  });

  useEffect(() => {
    localStorage.setItem("cart", JSON.stringify(items));
  }, [items]);

  useEffect(() => {
    const lastSessionCode = localStorage.getItem("lastSessionCode");
    if (sessionCode) {
      if (lastSessionCode && lastSessionCode !== sessionCode) {
        clearCart();
      }
      localStorage.setItem("lastSessionCode", sessionCode);
    }
  }, [sessionCode]);

  const addItem = (item: CartItem) => {
    setItems((prev) => {
      const newLineId = getCartLineId(item);

      const existing = prev.find((i) => getCartLineId(i) === newLineId);

      if (existing) {
        return prev.map((i) =>
          getCartLineId(i) === newLineId
            ? { ...i, quantity: i.quantity + item.quantity }
            : i,
        );
      }

      return [...prev, item];
    });
  };

  const removeItem = (cartLineId: string) => {
    setItems((prev) => prev.filter((i) => getCartLineId(i) !== cartLineId));
  };

  const decreaseItem = (cartLineId: string) => {
    setItems((prev) =>
      prev
        .map((i) =>
          getCartLineId(i) === cartLineId
            ? { ...i, quantity: i.quantity - 1 }
            : i,
        )
        .filter((i) => i.quantity > 0),
    );
  };

  const clearCart = () => {
    setItems([]);
  };

  return (
    <CartContext.Provider
      value={{ items, addItem, removeItem, decreaseItem, clearCart }}
    >
      {children}
    </CartContext.Provider>
  );
}
