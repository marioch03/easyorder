import { jwtDecode } from "jwt-decode";

type JwtPayload = {
  roles: string[];
};

export function getRolesFromToken(token: string): string[] {
  const decoded = jwtDecode<JwtPayload>(token);
  return decoded.roles ?? [];
}