import type { OrderDTO } from "../../../common/types";
import { clientApi } from "../../api/apiClient";

export async function getEstadoMesa() {
  try {
    const response = await clientApi.get("/sesiones/mesa");
    return response.data;
  } catch (error) {
    throw new Error("Error obteniendo estado de mesa");
  }
}

export async function crearPedido(order: OrderDTO) {
  try {
    const response = await clientApi.post("/pedidos", order);
    return response.data;
  } catch (error) {
    throw new Error("Error al crear el pedido");
  }
}
