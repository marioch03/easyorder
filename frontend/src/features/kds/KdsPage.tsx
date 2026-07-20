import { useNavigate, useParams } from "react-router-dom";
import Banner from "../../common/Banner";
import KdsBoard from "./KdsBoard";

export default function KdsPage() {
  const { zonaTrabajoSlug } = useParams();
  const zonaTrabajo = zonaTrabajoSlug || "cocina";

  const title = zonaTrabajo.charAt(0).toUpperCase() + zonaTrabajo.slice(1);

  const navigate = useNavigate();
  const handleExit = () => {
    navigate("/kds");
  };

  return (
    <div className="kds-layout">
      <Banner title={`${title}`} onExit={handleExit} />
      <KdsBoard zonaTrabajo={zonaTrabajo.toUpperCase()} />
    </div>
  );
}
