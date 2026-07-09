import { useState } from "react";
import { useNavigate } from "react-router";
import OrderPage from "../orders/OrderPage";
import TablesPage from "../tables/TablesPage";
import Banner from "./Banner";
import Sidebar from "./Sidebar";
import { logout } from "./managementService";

function ManagementPage() {
  const [activeSection, setActiveSection] = useState("mesas");
  const navigate = useNavigate();

  const renderContent = () => {
    switch (activeSection) {
      case "mesas":
        return <TablesPage />;
      case "pedidos":
        return <OrderPage />;
      default:
        return <TablesPage />;
    }
  };

  const handleLogout = async () => {
    try {
      await logout();
    } catch (error) {
      console.error(error);
    }

    navigate("/auth/login");
  };

  return (
    <div className="app-layout">
      <aside className="sidebar">
        <Sidebar onSelectSection={setActiveSection} onLogout={handleLogout} />
      </aside>

      <main className="main-content">
        <Banner title={activeSection === "mesas" ? "Mesas" : "Pedidos"} />
        {renderContent()}
      </main>
    </div>
  );
}

export default ManagementPage;
