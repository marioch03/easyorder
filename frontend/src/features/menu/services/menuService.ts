import type { ProductoTipoDTO } from "../../../common/types";
import { clientApi } from "../../api/apiClient";
import type { ProductDTO } from "../types/menu";

export async function getProducts(): Promise<ProductDTO[]> {
  try {
    const response = await clientApi.get<ProductDTO[]>("/productos");
    return response.data.map((p) => ({
      ...p,
      disponible: Boolean(p.disponible),
    }));
  } catch (error) {
    throw new Error("Error al obtener productos");
  }
}

export async function getCategories(): Promise<ProductoTipoDTO[]> {
  try {
    const response = await clientApi.get<ProductoTipoDTO[]>("/productos/tipos");
    return response.data;
  } catch (error) {
    throw new Error("Error al obtener categorías");
  }
}
