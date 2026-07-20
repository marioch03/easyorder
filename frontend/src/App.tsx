import { lazy, Suspense } from "react";
import {
  BrowserRouter,
  Navigate,
  Outlet,
  Route,
  Routes,
} from "react-router-dom";
import ProtectedRoute from "./features/auth/ProtectedRoute";
import { CartProvider } from "./features/cart/CartProvider";
import { SessionProvider } from "./features/session/SessionProvider";
import { WebSocketProvider } from "./features/ws/WebSocketProvider";

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

const PageLoader = () => <div className="p-4">Cargando...</div>;

function App() {
  return (
    <BrowserRouter>
      <Suspense fallback={<PageLoader />}>
        <Routes>
          {/* RUTAS PÚBLICAS */}
          <Route path="/auth/login" element={<LoginPage />} />
          <Route path="/invalid" element={<ErrorPageCliente />} />
          <Route path="/error" element={<ErrorPageManagement />} />

          {/* ENTORNO CLIENTE */}
          <Route
            path="/cliente"
            element={
              <SessionProvider>
                <CartProvider>
                  <CustomerPage />
                </CartProvider>
              </SessionProvider>
            }
          />

          {/* ENTORNO ADMINISTRACIÓN Y STAFF */}
          <Route element={<ProtectedRoute allowedRoles={["ADMIN"]} />}>
            <Route path="/admin" element={<AdminPage />} />
            <Route path="/select-interface" element={<RoleSelectPage />} />
          </Route>

          <Route
            path="/staff"
            element={
              <ProtectedRoute allowedRoles={["ADMIN", "PERSONAL"]}>
                <WebSocketProvider>
                  <ManagementPage />
                </WebSocketProvider>
              </ProtectedRoute>
            }
          >
            <Route index element={<Navigate to="mesas" replace />} />

            <Route path="mesas" element={<TablesPage />} />
            <Route path="pedidos" element={<OrderPage />} />
          </Route>

          {/* ENTORNO KDS */}
          <Route
            path="/kds"
            element={
              <ProtectedRoute allowedRoles={["ADMIN", "KDS"]}>
                <Outlet />
              </ProtectedRoute>
            }
          >
            <Route index element={<KdsSelectPage />} />

            <Route path=":zonaTrabajoSlug" element={<KdsPage />} />
          </Route>

          <Route path="/" element={<Navigate to="/auth/login" replace />} />
          <Route path="*" element={<Navigate to="/error" replace />} />
        </Routes>
      </Suspense>
    </BrowserRouter>
  );
}

export default App;
