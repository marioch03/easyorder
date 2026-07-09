import { publicApi } from "../../api/apiClient";
import type { CategoryDTO, ProductDTO } from "../types/menu";

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

export async function getCategories(): Promise<CategoryDTO[]> {
  try {
    const response = await publicApi.get<CategoryDTO[]>("/productos/tipos");
    return response.data;
  } catch (error) {
    throw new Error("Error al obtener categorías");
  }
}