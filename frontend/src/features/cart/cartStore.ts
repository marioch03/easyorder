import { create } from "zustand";
import { persist } from "zustand/middleware";
import type { ModificadorDTO } from "../../common/types";

// ──────────────────────────────────────────────
// Tipos
// ──────────────────────────────────────────────

export type CartItem = {
  id: number;
  name: string;
  price: number;
  quantity: number;
  image?: string;
  note?: string;
  modifiers?: ModificadorDTO[];
};

interface CartState {
  items: CartItem[];
  addItem: (item: CartItem) => void;
  decreaseItem: (cartLineId: string) => void;
  removeItem: (cartLineId: string) => void;
  clearCart: () => void;
}

// ──────────────────────────────────────────────
// Helper: identificador único de línea de carrito
// (combinación de id + nota + modificadores)
// ──────────────────────────────────────────────

export const getCartLineId = (item: CartItem): string => {
  const modsString =
    item.modifiers
      ?.map((m) => m.id)
      .sort()
      .join(",") || "";
  return `${item.id}-${item.note || ""}-${modsString}`;
};

// ──────────────────────────────────────────────
// Store
// ──────────────────────────────────────────────

export const useCartStore = create<CartState>()(
  persist(
    (set, get) => ({
      items: [],

      addItem: (item) => {
        const newLineId = getCartLineId(item);
        const existing = get().items.find(
          (i) => getCartLineId(i) === newLineId,
        );

        if (existing) {
          set((state) => ({
            items: state.items.map((i) =>
              getCartLineId(i) === newLineId
                ? { ...i, quantity: i.quantity + item.quantity }
                : i,
            ),
          }));
        } else {
          set((state) => ({ items: [...state.items, item] }));
        }
      },

      decreaseItem: (cartLineId) => {
        set((state) => ({
          items: state.items
            .map((i) =>
              getCartLineId(i) === cartLineId
                ? { ...i, quantity: i.quantity - 1 }
                : i,
            )
            .filter((i) => i.quantity > 0),
        }));
      },

      removeItem: (cartLineId) => {
        set((state) => ({
          items: state.items.filter(
            (i) => getCartLineId(i) !== cartLineId,
          ),
        }));
      },

      clearCart: () => set({ items: [] }),
    }),
    {
      name: "cart", // clave en localStorage — compatible con la clave anterior
    },
  ),
);

export default useCartStore;
