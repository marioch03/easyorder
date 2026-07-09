import { createContext } from "react";
import type { CartContextType } from "./cart";

export const CartContext = createContext<CartContextType | undefined>(undefined);