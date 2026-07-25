import type { OrderDTO } from "../../../common/types";
import { publicApi } from "../../api/apiClient";

export async function getEstadoMesa() {
  try {
    const response = await publicApi.get("/sesiones/mesa");
    return response.data;
  } catch (error) {
    throw new Error("Error obteniendo estado de mesa");
  }
}

export async function crearPedido(order: OrderDTO) {
  try {
    const response = await publicApi.post("/pedidos/create", order);
    return response.data;
  } catch (error) {
    throw new Error("Error al crear el pedido");
  }
}
