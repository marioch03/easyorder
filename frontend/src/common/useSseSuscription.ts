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

async function requestSseTicket(tenantSlug: string): Promise<string> {
  const token = getAccessToken();
  const sessionCode = localStorage.getItem("sessionCode");

  if (!token && !sessionCode) {
    throw new Error("No hay credenciales activas (token o sessionCode)");
  }

  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    "X-Tenant-Slug": tenantSlug,
  };

  // Si hay token de usuario (empleado/admin), priorizarlo sobre un posible sessionCode residual
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  } else if (sessionCode) {
    headers["X-Session-Code"] = sessionCode;
  }

  const response = await fetch(`${import.meta.env.VITE_BASE_URL}/sse/ticket`, {
    method: "POST",
    headers,
  });

  if (!response.ok) {
    throw new Error(`Error obteniendo ticket SSE: HTTP ${response.status}`);
  }

  const data = (await response.json()) as { ticket: string };
  return data.ticket;
}

export function useSseSubscription({ topic, onRefresh }: SseOptions) {
  const onRefreshRef = useRef(onRefresh);
  const refreshTimeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  onRefreshRef.current = onRefresh;

  useEffect(() => {
    let activeEventSource: EventSource | null = null;
    let isCancelled = false;
    let retryDelay = INITIAL_RETRY_DELAY;
    let retryTimeoutId: ReturnType<typeof setTimeout> | null = null;

    const tenantSlug = getTenantSlug();

    if (!tenantSlug) {
      console.error(`[SSE] No se pudo determinar el tenant. topic=${topic}`);
      return;
    }

    let hasConnectedOnce = false;
    let lastEventId: string | null = null;

    const scheduleRefresh = () => {
      if (refreshTimeoutRef.current !== null) {
        return;
      }
      refreshTimeoutRef.current = setTimeout(() => {
        refreshTimeoutRef.current = null;
        void onRefreshRef.current();
      }, REFRESH_DEBOUNCE_MS);
    };

    const scheduleRetry = () => {
      if (isCancelled) return;
      const jitter = Math.floor(Math.random() * 1_000);
      const delay = Math.min(retryDelay + jitter, MAX_RETRY_DELAY);
      retryDelay = Math.min(retryDelay * 2, MAX_RETRY_DELAY);

      console.warn(
        `[SSE] Reintentando conexión nativa en ${delay}ms. topic=${topic}`,
      );
      retryTimeoutId = setTimeout(() => {
        void connect();
      }, delay);
    };

    const connect = async () => {
      if (isCancelled) return;

      try {
        const ticket = await requestSseTicket(tenantSlug);
        if (isCancelled) return;

        const streamUrl = `${import.meta.env.VITE_BASE_URL}/sse/stream/${topic}?ticket=${encodeURIComponent(ticket)}`;
        const es = new EventSource(streamUrl);
        activeEventSource = es;

        es.onopen = () => {
          retryDelay = INITIAL_RETRY_DELAY;
          console.debug(
            `[SSE] Conexión nativa establecida con éxito. topic=${topic}`,
          );

          // [SSE-03] Si es una reconexión tras pérdida de enlace, reconciliar estado inmediatamente
          if (hasConnectedOnce) {
            console.info(
              `[SSE] Reconexión exitosa detectada. Reconciliando estado de la aplicación. topic=${topic}, lastEventId=${lastEventId}`,
            );
            scheduleRefresh();
          }
          hasConnectedOnce = true;
        };

        es.addEventListener("connected", (event: MessageEvent) => {
          if (event.lastEventId) {
            lastEventId = event.lastEventId;
          }
        });

        es.addEventListener(SSE_REFRESH_EVENT, (event: MessageEvent) => {
          if (event.lastEventId) {
            lastEventId = event.lastEventId;
          }
          console.debug(
            `[SSE] Evento refresh recibido. topic=${topic}, eventId=${event.lastEventId}`,
          );
          scheduleRefresh();
        });

        es.onerror = (error) => {
          console.warn(
            `[SSE] Conexión cerrada o con error. topic=${topic}`,
            error,
          );
          es.close();
          if (activeEventSource === es) {
            activeEventSource = null;
          }
          scheduleRetry();
        };
      } catch (error) {
        console.error(
          `[SSE] Error durante solicitud de ticket o conexión. topic=${topic}`,
          error,
        );
        scheduleRetry();
      }
    };

    void connect();

    return () => {
      isCancelled = true;
      if (retryTimeoutId !== null) {
        clearTimeout(retryTimeoutId);
      }
      if (activeEventSource !== null) {
        activeEventSource.close();
        activeEventSource = null;
      }
      if (refreshTimeoutRef.current !== null) {
        clearTimeout(refreshTimeoutRef.current);
        refreshTimeoutRef.current = null;
      }
      console.debug(`[SSE] Conexión cerrada. topic=${topic}`);
    };
  }, [topic]);
}
