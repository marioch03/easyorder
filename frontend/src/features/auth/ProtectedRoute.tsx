import type { ReactNode } from "react";
import { Navigate, Outlet } from "react-router-dom";
import useAuth from "../../hooks/useAuth"; // Ajusta la ruta de importación a tu hook

type ProtectedRouteProps = {
  children?: ReactNode;
  allowedRoles?: string[];
};

export default function ProtectedRoute({
  children,
  allowedRoles,
}: ProtectedRouteProps) {
  const { user, isAuthenticated, isInitializing } = useAuth();

  if (isInitializing) {
    return <div>Cargando sesión...</div>;
  }

  if (!isAuthenticated || !user) {
    const tenantSlug = localStorage.getItem("tenant_slug") || "";
    const loginPath = `/${tenantSlug}/auth/login`;
    return <Navigate to={loginPath} replace />;
  }

  if (allowedRoles) {
    const hasRole = allowedRoles.includes(user.rol);
    if (!hasRole) {
      return <Navigate to="/error" replace />;
    }
  }

  return children ? <>{children}</> : <Outlet />;
}
