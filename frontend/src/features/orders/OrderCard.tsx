import "./styles.css";

type OrderCardProps = {
  idPedido: number;
  mesa: number;
  estado: string;
  createdAt: string;
  items: number;
  onClick?: () => void;
  onMarcarServido?: () => void;
  servidoLoading?: boolean;
};

const ESTADO_LABELS: Record<string, string> = {
  PENDIENTE: "Pendiente",
  EN_PREPARACION: "En cocina",
  LISTO: "Listo",
};

const estadoClassName = (estado: string) =>
  "pedido-estado-" + estado.toLowerCase().replaceAll("_", "-");

function formatearTiempo(dateString: string): { texto: string; minutos: number } {
  const ahora = Date.now();
  const creado = new Date(dateString).getTime();
  const minutos = Math.max(0, Math.floor((ahora - creado) / 60000));

  if (minutos < 1) return { texto: "Ahora", minutos };
  if (minutos < 60) return { texto: `${minutos} min`, minutos };

  const horas = Math.floor(minutos / 60);
  const restoMin = minutos % 60;
  return { texto: `${horas}h ${restoMin}min`, minutos };
}

const getClaseUrgencia = (estado: string, minutos: number) => {
  if (estado !== "LISTO") return "";
  if (minutos >= 8) return "urgent";
  if (minutos >= 4) return "warning";
  return "";
};

function OrderCard({
  idPedido,
  mesa,
  estado,
  createdAt,
  items,
  onClick,
  onMarcarServido,
  servidoLoading,
}: OrderCardProps) {
  const { texto: tiempoTexto, minutos } = formatearTiempo(createdAt);
  const esListo = estado === "LISTO";
  const claseUrgencia = getClaseUrgencia(estado, minutos);

  return (
    <div
      className={`pedido-card ${claseUrgencia}`.trim()}
      onClick={onClick}
      role="button"
    >
      <div className="pedido-top">
        <span className="pedido-mesa">Mesa {mesa}</span>
        <span className={`pedido-estado ${estadoClassName(estado)}`}>
          {ESTADO_LABELS[estado] ?? estado}
        </span>
      </div>

      <div className="pedido-meta">
        <span className="pedido-id">#{idPedido}</span>
        <span className="pedido-items-count">
          {items} {items === 1 ? "producto" : "productos"}
        </span>
        <span className={`pedido-tiempo ${claseUrgencia}`.trim()}>{tiempoTexto}</span>
      </div>

      {esListo && onMarcarServido && (
        <button
          type="button"
          className="pedido-quick-serve"
          disabled={servidoLoading}
          onClick={(e) => {
            e.stopPropagation();
            onMarcarServido();
          }}
        >
          {servidoLoading ? <span className="loader" /> : "Marcar servido"}
        </button>
      )}
    </div>
  );
}

export default OrderCard;