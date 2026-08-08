import type { ModificadorDTO } from "../../common/types";

export type CartItem = {
  id: number;
  name: string;
  price: number;
  quantity: number;
  image?: string;
  note?: string;
  modifiers?: ModificadorDTO[];
};

export type CartContextType = {
  items: CartItem[];
  addItem: (item: CartItem) => void;
  decreaseItem: (cartLineId: string) => void;
  removeItem: (cartLineId: string) => void;
  clearCart: () => void;
};
