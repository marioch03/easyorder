export type Modificador = {
  id: number;
  nombre: string;
  precioAplicado: number;
};

export type PedidoItem = {
  id: number;
  idProducto: number;
  nombreProducto: string;
  cantidad: number;
  precioUnitario: number;
  nota: string;
  listoParaServir: boolean;
  servido: boolean;
  modificadores: Modificador[];
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
