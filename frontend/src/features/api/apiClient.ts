import axios, { AxiosError, type InternalAxiosRequestConfig } from "axios";
import {
  clearAccessToken,
  getAccessToken,
  setAccessToken,
} from "../../utils/token";

// urls
const BASE_URL = import.meta.env.VITE_BASE_URL;
const BASE_URL_CLIENTE = `${BASE_URL}/cliente`;
const BASE_URL_ADMIN = `${BASE_URL}/admin`;
const BASE_URL_AUTH = `${BASE_URL}/auth`;
const BASE_URL_PUBLIC = `${BASE_URL}/public`;

// obtener tenant
const getTenantSlug = (): string | null => {
  const storedSlug = localStorage.getItem("tenant_slug");
  if (storedSlug) return storedSlug;

  const possibleSlug = window.location.pathname.split("/").filter(Boolean)[0];
  if (
    possibleSlug &&
    !["auth", "invalid", "login", "forbidden", "error"].includes(possibleSlug)
  ) {
    return possibleSlug;
  }
  return null;
};

// -------------------------------------------------------------
// CONTROL DE CONCURRENCIA PARA EL REFRESH (COLA GLOBAL)
// -------------------------------------------------------------
let isRefreshing = false;
let failedQueue: Array<{
  resolve: (value: string) => void;
  reject: (reason?: unknown) => void;
}> = [];

const processQueue = (error: unknown, token: string | null = null) => {
  failedQueue.forEach((prom) => {
    if (error) prom.reject(error);
    else if (token) prom.resolve(token);
  });
  failedQueue = [];
};

/**
 * Función global que solicita el refresco respetando la cola.
 * Puede ser llamada por interceptores o directamente desde App.tsx
 */
export const requestNewToken = async (): Promise<string> => {
  if (isRefreshing) {
    return new Promise((resolve, reject) => {
      failedQueue.push({ resolve, reject });
    });
  }

  isRefreshing = true;

  try {
    const res = await publicAuthApi.post<{
      access_token?: string;
      accessToken?: string;
    }>("/refresh");

    const newAccessToken = res.data.accessToken || res.data.access_token || "";

    if (!newAccessToken) throw new Error("No token returned");

    setAccessToken(newAccessToken);
    processQueue(null, newAccessToken);
    return newAccessToken;
  } catch (error) {
    processQueue(error, null);
    clearAccessToken();
    window.dispatchEvent(new Event("auth:logout"));
    throw error;
  } finally {
    isRefreshing = false;
  }
};

// -------------------------------------------------------------
// INSTANCIAS DE AXIOS
// -------------------------------------------------------------
export const publicAuthApi = axios.create({
  baseURL: BASE_URL_AUTH,
  headers: { "Content-Type": "application/json" },
  withCredentials: true,
});

export const publicApi = axios.create({
  baseURL: BASE_URL_PUBLIC,
  headers: { "Content-Type": "application/json" },
});

export const initApi = axios.create({
  baseURL: BASE_URL_CLIENTE,
  headers: { "Content-Type": "application/json" },
});

export const privateAuthApi = axios.create({
  baseURL: BASE_URL_AUTH,
  headers: { "Content-Type": "application/json" },
  withCredentials: true,
});

export const privateApi = axios.create({
  baseURL: BASE_URL_ADMIN,
  headers: { "Content-Type": "application/json" },
});

export const clientApi = axios.create({
  baseURL: BASE_URL_CLIENTE,
  headers: { "Content-Type": "application/json" },
});

// -------------------------------------------------------------
// INTERCEPTORES REQUEST (Inyección de Tokens y Headers)
// -------------------------------------------------------------
const injectBearerToken = (config: InternalAxiosRequestConfig) => {
  const token = getAccessToken();
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
};

privateAuthApi.interceptors.request.use(injectBearerToken, (error) =>
  Promise.reject(error),
);
privateApi.interceptors.request.use(injectBearerToken, (error) =>
  Promise.reject(error),
);

clientApi.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const sessionCode = localStorage.getItem("sessionCode");
    if (sessionCode) config.headers["X-Session-Code"] = sessionCode;
    return config;
  },
  (error) => Promise.reject(error),
);

const apisToInjectTenant = [
  publicAuthApi,
  privateAuthApi,
  initApi,
  clientApi,
  privateApi,
];
apisToInjectTenant.forEach((apiInstance) => {
  apiInstance.interceptors.request.use((config: InternalAxiosRequestConfig) => {
    const slug = getTenantSlug();
    if (slug) config.headers["X-Tenant-Slug"] = slug;
    return config;
  });
});

// -------------------------------------------------------------
// INTERCEPTORES RESPONSE (Manejo de 401 y Refresco)
// -------------------------------------------------------------
clientApi.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && [401, 403, 404].includes(error.response.status)) {
      localStorage.removeItem("sessionCode");
      window.location.href = "/invalid";
    }
    return Promise.reject(error);
  },
);

const setupAuthResponseInterceptor = (apiInstance: any) => {
  apiInstance.interceptors.response.use(
    (response: any) => response,
    async (error: AxiosError) => {
      const originalRequest = error.config as InternalAxiosRequestConfig & {
        _retry?: boolean;
      };

      if (!originalRequest || originalRequest.url?.includes("/refresh")) {
        return Promise.reject(error);
      }

      if (error.response?.status === 401 && !originalRequest._retry) {
        originalRequest._retry = true;

        try {
          const newAccessToken = await requestNewToken();
          originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;

          return apiInstance(originalRequest);
        } catch (refreshError) {
          return Promise.reject(refreshError);
        }
      }

      return Promise.reject(error);
    },
  );
};

setupAuthResponseInterceptor(privateApi);
setupAuthResponseInterceptor(privateAuthApi);
