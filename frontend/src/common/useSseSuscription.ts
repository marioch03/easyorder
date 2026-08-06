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

    const url = `${import.meta.env.VITE_BASE_URL}/sse/stream/${topic}`;

    console.log(`🔌 Conectando al canal de tiempo real: [${topic}]`);

    fetchEventSource(url, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
        Accept: "text/event-stream",
      },
      signal: abortController.signal,

      async onopen(response) {
        const contentType = response.headers.get("content-type");
        if (response.ok && contentType?.includes("text/event-stream")) {
          console.log(`✅ Conexión SSE establecida con éxito [${topic}]`);
          return;
        }

        if (response.status === 401 || response.status === 403) {
          console.error(
            `⛔ Error de autenticación en SSE (${response.status}) [${topic}]`,
          );
          abortController.abort();
          return;
        }

        throw new Error(
          `Error en respuesta del servidor: status ${response.status}`,
        );
      },

      onmessage(event) {
        if (
          !event.data ||
          event.data === "connected" ||
          event.data === "ping"
        ) {
          return;
        }

        console.log(`📥 MENSAJE RECIBIDO [${topic}]:`, event.data);

        if (event.data === "refresh") {
          console.log(`¡Señal de recarga recibida para el topic: ${topic}!`);
          onRefreshRef.current();
        }
      },

      onclose() {
        console.warn(
          `⚠️ Conexión SSE cerrada por el servidor [${topic}]. Reintentando...`,
        );
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
