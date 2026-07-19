import type { ProductoTipoDTO } from "../../common/types";
import { privateApi } from "../api/apiClient";
import type { PedidoItemKds } from "./kds";

export async function getComandasKds(nombreZonaTrabajo: string): Promise<PedidoItemKds[]> {
    const response = await privateApi.get<PedidoItemKds[]>(`/comandas/kds/${nombreZonaTrabajo}`);
    return response.data;
}

export async function marcarListoItem(idItem: number): Promise<PedidoItemKds> {
    const response = await privateApi.patch<PedidoItemKds>(`/comandas/listo/${idItem}`);
    return response.data;
}

export async function getTiposProductos(nombreZonaTrabajo: string): Promise<ProductoTipoDTO[]> {
    const response = await privateApi.get<ProductoTipoDTO[]>(`/productos/tipos/${nombreZonaTrabajo}`);
    return response.data;
}