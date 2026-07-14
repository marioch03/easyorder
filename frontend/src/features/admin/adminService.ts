import { privateApi, registerApi } from "../api/apiClient";
import type { MesaDTO, ProductoDTO, ProductoTipoDTO, UsuarioDTO, UsuarioRolDTO, ZonaDTO } from "./admin";

const BASE_URL = import.meta.env.VITE_BASE_URL;

export async function getZonas(): Promise<ZonaDTO[]> {
  const response = await privateApi.get<ZonaDTO[]>(`${BASE_URL}/api/admin/zonas/list`);
  return response.data;
}

export async function getMesas(): Promise<MesaDTO[]> {
  const response = await privateApi.get<MesaDTO[]>(`${BASE_URL}/api/admin/mesas/list`);
  return response.data;
}

export async function getProductos(): Promise<ProductoDTO[]> {
  const response = await privateApi.get<ProductoDTO[]>(`${BASE_URL}/api/admin/productos/all`);
  return response.data;
}

export async function getTiposProducto(): Promise<ProductoTipoDTO[]> {
  const response = await privateApi.get<ProductoTipoDTO[]>(`${BASE_URL}/api/admin/productos/tipos`);
  return response.data;
}

export async function getUsuarioRoles(): Promise<UsuarioRolDTO[]> {
  const response = await privateApi.get<UsuarioRolDTO[]>(`${BASE_URL}/api/admin/usuarios/roles`);
  return response.data;
}

export async function getUsuarios(): Promise<UsuarioDTO[]> {
  const response = await privateApi.get<UsuarioDTO[]>(`${BASE_URL}/api/admin/usuarios/list`);
  return response.data;
}

export async function crearMesa(numero: number, idZona: number) {
  const response = await privateApi.post("/mesas/create", {
    numero,
    idZona,
  });
  return response.data;
}

export async function eliminarMesa(numero: number) {

  const response = await privateApi.delete(`/mesas/${numero}`);
  return response.data;
}

export async function registrarUsuario(nombre: string, rol: string, clave: string) {
  const response = await registerApi.post("/register", {
    nombre,
    rol,
    clave,
  });
  return response.data;
}

export async function deshabilitarUsuario(id: number){
  const response = await privateApi.patch(`/usuarios/${id}`);
  return response.data;
}