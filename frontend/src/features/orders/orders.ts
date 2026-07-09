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