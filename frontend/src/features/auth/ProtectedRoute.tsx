import type { ReactNode } from "react";
import { Navigate, Outlet } from "react-router-dom";
import { getRolesFromToken } from "./jwtService";

type ProtectedRouteProps = {
  children?: ReactNode; // 1. Hacemos que children sea OPCIONAL (?) y usamos ReactNode
  allowedRoles?: string[];
};

export default function ProtectedRoute({
  children,
  allowedRoles,
}: ProtectedRouteProps) {
  const accessToken = localStorage.getItem("accessToken");
  const refreshToken = localStorage.getItem("refreshToken");

  if (!accessToken && !refreshToken) {
    return <Navigate to="/auth/login" replace />;
  }

  if (allowedRoles && accessToken) {
    const roles = getRolesFromToken(accessToken);

    const hasRole = allowedRoles.some((role) => roles.includes(role));

    if (!hasRole) {
      return <Navigate to="/error" replace />;
    }
  }

  return children ? <>{children}</> : <Outlet />;
}
