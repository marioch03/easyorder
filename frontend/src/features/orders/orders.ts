export type PedidoItemModificadorDTO = {
  id: number;
  nombre: string;
  precioAplicado: number;
};

export type PedidoItemDTO = {
  id: number;
  idProducto: number;
  nombreProducto: string;
  cantidad: number;
  precioUnitario: number;
  nota: string | null;
  listoParaServir: boolean;
  servido: boolean;
  modificadores?: PedidoItemModificadorDTO[];
};

export type PedidoDTO = {
  idPedido: number;
  idSesion: number;
  numeroMesa: number;
  nombreEstado: string;
  createdAt: string;
  items: PedidoItemDTO[];
};

export const EstadoPedido = {
  PENDIENTE: "PENDIENTE",
  PARCIAL: "PARCIAL",
  LISTO: "LISTO",
} as const;
