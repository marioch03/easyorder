import { publicApi } from "../../api/apiClient";
import type { Pedido } from "../types/bill";

export async function getMesa() {
  try {
    const response = await publicApi.get("/sesiones/mesa");
    return response.data;
  } catch (error) {
    throw new Error("Error obteniendo mesa");
  }
}

export async function getPedidos(): Promise<Pedido[]> {
  try {
    const response = await publicApi.get<Pedido[]>("/pedidos/");
    return response.data;
  } catch (error) {
    throw new Error("Error obteniendo pedidos");
  }
}

export async function solicitarCuentaApi(sessionCode: string) {
  try {
    const response = await publicApi.put("/mesas/cuenta", {
      sessionCode,
    });

    return response.data;
  } catch (error) {
    throw new Error("Error solicitando cuenta", { cause: error });
  }
}

export async function getCuenta() {
  try {
    const response = await publicApi.get("/pedidos/cuenta");
    return response.data;
  } catch (error) {
    throw new Error("Error obteniendo cuenta");
  }
}
