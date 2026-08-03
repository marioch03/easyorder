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

export type CategoryDTO = {
  id: number;
  nombre: string;
};

export type ProductosPorCategorias = {
  id: number;
  nombre: string;
  products: ProductDTO[];
};

export type Product = {
  id: number;
  name: string;
  price: number;
  description?: string;
  allergens: Alergeno[];
  imageUrl?: string;
};

export type Alergeno = {
  nombre: string;
  tipo: "CONTIENE" | "PUEDE_CONTENER_TRAZAS";
};
