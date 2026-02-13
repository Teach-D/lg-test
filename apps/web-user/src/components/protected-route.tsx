import { Navigate, Outlet } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { useAuthStore } from '@/stores/auth-store';
import { authApi } from '@/api/auth';

export default function ProtectedRoute() {
  const { token, user, setUser } = useAuthStore();

  useQuery({
    queryKey: ['me'],
    queryFn: async () => {
      const res = await authApi.getMe();
      setUser(res.data.data);
      return res.data.data;
    },
    enabled: token !== null && user === null,
    retry: false,
  });

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  // user 정보 로딩 중
  if (!user) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-gray-400">로딩 중...</div>
      </div>
    );
  }

  return <Outlet />;
}
