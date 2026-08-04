import type { ProductoTipoDTO } from "../../../common/types";
import { publicApi } from "../../api/apiClient";
import type { ProductDTO } from "../types/menu";

export async function getProducts(): Promise<ProductDTO[]> {
  try {
    const response = await publicApi.get<ProductDTO[]>("/productos/all");
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
    const response = await publicApi.get<ProductoTipoDTO[]>("/productos/tipos");
    return response.data;
  } catch (error) {
    throw new Error("Error al obtener categorías");
  }
}