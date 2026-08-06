import { privateApi } from "../api/apiClient";
import type { MesaDTO, ZonaDTO } from "./tables";

export async function getMesas(): Promise<MesaDTO[]> {
  const response = await privateApi.get<MesaDTO[]>("/mesas");
  return response.data;
}

export async function getZonas(): Promise<ZonaDTO[]> {
  const response = await privateApi.get<ZonaDTO[]>("/zonas");
  return response.data;
}

export async function crearMesa(numero: number, idZona: number) {
  const response = await privateApi.post("/mesas", {
    numero,
    idZona,
  });
  return response.data;
}

export async function crearSesionMesa(mesaId: number) {
  const response = await privateApi.post(`/sesiones/open/${mesaId}`);
  return response.data;
}

export async function cerrarSesionMesa(sessionCode: string) {
  const response = await privateApi.post(`/sesiones/close/${sessionCode}`);
  return response.data;
}

export async function getCuentaMesa(mesaId: number) {
  const response = await privateApi.get(`/pedidos/${mesaId}/cuenta`);
  return response.data;
}
