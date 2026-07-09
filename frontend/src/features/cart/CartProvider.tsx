import { useEffect, useState } from "react";
import { useSession } from "../session/useSession";
import { CartContext } from "./CartContext";
import type { CartItem } from "./cart";

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
      const existing = prev.find(
        (i) => i.id === item.id && i.note === item.note,
      );
      if (existing) {
        return prev.map((i) =>
          i.id === item.id && i.note === item.note
            ? { ...i, quantity: i.quantity + item.quantity }
            : i,
        );
      }

      return [...prev, item];
    });
  };

  const removeItem = (id: number) => {
    setItems((prev) => prev.filter((i) => i.id !== id));
  };

  const decreaseItem = (id: number) => {
    setItems((prev) =>
      prev
        .map((i) => (i.id === id ? { ...i, quantity: i.quantity - 1 } : i))
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
