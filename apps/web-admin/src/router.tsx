import { createBrowserRouter } from 'react-router-dom';
import AdminLayout from './layouts/admin-layout';
import DashboardPage from './pages/dashboard';
import ProductPage from './pages/product';
import BudgetPage from './pages/budget';
import OrderPage from './pages/order';

const router = createBrowserRouter([
  {
    path: '/',
    element: <AdminLayout />,
    children: [
      { index: true, element: <DashboardPage /> },
      { path: 'products', element: <ProductPage /> },
      { path: 'budgets', element: <BudgetPage /> },
      { path: 'orders', element: <OrderPage /> },
    ],
  },
]);

export default router;
