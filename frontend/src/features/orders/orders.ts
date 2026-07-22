export type PedidoDTO = {
  idPedido: number;
  idSesion: number;
  numeroMesa: number;
  nombreEstado: string;
  createdAt: string;
  items: PedidoItemDTO[];
};

export type PedidoItemDTO = {
  idProducto: number;
  nombreProducto: string;
  cantidad: number;
  precioUnitario: number;
  nota: string | null;
  listoParaServir: boolean;
};

export const EstadoPedido = {
  PENDIENTE: 'PENDIENTE',
  PARCIAL: 'PARCIAL',
  LISTO: 'LISTO',
} as const;

export type EstadoPedido = typeof EstadoPedido[keyof typeof EstadoPedido];