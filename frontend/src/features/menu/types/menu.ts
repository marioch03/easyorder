import type { ModificadorDTO } from "../../../common/types";

export type ProductDTO = {
  id: number;
  nombre: string;
  descripcion?: string | null;
  precio: number;
  disponible: boolean;
  imagen?: string | null;
  tipoId: number;
  alergenos: Alergeno[];
  gruposModificadores?: GrupoModificadorDTO[];
};

export type ProductosPorCategorias = {
  id: number;
  nombre: string;
  products: ProductDTO[];
};

export type Alergeno = {
  nombre: string;
  descripcion?: string;
  tipo: "CONTIENE" | "PUEDE_CONTENER_TRAZAS";
};

export type GrupoModificadorDTO = {
  id: number;
  nombre: string;
  seleccionMinima: number;
  seleccionMaxima: number;
  modificadores: ModificadorDTO[];
};
