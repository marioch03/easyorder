export type CartItem = {
  id: number;
  name: string;
  price: number;
  quantity: number;
  image?: string;
  note?: string;
};

export type CartContextType = {
  items: CartItem[];
  addItem: (item: CartItem) => void;
  decreaseItem: (id: number) => void;
  removeItem: (id: number) => void;
  clearCart: () => void;
};