import "./styles.css";

type TableButtonProps = {
  number: number;
  state: string;
  zone?: string;
  onClick?: () => void;
};

const STATE_LABELS: Record<string, string> = {
  green: "Libre",
  red: "Ocupada",
  yellow: "Cuenta",
  gray: "Sin datos",
};

function TableButton({ number, state, onClick }: TableButtonProps) {
  return (
    <button className={`table-button ${state}`} onClick={onClick} type="button">
      <span className="table-number">{number}</span>
      <span className="table-status-label">{STATE_LABELS[state] ?? state}</span>
    </button>
  );
}

export default TableButton;