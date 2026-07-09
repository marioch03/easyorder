import "./styles.css";

type TableButtonProps = {
  number: number;
  state: string;
  zone?: string;
  onClick?: () => void;
};

function TableButton({ number, state, onClick }: TableButtonProps) {
  return (
    <button
      className={`table-button ${state}`}
      onClick={onClick}
    >
      <span>{number}</span>
    </button>
  );
}

export default TableButton;