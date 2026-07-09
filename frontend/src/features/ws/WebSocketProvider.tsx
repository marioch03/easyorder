import { Client } from "@stomp/stompjs";
import React, { useEffect, useRef, useState } from "react";
import { WSContext } from "./WebSocketContext";

export function WebSocketProvider({ children }: { children: React.ReactNode }) {
  const clientRef = useRef<Client | null>(null);

  const [mesas, setMesas] = useState<any[]>([]);
  const [pedidos, setPedidos] = useState<any[]>([]);

  const ws_url = import.meta.env.VITE_WS_URL;

  useEffect(() => {
    const client = new Client({
      brokerURL: ws_url,
      connectHeaders: {
        Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
      },
      reconnectDelay: 5000,
      debug: (msg) => console.log(msg),
    });

    client.onConnect = () => {
      client.subscribe("/topic/mesas", (msg) => {
        setMesas(JSON.parse(msg.body));
      });

      client.subscribe("/topic/pedidos", (msg) => {
        setPedidos(JSON.parse(msg.body));
      });
    };

    client.activate();
    clientRef.current = client;

    return () => {
      client.deactivate();
    };
  }, []);

  return (
    <WSContext.Provider value={{ mesas, pedidos }}>
      {children}
    </WSContext.Provider>
  );
}

