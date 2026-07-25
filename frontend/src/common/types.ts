export type ProductoTipoDTO = {
  id: number;
  nombre: string;
};

export type SseTopic = "mesas" | "pedidos" | "kds";

export type OrderItemDTO = {
  idProducto: number;
  cantidad: number;
  precioUnitario: number;
  nota: string;
};

export type OrderDTO = {
  items: OrderItemDTO[];
};
