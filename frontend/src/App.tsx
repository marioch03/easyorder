import { QueryClientProvider } from "@tanstack/react-query";
import { jwtDecode } from "jwt-decode";
import { lazy, Suspense, useEffect } from "react";
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { Toaster } from "sonner";
import { refreshToken } from "./features/auth/authService";
import ProtectedRoute from "./features/auth/ProtectedRoute";
import ComandasPage from "./features/comandas/ComandasPage";
import TenantLayout from "./features/tenant/TenantLayout";
import queryClient from "./lib/queryClient";
import useAuthStore from "./store/authStore";

const LoginPage = lazy(() => import("./features/auth/LoginPage"));
const CustomerPage = lazy(() => import("./features/menu/pages/CustomerPage"));
const ManagementPage = lazy(
  () => import("./features/management/ManagementPage"),
);
const TablesPage = lazy(() => import("./features/tables/TablesPage"));
const OrderPage = lazy(() => import("./features/orders/OrderPage"));
const AdminPage = lazy(() => import("./features/admin/AdminPage"));
const KdsSelectPage = lazy(() => import("./features/auth/KdsSelectPage"));
const KdsPage = lazy(() => import("./features/kds/KdsPage"));
const RoleSelectPage = lazy(() => import("./features/auth/RoleSelectPage"));
const ErrorPageCliente = lazy(
  () => import("./features/error/ErrorPageCliente"),
);
const ErrorPageManagement = lazy(
  () => import("./features/error/ErrorPageManagement"),
);

const PageLoader = () => (
  <div className="flex h-screen items-center justify-center p-4">
    <span>Cargando...</span>
  </div>
);

function App() {
  const setAuth = useAuthStore((state) => state.setAuth);
  const clearAuth = useAuthStore((state) => state.clearAuth);
  const setInitializing = useAuthStore((state) => state.setInitializing);

  useEffect(() => {
    const initializeApp = async () => {
      const currentPath = window.location.pathname;

      const isCustomerRoute =
        currentPath.includes("/cliente") ||
        currentPath === "/invalid" ||
        currentPath === "/error";

      if (isCustomerRoute) {
        setInitializing(false);
        return;
      }
      try {
        const data = await refreshToken();

        const decoded: any = jwtDecode(data.access_token);
        const user = {
          id: decoded.id,
          nombre: decoded.nombre || "Usuario",
          rol: decoded.roles,
          tenantId: decoded.tenantId,
        };

        setAuth(user, data.access_token);
      } catch (error) {
        clearAuth();
      } finally {
        setInitializing(false);
      }
    };

    initializeApp();
  }, [setAuth, clearAuth, setInitializing]);

  return (
    <BrowserRouter>
      <QueryClientProvider client={queryClient}>
        <Suspense fallback={<PageLoader />}>
          <Routes>
            {/* RUTAS GLOBALES (SIN SLUG) */}
            <Route path="/invalid" element={<ErrorPageCliente />} />
            <Route path="/error" element={<ErrorPageManagement />} />
            {/* 🟢 CONTENEDOR PADRE QUE CAPTURA EL SLUG */}
            <Route path="/:slug" element={<TenantLayout />}>
              <Route path="auth/login" element={<LoginPage />} />

              {/* ENTORNO CLIENTE */}
              <Route
                path="cliente"
                element={
                  <CustomerPage />
                }
              />

              {/* ENTORNO ADMINISTRACIÓN */}
              <Route element={<ProtectedRoute allowedRoles={["ADMIN"]} />}>
                <Route path="admin" element={<AdminPage />} />
                <Route path="select-interface" element={<RoleSelectPage />} />
              </Route>

              {/* ENTORNO STAFF (MESAS Y PEDIDOS) */}
              <Route
                element={
                  <ProtectedRoute allowedRoles={["ADMIN", "PERSONAL"]} />
                }
              >
                <Route path="staff" element={<ManagementPage />}>
                  <Route index element={<Navigate to="mesas" replace />} />
                  <Route path="mesas" element={<TablesPage />} />
                  <Route path="pedidos" element={<OrderPage />} />
                  <Route path="comandas" element={<ComandasPage />} />
                </Route>
              </Route>

              {/* ENTORNO KDS */}
              <Route
                element={<ProtectedRoute allowedRoles={["ADMIN", "KDS"]} />}
              >
                <Route path="kds" element={<KdsSelectPage />} />
                <Route path="kds/:zonaTrabajoSlug" element={<KdsPage />} />
              </Route>
            </Route>{" "}
            {/* 👈 AQUÍ CIERRA EL TENANT LAYOUT */}
            {/* REDIRECCIONES DE FALLBACK */}
            <Route path="/" element={<Navigate to="/error" replace />} />
            <Route path="*" element={<Navigate to="/error" replace />} />
          </Routes>
        </Suspense>

        <Toaster
          position="top-center"
          richColors={false}
          toastOptions={{ className: "eo-toast" }}
        />
      </QueryClientProvider>
    </BrowserRouter>
  );
}

export default App;
