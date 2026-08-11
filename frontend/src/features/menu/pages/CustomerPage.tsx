import { useState } from "react";
import MenuBanner from "../components/MenuBanner";
import MenuBottomNav from "../components/MenuBottomNav";
import "../styles.css";
import BillPage from "./BillPage";
import CartPage from "./CartPage";
import MenuPage from "./MenuPage";

function CustomerPage() {
  const [activeSection, setActiveSection] = useState("menu");

  const renderContent = () => {
    switch (activeSection) {
      case "carrito":
        return <CartPage onGoToMenu={() => setActiveSection("menu")} />;
      case "cuenta":
        return <BillPage />;
      default:
        return <MenuPage />;
    }
  };

  return (
    <div className="menu-layout">
      <MenuBanner />
      <main className="customer-content">
        {}
        {renderContent()}
      </main>
      <MenuBottomNav
        activeSection={activeSection}
        setActiveSection={setActiveSection}
      />
    </div>
  );
}

export default CustomerPage;
