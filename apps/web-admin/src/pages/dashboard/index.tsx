import { useQuery } from '@tanstack/react-query';
import { Card, Col, Row, Statistic, Progress } from 'antd';
import {
  ShoppingOutlined,
  UserOutlined,
  OrderedListOutlined,
  DollarOutlined,
  ThunderboltOutlined,
  GiftOutlined,
} from '@ant-design/icons';
import {
  PieChart,
  Pie,
  Cell,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts';
import { dashboardApi } from '../../api/dashboard';

const STATUS_LABELS: Record<string, string> = {
  PENDING: '대기',
  CONFIRMED: '확인',
  SHIPPED: '배송중',
  COMPLETED: '완료',
  CANCELLED: '취소',
};

const PIE_COLORS = ['#faad14', '#1677ff', '#722ed1', '#52c41a', '#bfbfbf'];

export default function DashboardPage() {
  const { data, isLoading } = useQuery({
    queryKey: ['dashboard-summary'],
    queryFn: () => dashboardApi.getSummary().then((r) => r.data.data),
  });

  const pieData = data
    ? Object.entries(data.orderStatusCounts).map(([key, value]) => ({
        name: STATUS_LABELS[key] ?? key,
        value,
      }))
    : [];

  const budgetPercent =
    data && data.todayBudgetLimit > 0
      ? Math.round((data.todayBudgetSpent / data.todayBudgetLimit) * 100)
      : 0;

  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>대시보드</h2>

      {/* 오늘 예산 현황 */}
      <Card
        title="오늘 예산 현황"
        loading={isLoading}
        style={{ marginBottom: 24 }}
      >
        {data && data.todayBudgetLimit > 0 ? (
          <>
            <Row gutter={16}>
              <Col span={8}>
                <Statistic
                  title="예산 한도"
                  value={data.todayBudgetLimit}
                  suffix="P"
                />
              </Col>
              <Col span={8}>
                <Statistic
                  title="소진액"
                  value={data.todayBudgetSpent}
                  suffix="P"
                />
              </Col>
              <Col span={8}>
                <Statistic
                  title="잔여"
                  value={data.todayBudgetRemaining}
                  suffix="P"
                  valueStyle={{
                    color:
                      data.todayBudgetRemaining > 0 ? '#3f8600' : '#cf1322',
                  }}
                />
              </Col>
            </Row>
            <Progress percent={budgetPercent} style={{ marginTop: 16 }} />
          </>
        ) : (
          <div style={{ textAlign: 'center', padding: 20, color: '#999' }}>
            오늘의 예산이 설정되지 않았습니다.
          </div>
        )}
      </Card>

      {/* 오늘 룰렛 현황 + 기타 통계 */}
      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={6}>
          <Card loading={isLoading}>
            <Statistic
              title="오늘 참여자"
              value={data?.todaySpinCount ?? 0}
              suffix="명"
              prefix={<ThunderboltOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card loading={isLoading}>
            <Statistic
              title="오늘 지급 포인트"
              value={data?.todayPointsDistributed ?? 0}
              suffix="P"
              prefix={<GiftOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card loading={isLoading}>
            <Statistic
              title="오늘 매출"
              value={data?.todayRevenue ?? 0}
              suffix="P"
              prefix={<DollarOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card loading={isLoading}>
            <Statistic
              title="오늘 주문"
              value={data?.todayOrderCount ?? 0}
              suffix="건"
              prefix={<OrderedListOutlined />}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={12}>
          <Card loading={isLoading}>
            <Statistic
              title="전체 사용자"
              value={data?.totalUsers ?? 0}
              suffix="명"
              prefix={<UserOutlined />}
            />
          </Card>
        </Col>
        <Col span={12}>
          <Card loading={isLoading}>
            <Statistic
              title="등록 상품"
              value={data?.totalProducts ?? 0}
              suffix="개"
              prefix={<ShoppingOutlined />}
            />
          </Card>
        </Col>
      </Row>

      <Card title="주문 상태별 현황" loading={isLoading}>
        {pieData.length === 0 ? (
          <div style={{ textAlign: 'center', padding: 40, color: '#999' }}>
            주문 데이터가 없습니다.
          </div>
        ) : (
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={pieData}
                dataKey="value"
                nameKey="name"
                cx="50%"
                cy="50%"
                outerRadius={100}
                label={({ name, value }) => `${name}: ${value}`}
              >
                {pieData.map((_, index) => (
                  <Cell
                    key={`cell-${index}`}
                    fill={PIE_COLORS[index % PIE_COLORS.length]}
                  />
                ))}
              </Pie>
              <Tooltip />
              <Legend />
            </PieChart>
          </ResponsiveContainer>
        )}
      </Card>
    </div>
  );
}
