import { privateApi } from "../api/apiClient";
import type { ProductoDTO, ProductoTipoDTO, UsuarioRolDTO, ZonaDTO } from "./admin";

const BASE_URL = import.meta.env.VITE_BASE_URL;

export async function getZonas(): Promise<ZonaDTO[]> {
  const response = await privateApi.get<ZonaDTO[]>(`${BASE_URL}/zona/list`);
  return response.data;
}

export async function getProductos(): Promise<ProductoDTO[]> {
  const response = await privateApi.get<ProductoDTO[]>(`${BASE_URL}/producto/all`);
  return response.data;
}

export async function getTiposProducto(): Promise<ProductoTipoDTO[]> {
  const response = await privateApi.get<ProductoTipoDTO[]>(`${BASE_URL}/producto/tipos`);
  return response.data;
}

export async function getUsuarioRoles(): Promise<UsuarioRolDTO[]> {
  const response = await privateApi.get<UsuarioRolDTO[]>(`${BASE_URL}/usuario/roles`);
  return response.data;
}