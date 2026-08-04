export type ProductDTO = {
  id: number;
  nombre: string;
  descripcion?: string | null;
  precio: number;
  disponible: boolean;
  imagen?: string | null;
  tipoId: number;
  alergenos: Alergeno[];
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
