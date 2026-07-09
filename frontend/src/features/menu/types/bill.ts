export type PedidoItem = {
  idProducto: number;
  nombreProducto: string;
  cantidad: number;
  precioUnitario: number;
};

export type Pedido = {
  idPedido: number;
  numeroMesa: number;
  createdAt: string;
  items: PedidoItem[];
};

export type Cuenta = {
  items: PedidoItem[];
  total: number;
};