import { privateApi } from "../api/apiClient";
import type { MesaDTO, ZonaDTO } from "./tables";

export async function getMesas(): Promise<MesaDTO[]> {
  const response = await privateApi.get<MesaDTO[]>("/mesas/list");
  return response.data;
}

export async function getZonas(): Promise<ZonaDTO[]> {
  const response = await privateApi.get<ZonaDTO[]>("/zonas/list");
  return response.data;
}

export async function crearMesa(numero: number, idZona: number) {
  const response = await privateApi.post("/mesas/create", {
    numero,
    idZona,
  });
  return response.data;
}

export async function crearSesionMesa(mesaId: number) {
  await privateApi.post(`/sesiones/create/${mesaId}`);
}

export async function cerrarSesionMesa(sessionCode: string) {
  await privateApi.post(`/sesiones/close/${sessionCode}`);
}

export async function getCuentaMesa(mesaId: number) {
  const response = await privateApi.get(`/pedidos/${mesaId}/cuenta`);
  return response.data;
}