import { createBrowserRouter } from 'react-router-dom';
import AppLayout from '@/layouts/app-layout';
import ProtectedRoute from '@/components/protected-route';
import LoginPage from '@/pages/auth/login-page';
import RoulettePage from '@/pages/roulette/roulette-page';
import PointPage from '@/pages/point/point-page';
import ProductPage from '@/pages/product/product-page';
import OrderPage from '@/pages/order/order-page';

const router = createBrowserRouter([
  { path: '/login', element: <LoginPage /> },
  {
    element: <ProtectedRoute />,
    children: [
      {
        element: <AppLayout />,
        children: [
          { path: '/', element: <RoulettePage /> },
          { path: '/points', element: <PointPage /> },
          { path: '/products', element: <ProductPage /> },
          { path: '/orders', element: <OrderPage /> },
        ],
      },
    ],
  },
]);

export default router;
