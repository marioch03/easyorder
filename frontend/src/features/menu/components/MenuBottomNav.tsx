// MenuBottomNav.tsx
import billIcon from "../../../assets/bill.png";
import cartIcon from "../../../assets/cart.png";
import menuIcon from "../../../assets/menu.png";

type MenuBottomNavProps = {
  activeSection: string;
  setActiveSection: (section: string) => void;
};

const BUTTONS = [
  { icon: menuIcon, label: "Menu", key: "menu" },
  { icon: cartIcon, label: "Carrito", key: "carrito" },
  { icon: billIcon, label: "Cuenta", key: "cuenta" },
];

export default function MenuBottomNav({ activeSection, setActiveSection }: MenuBottomNavProps) {
  return (
    <nav className="bottom-nav">
      {BUTTONS.map((btn) => (
        <button
          key={btn.key}
          className={`bottom-nav-button ${activeSection === btn.key ? "active" : ""}`}
          onClick={() => setActiveSection(btn.key)}
        >
          <span className="nav-icon-badge">
            <img src={btn.icon} alt="" aria-hidden="true" />
          </span>
          <span>{btn.label}</span>
        </button>
      ))}
    </nav>
  );
}