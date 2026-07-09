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
      <SessionProvider>
        <WebSocketProvider>
          <CartProvider>
            <Routes>
              <Route path="/" element={<Navigate to="/admin" replace />} />
              <Route path="/cliente" element={<CustomerPage />} />
              <Route path="/auth/login" element={<LoginPage />} />
              <Route
                path="/admin"
                element={
                  <ProtectedRoute>
                    <ManagementPage />
                  </ProtectedRoute>
                }
              />
              <Route path="/error" element={<ErrorPage />} />
            </Routes>
          </CartProvider>
        </WebSocketProvider>
      </SessionProvider>
    </BrowserRouter>
  );
}

export default App;
