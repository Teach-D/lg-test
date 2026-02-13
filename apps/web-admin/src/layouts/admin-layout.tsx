import { useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { Layout, Menu } from 'antd';
import {
  DashboardOutlined,
  ShoppingOutlined,
  WalletOutlined,
  OrderedListOutlined,
} from '@ant-design/icons';

const { Header, Sider, Content } = Layout;

const menuItems = [
  { key: '/', icon: <DashboardOutlined />, label: '대시보드' },
  { key: '/products', icon: <ShoppingOutlined />, label: '상품 관리' },
  { key: '/budgets', icon: <WalletOutlined />, label: '예산 관리' },
  { key: '/orders', icon: <OrderedListOutlined />, label: '주문 내역' },
];

export default function AdminLayout() {
  const [collapsed, setCollapsed] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

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
          }}
        >
          관리자 페이지
        </Header>
        <Content style={{ margin: 24 }}>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
}
