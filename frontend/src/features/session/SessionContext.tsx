import { createContext } from "react";
import type { SessionContextType } from "./session";

export const SessionContext = createContext<SessionContextType | undefined>(undefined);

