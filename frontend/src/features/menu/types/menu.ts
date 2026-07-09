export type ProductDTO = {
  id: number;
  nombre: string;
  descripcion?: string | null;
  precio: number;
  disponible: boolean;
  imagen?: string | null;
  idTipo: number;
};

export type CategoryDTO = {
  id: number;
  nombre: string;
  description?: string | null;
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
  allergens?: string;
  imageUrl?: string;
};

export type OrderItemDTO = {
  idProducto: number;
  cantidad: number;
  precioUnitario: number;
  nota: string;
};

export type OrderDTO = {
  items: OrderItemDTO[];
};
