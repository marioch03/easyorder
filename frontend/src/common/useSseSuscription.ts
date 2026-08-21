import { fetchEventSource } from "@microsoft/fetch-event-source";
import { useEffect, useRef } from "react";
import { getAccessToken } from "../utils/token";
import type { SseTopic } from "./types";

interface SseOptions {
  topic: SseTopic;
  onRefresh: () => void | Promise<void>;
}

const SSE_REFRESH_EVENT = "refresh";

const INITIAL_RETRY_DELAY = 1_000;
const MAX_RETRY_DELAY = 30_000;
const REFRESH_DEBOUNCE_MS = 250;

function getTenantSlug(): string | null {
  const storedSlug = localStorage.getItem("tenant_slug");

  if (storedSlug) {
    return storedSlug;
  }

  const possibleSlug = window.location.pathname.split("/").filter(Boolean)[0];

  if (
    possibleSlug &&
    !["auth", "invalid", "login", "forbidden", "error"].includes(possibleSlug)
  ) {
    return possibleSlug;
  }

  return null;
}

export function useSseSubscription({ topic, onRefresh }: SseOptions) {
  const onRefreshRef = useRef(onRefresh);
  const refreshTimeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  onRefreshRef.current = onRefresh;

  useEffect(() => {
    const abortController = new AbortController();

    let retryDelay = INITIAL_RETRY_DELAY;
    let intentionallyClosed = false;

    const tenantSlug = getTenantSlug();

    if (!tenantSlug) {
      console.error(`[SSE] No se pudo determinar el tenant. topic=${topic}`);

      return () => {
        abortController.abort();
      };
    }

    const url = `${import.meta.env.VITE_BASE_URL}/sse/stream/${topic}`;

    const scheduleRefresh = () => {
      if (refreshTimeoutRef.current !== null) {
        return;
      }

      refreshTimeoutRef.current = setTimeout(() => {
        refreshTimeoutRef.current = null;
        void onRefreshRef.current();
      }, REFRESH_DEBOUNCE_MS);
    };

    const connectOnce = async (): Promise<void> => {
      const token = getAccessToken();
      if (!token) {
        console.error(`[SSE] No existe accessToken. topic=${topic}`);
        // Lanzar para que el bucle externo aplique backoff y reintente
        throw new Error("No hay accessToken");
      }

      console.debug(`[SSE] Conectando. tenant=${tenantSlug}, topic=${topic}`);

      await fetchEventSource(url, {
        method: "GET",

        headers: {
          Authorization: `Bearer ${token}`,
          Accept: "text/event-stream",
          "X-Tenant-Slug": tenantSlug,
        },

        signal: abortController.signal,

        async onopen(response) {
          const contentType = response.headers.get("content-type");

          if (response.ok && contentType?.includes("text/event-stream")) {
            retryDelay = INITIAL_RETRY_DELAY;
            console.debug(`[SSE] Conexión establecida. topic=${topic}`);
            return;
          }

          if (response.status === 401 || response.status === 403) {
            console.warn(
              `[SSE] Acceso rechazado (${response.status}). topic=${topic}. Reintentando con token renovado.`,
            );
            // Lanzar en lugar de abortar: el bucle externo re-leerá el token
            // (el interceptor de axios puede haberlo renovado entre tanto).
            throw new Error(`SSE_AUTH_ERROR:${response.status}`);
          }

          throw new Error(`Respuesta SSE inesperada: ${response.status}`);
        },

        onmessage(event) {
          if (event.event !== SSE_REFRESH_EVENT) {
            return;
          }

          console.debug(`[SSE] Refresh recibido. topic=${topic}`);

          scheduleRefresh();
        },

        onclose() {
          if (intentionallyClosed) {
            return;
          }

          console.warn(
            `[SSE] Conexión cerrada por el servidor. topic=${topic}`,
          );

          // Lanzar para que el bucle externo controle el reintento
          throw new Error("Conexión SSE cerrada inesperadamente");
        },

        onerror(error) {
          // Propagar siempre: el control del retry lo tiene el bucle externo,
          // que re-lee el token en cada intento.
          throw error;
        },
      });
    };

    /**
     * Bucle de reintentos con backoff exponencial + jitter.
     * Llama a connectOnce() en cada iteración, lo que garantiza
     * que el token se re-lee desde localStorage en cada intento.
     */
    const startRetryLoop = async (): Promise<void> => {
      while (!abortController.signal.aborted && !intentionallyClosed) {
        try {
          await connectOnce();
          // connectOnce solo resuelve si la conexión terminó limpiamente
          // (señal de abort). Si llegamos aquí, no hay que reintentar.
          break;
        } catch (error) {
          if (abortController.signal.aborted || intentionallyClosed) {
            break;
          }

          const jitter = Math.floor(Math.random() * 1_000);
          const delay = Math.min(retryDelay + jitter, MAX_RETRY_DELAY);
          retryDelay = Math.min(retryDelay * 2, MAX_RETRY_DELAY);

          console.warn(
            `[SSE] Error. topic=${topic}. Reintentando en ${delay}ms`,
            error,
          );

          await new Promise<void>((resolve) => setTimeout(resolve, delay));
        }
      }
    };

    void startRetryLoop().catch((error) => {
      if (!abortController.signal.aborted) {
        console.error(
          `[SSE] Error fatal en bucle de reintentos. topic=${topic}`,
          error,
        );
      }
    });

    return () => {
      intentionallyClosed = true;
      abortController.abort();

      if (refreshTimeoutRef.current !== null) {
        clearTimeout(refreshTimeoutRef.current);
        refreshTimeoutRef.current = null;
      }

      console.debug(`[SSE] Conexión cerrada. topic=${topic}`);
    };
  }, [topic]);
}
