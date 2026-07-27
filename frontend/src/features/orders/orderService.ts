import { privateApi } from "../api/apiClient";
import type { PedidoDTO } from "./orders";

const PATH = "/pedidos";

export async function getPedidosAdmin(): Promise<PedidoDTO[]> {
  const response = await privateApi.get<PedidoDTO[]>(`${PATH}/list`);
  return response.data;
}

export async function marcarPedidoServido(
  idPedido: number,
): Promise<PedidoDTO> {
  const response = await privateApi.patch<PedidoDTO>(
    `${PATH}/${idPedido}/servido`,
  );

  return response.data;
}

export async function marcarServidoItem(idItem: number): Promise<void> {
  await privateApi.patch(`/comandas/servido/${idItem}`);
}
