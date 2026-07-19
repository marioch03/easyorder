import { fetchEventSource } from "@microsoft/fetch-event-source";
import { useEffect, useRef } from "react";

interface SseOptions {
  topic: string;
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
        if (event.data === "refresh") {
          console.log(`¡Señal de recarga recibida para el topic: ${topic}!`);
          onRefreshRef.current();
        }
      },

      onclose() {
        console.warn(
          `⚠️ Conexión SSE cerrada para el topic: ${topic}. Reintentando...`,
        );
      },

      onerror(err) {
        console.error(`❌ Error en el canal SSE [${topic}]:`, err);
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
