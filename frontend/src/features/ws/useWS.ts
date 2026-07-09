import { useContext } from "react";
import { WSContext } from "./WebSocketContext";

export function useWS() {
  const ws = useContext(WSContext);
  if (!ws) throw new Error("useWS debe usarse dentro de WebSocketProvider");
  return ws;
}