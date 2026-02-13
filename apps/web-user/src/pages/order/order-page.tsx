import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { orderApi } from '@/api/order';
import type { OrderItem } from '@/api/order';
import { cn } from '@/lib/utils';

const STATUS_OPTIONS = [
  { value: '', label: '전체' },
  { value: 'PENDING', label: '대기' },
  { value: 'CONFIRMED', label: '확인' },
  { value: 'SHIPPED', label: '배송중' },
  { value: 'COMPLETED', label: '완료' },
  { value: 'CANCELLED', label: '취소' },
];

const STATUS_COLORS: Record<string, string> = {
  PENDING: 'bg-yellow-100 text-yellow-700',
  CONFIRMED: 'bg-blue-100 text-blue-700',
  SHIPPED: 'bg-indigo-100 text-indigo-700',
  COMPLETED: 'bg-green-100 text-green-700',
  CANCELLED: 'bg-gray-100 text-gray-500',
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

function OrderCard({ order }: { order: OrderItem }) {
  return (
    <div className="bg-white rounded-xl p-4 shadow-sm">
      <div className="flex items-start justify-between">
        <div>
          <p className="font-semibold text-gray-800">{order.productName}</p>
          <p className="text-xs text-gray-400 mt-0.5">
            {TYPE_LABELS[order.orderType] ?? order.orderType}
          </p>
        </div>
        <span
          className={cn(
            'text-xs font-medium px-2 py-1 rounded-full',
            STATUS_COLORS[order.status] ?? 'bg-gray-100 text-gray-500',
          )}
        >
          {STATUS_LABELS[order.status] ?? order.status}
        </span>
      </div>
      <div className="flex items-center justify-between mt-3 text-sm">
        <span className="text-indigo-600 font-bold">
          {order.pointCost.toLocaleString()} P
        </span>
        <span className="text-gray-400 text-xs">
          {new Date(order.createdAt).toLocaleString('ko-KR')}
        </span>
      </div>
    </div>
  );
}

export default function OrderPage() {
  const [statusFilter, setStatusFilter] = useState('');

  const { data, isLoading } = useQuery({
    queryKey: ['my-orders', statusFilter],
    queryFn: () =>
      orderApi
        .getMyOrders(0, 20, statusFilter || undefined)
        .then((r) => r.data.data),
  });

  return (
    <div>
      <h2 className="text-lg font-bold text-gray-800 mb-4">주문 내역</h2>

      {/* 상태 필터 */}
      <div className="flex gap-2 overflow-x-auto pb-3 mb-4">
        {STATUS_OPTIONS.map((opt) => (
          <button
            key={opt.value}
            onClick={() => setStatusFilter(opt.value)}
            className={cn(
              'px-3 py-1.5 rounded-full text-xs font-medium whitespace-nowrap transition-colors',
              statusFilter === opt.value
                ? 'bg-indigo-600 text-white'
                : 'bg-gray-100 text-gray-500 hover:bg-gray-200',
            )}
          >
            {opt.label}
          </button>
        ))}
      </div>

      {isLoading ? (
        <div className="text-center py-20 text-gray-400">로딩 중...</div>
      ) : (data?.content?.length ?? 0) === 0 ? (
        <div className="text-center py-20 text-gray-400">
          주문 내역이 없습니다.
        </div>
      ) : (
        <div className="space-y-3">
          {data?.content.map((order) => (
            <OrderCard key={order.id} order={order} />
          ))}
        </div>
      )}
    </div>
  );
}