import type { MesaDTO, ProductoTipoDTO, ZonaDTO } from "../../common/types";
import { privateApi, privateAuthApi } from "../api/apiClient";
import type {
  EditProductPayload,
  ProductoDTO,
  UsuarioDTO,
  UsuarioRolDTO,
} from "./admin";

export async function getZonas(): Promise<ZonaDTO[]> {
  const response = await privateApi.get<ZonaDTO[]>("/zonas");
  return response.data;
}

export async function getMesas(): Promise<MesaDTO[]> {
  const response = await privateApi.get<MesaDTO[]>("/mesas");
  return response.data;
}

export async function getProductos(): Promise<ProductoDTO[]> {
  const response = await privateApi.get<ProductoDTO[]>("/productos");
  return response.data;
}

export async function getTiposProducto(): Promise<ProductoTipoDTO[]> {
  const response = await privateApi.get<ProductoTipoDTO[]>("/productos/tipos");
  return response.data;
}

export async function getUsuarioRoles(): Promise<UsuarioRolDTO[]> {
  const response = await privateApi.get<UsuarioRolDTO[]>("/usuarios/roles");
  return response.data;
}

export async function getUsuarios(): Promise<UsuarioDTO[]> {
  const response = await privateApi.get<UsuarioDTO[]>(`/usuarios`);
  return response.data;
}

export async function crearMesa(numero: number, idZona: number) {
  const response = await privateApi.post("/mesas", {
    numero,
    idZona,
  });
  return response.data;
}

export async function eliminarMesa(numero: number) {
  const response = await privateApi.delete(`/mesas/${numero}`);
  return response.data;
}

export async function registrarUsuario(
  nombre: string,
  rol: string,
  clave: string,
  tenantSlug: string,
) {
  const response = await privateAuthApi.post("/register", {
    nombre,
    rol,
    clave,
    tenantSlug,
  });
  return response.data;
}

export async function deshabilitarUsuario(id: number) {
  const response = await privateApi.patch(`/usuarios/${id}`);
  return response.data;
}

export async function editarProducto(producto: EditProductPayload) {
  const response = await privateApi.put(`/productos/${producto.id}`, producto);

  return response.data;
}
