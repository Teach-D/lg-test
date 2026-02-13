import { Outlet, Link, useLocation } from 'react-router-dom';
import { useAuthStore } from '@/stores/auth-store';
import { cn } from '@/lib/utils';

const navItems = [
  { path: '/', label: '룰렛' },
  { path: '/points', label: '내 포인트' },
  { path: '/products', label: '상품' },
  { path: '/orders', label: '주문내역' },
];

export default function AppLayout() {
  const location = useLocation();
  const { user, logout } = useAuthStore();

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <header className="bg-white shadow-sm sticky top-0 z-10">
        <div className="max-w-md mx-auto px-4 h-14 flex items-center justify-between">
          <Link to="/" className="text-lg font-bold text-indigo-600">
            Point Roulette
          </Link>
          <div className="flex items-center gap-3 text-sm">
            <span className="text-gray-500">{user?.nickname}</span>
            <button
              onClick={logout}
              className="text-gray-400 hover:text-gray-600"
            >
              로그아웃
            </button>
          </div>
        </div>
      </header>

      <main className="flex-1 max-w-md mx-auto w-full px-4 py-6">
        <Outlet />
      </main>

      <nav className="bg-white border-t sticky bottom-0">
        <div className="max-w-md mx-auto flex">
          {navItems.map((item) => (
            <Link
              key={item.path}
              to={item.path}
              className={cn(
                'flex-1 py-3 text-center text-xs font-medium transition-colors',
                location.pathname === item.path
                  ? 'text-indigo-600 border-t-2 border-indigo-600'
                  : 'text-gray-400 hover:text-gray-600',
              )}
            >
              {item.label}
            </Link>
          ))}
        </div>
      </nav>
    </div>
  );
}
