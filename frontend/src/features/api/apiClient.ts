import axios, { AxiosError, type InternalAxiosRequestConfig } from "axios";

const BASE_URL = import.meta.env.VITE_BASE_URL;
const BASE_URL_CLIENTE = `${BASE_URL}/api/cliente`;
const BASE_URL_ADMIN = `${BASE_URL}/api/admin`;
const BASE_URL_AUTH = `${BASE_URL}/auth`;

let isRefreshing = false;
let failedQueue: Array<{
  resolve: (value: string) => void;
  reject: (reason?: unknown) => void;
}> = [];

const processQueue = (error: unknown, token: string | null = null) => {
  failedQueue.forEach((prom) => {
    if (error) prom.reject(error);
    else prom.resolve(token!);
  });
  failedQueue = [];
};

export const authApi = axios.create({
  baseURL: BASE_URL_AUTH,
  headers: {
    "Content-Type": "application/json",
  },
});

export const initApi = axios.create({
  baseURL: BASE_URL_CLIENTE,
  headers: {
    "Content-Type": "application/json",
  },
});

export const publicApi = axios.create({
  baseURL: BASE_URL_CLIENTE,
  headers: {
    "Content-Type": "application/json",
  },
});

publicApi.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const sessionCode = localStorage.getItem("sessionCode");

    if (sessionCode) {
      config.headers["X-Session-Code"] = sessionCode;
    }
    return config;
  },
  (error: AxiosError) => Promise.reject(error),
);

publicApi.interceptors.response.use(
  (response) => response,
  (error) => {
    if (
      error.response &&
      (error.response.status === 401 || error.response.status === 403 || error.response.status === 404)
    ) {
      localStorage.removeItem("sessionCode");
      window.location.href = "/error";
    }
    return Promise.reject(error);
  },
);

export const privateApi = axios.create({
  baseURL: BASE_URL_ADMIN,
  headers: {
    "Content-Type": "application/json",
  },
});

privateApi.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem("accessToken");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error: AxiosError) => Promise.reject(error),
);

privateApi.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config;
    if (!originalRequest) return Promise.reject(error);

    const customRequest = originalRequest as InternalAxiosRequestConfig & {
      _retry?: boolean;
    };

    if (
      (error.response?.status === 401 || error.response?.status === 403) &&
      !customRequest._retry
    ) {
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        }).then((token) => {
          customRequest.headers.Authorization = `Bearer ${token}`;
          return privateApi(customRequest);
        });
      }

      customRequest._retry = true;
      isRefreshing = true;

      try {
        const refreshToken = localStorage.getItem("refreshToken");
        if (!refreshToken) throw new Error("No hay refresh token");

        const res = await authApi.post(`/refresh`, {
          refreshToken,
        });
        const { access_token: accessToken } = res.data;

        localStorage.setItem("accessToken", accessToken);
        customRequest.headers.Authorization = `Bearer ${accessToken}`;
        processQueue(null, accessToken);
        return privateApi(customRequest);
      } catch (refreshError) {
        processQueue(refreshError, null);
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        window.location.href = "/auth/login";
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  },
);
