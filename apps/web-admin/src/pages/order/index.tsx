import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Table, Tag, Select, Space, message } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { orderApi } from '../../api/order';
import type { OrderItem, OrderStatus } from '../../api/order';

const STATUS_OPTIONS: { value: OrderStatus | ''; label: string }[] = [
  { value: '', label: '전체' },
  { value: 'PENDING', label: '대기' },
  { value: 'CONFIRMED', label: '확인' },
  { value: 'SHIPPED', label: '배송중' },
  { value: 'COMPLETED', label: '완료' },
  { value: 'CANCELLED', label: '취소' },
];

const STATUS_COLORS: Record<string, string> = {
  PENDING: 'orange',
  CONFIRMED: 'blue',
  SHIPPED: 'purple',
  COMPLETED: 'green',
  CANCELLED: 'default',
};

const STATUS_LABELS: Record<string, string> = {
  PENDING: '대기',
  CONFIRMED: '확인',
  SHIPPED: '배송중',
  COMPLETED: '완료',
  CANCELLED: '취소',
};

const TYPE_LABELS: Record<string, string> = {
  EXCHANGE: '상품 교환',
  ROULETTE_WIN: '룰렛 당첨',
};

export default function OrderPage() {
  const queryClient = useQueryClient();
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [statusFilter, setStatusFilter] = useState<OrderStatus | ''>('');

  const { data, isLoading } = useQuery({
    queryKey: ['admin-orders', page, pageSize, statusFilter],
    queryFn: () =>
      orderApi
        .getAll(page, pageSize, statusFilter || undefined)
        .then((r) => r.data.data),
  });

  const statusMutation = useMutation({
    mutationFn: ({ id, status }: { id: number; status: OrderStatus }) =>
      orderApi.updateStatus(id, status),
    onSuccess: () => {
      message.success('주문 상태가 변경되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['admin-orders'] });
    },
  });

  const columns: ColumnsType<OrderItem> = [
    { title: 'ID', dataIndex: 'id', width: 60 },
    { title: '사용자 ID', dataIndex: 'userId', width: 90 },
    { title: '상품명', dataIndex: 'productName' },
    {
      title: '유형',
      dataIndex: 'orderType',
      width: 100,
      render: (v: string) => TYPE_LABELS[v] ?? v,
    },
    {
      title: '포인트',
      dataIndex: 'pointCost',
      width: 100,
      render: (v: number) => `${v.toLocaleString()} P`,
    },
    {
      title: '상태',
      dataIndex: 'status',
      width: 130,
      render: (status: OrderStatus, record) => (
        <Select
          value={status}
          size="small"
          style={{ width: 110 }}
          onChange={(val: OrderStatus) =>
            statusMutation.mutate({ id: record.id, status: val })
          }
        >
          {STATUS_OPTIONS.filter((o) => o.value !== '').map((o) => (
            <Select.Option key={o.value} value={o.value}>
              <Tag color={STATUS_COLORS[o.value]} style={{ margin: 0 }}>
                {o.label}
              </Tag>
            </Select.Option>
          ))}
        </Select>
      ),
    },
    {
      title: '주문일시',
      dataIndex: 'createdAt',
      width: 170,
      render: (v: string) => new Date(v).toLocaleString('ko-KR'),
    },
  ];

  return (
    <div>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2 style={{ margin: 0 }}>주문 내역</h2>
        <Space>
          <span>상태 필터:</span>
          <Select
            value={statusFilter}
            style={{ width: 120 }}
            onChange={(val) => {
              setStatusFilter(val);
              setPage(0);
            }}
          >
            {STATUS_OPTIONS.map((o) => (
              <Select.Option key={o.value} value={o.value}>
                {o.label}
              </Select.Option>
            ))}
          </Select>
        </Space>
      </div>
      <Table
        rowKey="id"
        columns={columns}
        dataSource={data?.content}
        loading={isLoading}
        pagination={{
          current: page + 1,
          pageSize,
          total: data?.totalElements,
          onChange: (p, s) => {
            setPage(p - 1);
            setPageSize(s);
          },
        }}
      />
    </div>
  );
}