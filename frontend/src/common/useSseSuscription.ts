import { fetchEventSource } from "@microsoft/fetch-event-source";
import { useEffect, useRef } from "react";
import type { SseTopic } from "./types";

interface SseOptions {
  topic: SseTopic;
  onRefresh: () => void;
}

export function useSseSubscription({ topic, onRefresh }: SseOptions) {
  const onRefreshRef = useRef(onRefresh);
  onRefreshRef.current = onRefresh;

  useEffect(() => {
    const token = localStorage.getItem("accessToken");
    const abortController = new AbortController();

    const url = `${import.meta.env.VITE_BASE_URL}/api/sse/stream/${topic}`;

    console.log(`🔌 Conectando al canal de tiempo real: [${topic}]`);

    fetchEventSource(url, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
        Accept: "text/event-stream",
      },
      signal: abortController.signal,

      onmessage(event) {
        console.log(`📥 MENSAJE BRUTO RECIBIDO [${topic}]:`, event.data);
        if (event.data === "refresh") {
          console.log(`¡Señal de recarga recibida para el topic: ${topic}!`);
          onRefreshRef.current();
        }
      },

      onclose() {
        console.warn(
          `⚠️ Conexión SSE cerrada para el topic: ${topic}. Reconectando...`,
        );
        throw new Error(`Conexión SSE cerrada por el servidor [${topic}]`);
      },

      onerror(err) {
        if (abortController.signal.aborted) return;
        console.error(`❌ Error en el canal SSE [${topic}]:`, err);
        return 2000;
      },
    });

    return () => {
      abortController.abort();
      console.log(
        `🛑 Conexión SSE cerrada limpiamente para el topic: [${topic}]`,
      );
    };
  }, [topic]);
}
