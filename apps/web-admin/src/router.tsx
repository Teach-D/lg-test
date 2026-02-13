import { createBrowserRouter, Navigate, Outlet } from 'react-router-dom';
import AdminLayout from './layouts/admin-layout';
import DashboardPage from './pages/dashboard';
import ProductPage from './pages/product';
import BudgetPage from './pages/budget';
import OrderPage from './pages/order';
import UserPage from './pages/user';
import LoginPage from './pages/login';

function AuthGuard() {
  const token = localStorage.getItem('accessToken');
  if (!token) {
    return <Navigate to="/login" replace />;
  }
  return <Outlet />;
}

const router = createBrowserRouter([
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    element: <AuthGuard />,
    children: [
      {
        path: '/',
        element: <AdminLayout />,
        children: [
          { index: true, element: <DashboardPage /> },
          { path: 'products', element: <ProductPage /> },
          { path: 'budgets', element: <BudgetPage /> },
          { path: 'orders', element: <OrderPage /> },
          { path: 'users', element: <UserPage /> },
        ],
      },
    ],
  },
]);

export default router;
