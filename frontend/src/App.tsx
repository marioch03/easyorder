import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import LoginPage from "./features/auth/LoginPage";
import ProtectedRoute from "./features/auth/ProtectedRoute";
import { CartProvider } from "./features/cart/CartProvider";
import { ErrorPage } from "./features/error/ErrorPage";
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
        <Route path="/" element={<Navigate to="/admin" replace />} />
        <Route
          path="/admin"
          element={
            <ProtectedRoute>
              <WebSocketProvider>
                <ManagementPage />
              </WebSocketProvider>
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