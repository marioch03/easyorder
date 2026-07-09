import billIcon from "../../../assets/bill.png";
import cartIcon from "../../../assets/cart.png";
import menuIcon from "../../../assets/menu.png";

type MenuBottomNavProps = {
  activeSection: string;
  setActiveSection: (section: string) => void;
};

export default function MenuBottomNav({ activeSection, setActiveSection }: MenuBottomNavProps) {
  const buttons = [
    { icon: menuIcon, label: "Menu", key: "menu" },
    { icon: cartIcon, label: "Carrito", key: "carrito" },
    { icon: billIcon, label: "Cuenta", key: "cuenta" },
  ];

  return (
    <nav className="bottom-nav">
      {buttons.map((btn) => (
        <button
          key={btn.key}
          className={`bottom-nav-button ${activeSection === btn.key ? "active" : ""}`}
          onClick={() => setActiveSection(btn.key)}
        >
          <img src={btn.icon} alt={btn.label} />
          <span>{btn.label}</span>
        </button>
      ))}
    </nav>
  );
}