import { Link, useLocation } from "react-router-dom";

type BottomNavButtonProps = {
  icon: string;
  label: string;
  to: string;
};

function BottomNavButton({ icon, label, to }: BottomNavButtonProps) {
  const location = useLocation();
  const isActive = location.pathname === to;

  return (
    <Link to={to} className={`bottom-nav-button ${isActive ? "active" : ""}`}>
      <img src={icon} alt={label} />
      <span>{label}</span>
    </Link>
  );
}

export default BottomNavButton;