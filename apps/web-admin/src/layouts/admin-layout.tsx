import { useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { Button, Layout, Menu } from 'antd';
import {
  DashboardOutlined,
  LogoutOutlined,
  OrderedListOutlined,
  ShoppingOutlined,
  UserOutlined,
  WalletOutlined,
} from '@ant-design/icons';

const { Header, Sider, Content } = Layout;

const menuItems = [
  { key: '/', icon: <DashboardOutlined />, label: '대시보드' },
  { key: '/products', icon: <ShoppingOutlined />, label: '상품 관리' },
  { key: '/budgets', icon: <WalletOutlined />, label: '예산 관리' },
  { key: '/orders', icon: <OrderedListOutlined />, label: '주문 내역' },
  { key: '/users', icon: <UserOutlined />, label: '사용자 관리' },
];

export default function AdminLayout() {
  const [collapsed, setCollapsed] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    localStorage.removeItem('accessToken');
    navigate('/login');
  };

  const selectedKey = menuItems
    .filter((item) => item.key !== '/')
    .find((item) => location.pathname.startsWith(item.key))?.key
    ?? '/';

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider
        collapsible
        collapsed={collapsed}
        onCollapse={setCollapsed}
      >
        <div
          style={{
            height: 32,
            margin: 16,
            color: '#fff',
            fontWeight: 700,
            fontSize: collapsed ? 14 : 16,
            textAlign: 'center',
            lineHeight: '32px',
            whiteSpace: 'nowrap',
            overflow: 'hidden',
          }}
        >
          {collapsed ? 'PR' : 'Point Roulette'}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[selectedKey]}
          items={menuItems}
          onClick={({ key }) => navigate(key)}
        />
      </Sider>
      <Layout>
        <Header
          style={{
            padding: '0 24px',
            background: '#fff',
            fontSize: 18,
            fontWeight: 600,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
          }}
        >
          <span>관리자 페이지</span>
          <Button
            type="text"
            icon={<LogoutOutlined />}
            onClick={handleLogout}
          >
            로그아웃
          </Button>
        </Header>
        <Content style={{ margin: 24 }}>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
}
