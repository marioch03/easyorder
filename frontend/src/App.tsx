import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import AdminPage from "./features/admin/AdminPage";
import KdsSelectPage from "./features/auth/KdsSelectPage";
import LoginPage from "./features/auth/LoginPage";
import ProtectedRoute from "./features/auth/ProtectedRoute";
import RoleSelectPage from "./features/auth/RoleSelecfPage";
import { CartProvider } from "./features/cart/CartProvider";
import { ErrorPage } from "./features/error/ErrorPage";
import KdsPage from "./features/kds/KdsPage";
import ManagementPage from "./features/management/ManagementPage";
import CustomerPage from "./features/menu/pages/CustomerPage";
import { SessionProvider } from "./features/session/SessionProvider";
import { WebSocketProvider } from "./features/ws/WebSocketProvider";

function App() {
  return (
    <BrowserRouter>
      <Routes>
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

        {/* ENTORNO ADMINISTRACIÓN */}
        <Route path="/" element={<Navigate to="/staff" replace />} />
        <Route
          path="/staff"
          element={
            <ProtectedRoute allowedRoles={["ADMIN", "PERSONAL"]}>
              <WebSocketProvider>
                <ManagementPage />
              </WebSocketProvider>
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin"
          element={
            <ProtectedRoute allowedRoles={["ADMIN"]}>
              <AdminPage />
            </ProtectedRoute>
          }
        />
      <Route 
        path="/kds" 
        element={
          <ProtectedRoute allowedRoles={["ADMIN", "KDS"]}>
            <KdsSelectPage />
          </ProtectedRoute>
        } 
      />
      <Route 
        path="/kds/:zonaTrabajoSlug" 
        element={
          <ProtectedRoute allowedRoles={["ADMIN", "KDS"]}>
            <KdsPage /> 
          </ProtectedRoute>
        } 
      />
        <Route
          path="/auth/select-interface"
          element={
            <ProtectedRoute allowedRoles={["ADMIN"]}>
              <RoleSelectPage />
            </ProtectedRoute>
          }
        />

        {/* RUTAS PÚBLICAS */}
        <Route path="/auth/login" element={<LoginPage />} />
        <Route path="/error" element={<ErrorPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
