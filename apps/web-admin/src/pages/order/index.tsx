import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Table, Tag, Select, Space, message, Button, Popconfirm } from 'antd';
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
    onError: () => {
      message.error('상태 변경에 실패했습니다.');
    },
  });

  const handleStatusChange = (id: number, newStatus: OrderStatus) => {
    statusMutation.mutate({ id, status: newStatus });
  };

  const canCancel = (status: OrderStatus) =>
    status === 'PENDING' || status === 'CONFIRMED';

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
      render: (status: OrderStatus, record) => {
        if (status === 'CANCELLED' || status === 'COMPLETED') {
          return (
            <Tag color={STATUS_COLORS[status]}>
              {STATUS_LABELS[status]}
            </Tag>
          );
        }
        return (
          <Select
            value={status}
            size="small"
            style={{ width: 110 }}
            onChange={(val: OrderStatus) => {
              if (val !== 'CANCELLED') {
                handleStatusChange(record.id, val);
              }
            }}
          >
            {STATUS_OPTIONS.filter(
              (o) => o.value !== '' && o.value !== 'CANCELLED',
            ).map((o) => (
              <Select.Option key={o.value} value={o.value}>
                <Tag color={STATUS_COLORS[o.value]} style={{ margin: 0 }}>
                  {o.label}
                </Tag>
              </Select.Option>
            ))}
          </Select>
        );
      },
    },
    {
      title: '주문일시',
      dataIndex: 'createdAt',
      width: 170,
      render: (v: string) => new Date(v).toLocaleString('ko-KR'),
    },
    {
      title: '관리',
      width: 100,
      render: (_, record) => {
        if (record.status === 'CANCELLED') {
          return <span style={{ color: '#999' }}>취소됨</span>;
        }
        if (!canCancel(record.status)) {
          return null;
        }
        return (
          <Popconfirm
            title="주문 취소"
            description={`${record.pointCost.toLocaleString()}P가 환불됩니다. 취소하시겠습니까?`}
            onConfirm={() => handleStatusChange(record.id, 'CANCELLED')}
            okText="취소 실행"
            cancelText="닫기"
            okButtonProps={{ danger: true }}
          >
            <Button type="link" danger size="small">
              취소
            </Button>
          </Popconfirm>
        );
      },
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
