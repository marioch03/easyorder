import { useEffect, useState } from "react";
import { Outlet, useNavigate, useParams } from "react-router-dom";
import { publicApi } from "../api/apiClient";

const TenantLayout = () => {
  const { slug } = useParams<{ slug: string }>();
  const navigate = useNavigate();
  const [isValidating, setIsValidating] = useState(true);

  useEffect(() => {
    if (!slug) return;
    const validateTenant = async () => {
      try {
        setIsValidating(true);
        await publicApi.get(`/tenant/exists/${slug}`);

        localStorage.setItem("tenant_slug", slug);
        setIsValidating(false);
      } catch (error) {
        localStorage.removeItem("tenant_slug");
        navigate("/error", { replace: true });
      }
    };

    validateTenant();
  }, [slug, navigate]);

  if (isValidating) {
    return (
      <div className="flex h-screen items-center justify-center p-4">
        <span>Comprobando restaurante...</span>
      </div>
    );
  }

  return <Outlet />;
};

export default TenantLayout;
