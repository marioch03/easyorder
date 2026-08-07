import { customerIcons } from "../customerIcons";

type MenuBottomNavProps = {
  activeSection: string;
  setActiveSection: (section: string) => void;
};

const BUTTONS = [
  { icon: customerIcons.menu, label: "Menu", key: "menu" },
  { icon: customerIcons.cart, label: "Carrito", key: "carrito" },
  { icon: customerIcons.bill, label: "Cuenta", key: "cuenta" },
];

export default function MenuBottomNav({
  activeSection,
  setActiveSection,
}: MenuBottomNavProps) {
  return (
    <nav className="bottom-nav">
      {BUTTONS.map((btn) => (
        <button
          key={btn.key}
          className={`bottom-nav-button ${activeSection === btn.key ? "active" : ""}`}
          onClick={() => setActiveSection(btn.key)}
          type="button"
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
