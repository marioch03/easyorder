import useAuthStore from "../store/authStore";

export const useAuth = () => {
  const user = useAuthStore((state) => state.user);
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
  const isInitializing = useAuthStore((state) => state.isInitializing);

  return {
    user,
    isAuthenticated,
    isInitializing,
  };
};

export default useAuth;
