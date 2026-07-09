import { useMemo, useState } from "react";
import { useLocation } from "react-router-dom";
import { SessionContext } from "./SessionContext";

export function SessionProvider({ children }: { children: React.ReactNode }) {
  const location = useLocation();

  const [sessionCode] = useState<string | null>(() => {
    const params = new URLSearchParams(location.search);
    const codeFromURL = params.get("sessionCode");
    const codeFromStorage = localStorage.getItem("sessionCode");

    if (codeFromURL) {
      localStorage.setItem("sessionCode", codeFromURL);
      return codeFromURL;
    }
    return codeFromStorage;
  });

  const code = useMemo(() => ({ 
    sessionCode, 
  }), [sessionCode]);

  return (
    <SessionContext.Provider value={code}>
      {children}
    </SessionContext.Provider>
  );
}