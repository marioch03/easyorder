import "./styles.css";

type OrderCardProps = {
  idPedido: number;
  mesa: number;
  estado: string;
  createdAt: string;
  items: number;
  onClick?: () => void;
};
const estadoClassName = (estado: string) =>
  "pedido-estado-" + estado.toLowerCase().replaceAll("_", "-");

function calcularTiempo(dateString: string): string {
  const now = new Date();
  const date = new Date(dateString);
  const diffMs = now.getTime() - date.getTime();
  const seconds = Math.floor(diffMs / 1000);
  const minutes = Math.floor(seconds / 60);
  const hours = Math.floor(minutes / 60);

  if (minutes < 1) return "Hace unos segundos";
  if (minutes < 60) return `Hace ${minutes} min`;
  return `Hace ${hours} h`;
}

function OrderCard({ idPedido, mesa, estado, createdAt, items, onClick }: OrderCardProps) {
  return (
    <div className="pedido-card" onClick={onClick} role="button">
        <div className="pedido-top">
            <div className="pedido-numero">Pedido #{idPedido}</div>
            <div className={`pedido-estado ${estadoClassName(estado)}`}>{estado}</div>
        </div>
        <div className="pedido-center">
            <div><span>Mesa: </span>{mesa}</div>
            <div><span>Items: </span>{items}</div>
        </div>
        <div className="pedido-bottom">
            <div className="pedido-tiempo">{calcularTiempo(createdAt)}</div>
        </div>
    </div>
  );
}

export default OrderCard;

