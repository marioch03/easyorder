import { useEffect } from "react";
import { useCartStore } from "./cartStore";

export function useCartSessionSync(sessionCode: string | null) {
  const clearCart = useCartStore((state) => state.clearCart);

  useEffect(() => {
    if (!sessionCode) return;

    const lastSessionCode = localStorage.getItem("lastSessionCode");
    if (lastSessionCode && lastSessionCode !== sessionCode) {
      clearCart();
    }
    localStorage.setItem("lastSessionCode", sessionCode);
  }, [sessionCode, clearCart]);
}
