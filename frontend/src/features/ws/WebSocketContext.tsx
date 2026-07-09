import { createContext } from "react";
import type { WSContextType } from "./ws";

export const WSContext = createContext<WSContextType | undefined>(undefined);
