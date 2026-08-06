import type { OrderDTO, ProductoTipoDTO } from "../../common/types";
import { privateApi } from "../api/apiClient";
import type { ProductoComandaDTO } from "./comandas";

export async function getProductosDisponibles(): Promise<ProductoComandaDTO[]> {
  const response = await privateApi.get<ProductoComandaDTO[]>(
    `/productos/simplified`,
  );
  return response.data;
}

export async function getTiposProductoDisponibles(): Promise<
  ProductoTipoDTO[]
> {
  const response = await privateApi.get<ProductoTipoDTO[]>(`/productos/tipos`);
  return response.data;
}

export async function crearPedidoAdmin(
  dto: OrderDTO,
  idMesa: number,
): Promise<void> {
  await privateApi.post<void>(`/pedidos/mesa/${idMesa}`, dto);
}
