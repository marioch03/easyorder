import { clientApi } from "../../api/apiClient";
import type { Cuenta, Pedido } from "../types/bill";

export async function getMesa() {
  try {
    const response = await clientApi.get("/sesiones/mesa");
    return response.data;
  } catch (error) {
    throw new Error("Error obteniendo mesa");
  }
}

export async function getPedidos(): Promise<Pedido[]> {
  try {
    const response = await clientApi.get<Pedido[]>("/pedidos");
    return response.data;
  } catch (error) {
    throw new Error("Error obteniendo pedidos");
  }
}

export async function solicitarCuentaApi() {
  try {
    const response = await clientApi.get("/pedidos/cuenta");

    return response.data;
  } catch (error) {
    throw new Error("Error solicitando cuenta", { cause: error });
  }
}

export async function getCuenta(): Promise<Cuenta> {
  try {
    const response = await clientApi.get("/pedidos/cuenta");
    return response.data;
  } catch (error) {
    throw new Error("Error obteniendo cuenta");
  }
}
