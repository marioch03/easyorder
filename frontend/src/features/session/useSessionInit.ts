import { useEffect } from "react";
import { useLocation } from "react-router-dom";
import { useSessionStore } from "./sessionStore";


export function useSessionInit() {
  const { search } = useLocation();
  const initSession = useSessionStore((state) => state.initSession);

  useEffect(() => {
    initSession(search);
  }, [search, initSession]);
}
