import type { JSX } from "react";
import { Navigate } from "react-router-dom";
import { getRolesFromToken } from "./jwtService";


type ProtectedRouteProps = {
  children: JSX.Element;
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

    const hasRole = allowedRoles.some(role =>
      roles.includes(role)
    );

    if (!hasRole) {
      return <Navigate to="/forbidden" replace />;
    }
  }

  return children;
}