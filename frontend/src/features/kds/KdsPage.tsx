import { useParams } from "react-router-dom";
import Banner from "../../common/Banner";
import KdsBoard from "./KdsBoard";

export default function KdsPage() {
const { zonaTrabajoSlug } = useParams(); 
    const zonaTrabajo = zonaTrabajoSlug || "cocina";

  const title = zonaTrabajo.charAt(0).toUpperCase() + zonaTrabajo.slice(1);

  return (
    <div className="kds-layout">
      <Banner title={`${title}`} />
      <KdsBoard zonaTrabajo={zonaTrabajo.toUpperCase()} />
    </div>
  );
}