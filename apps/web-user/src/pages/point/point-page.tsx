import { useQuery } from '@tanstack/react-query';
import { pointApi } from '@/api/point';
import type { PointHistory } from '@/api/point';
import { cn } from '@/lib/utils';

const TYPE_LABELS: Record<string, string> = {
  SIGNUP_BONUS: '가입 보너스',
  ROULETTE_WIN: '룰렛 당첨',
  PRODUCT_EXCHANGE: '상품 교환',
  ADMIN_ADJUST: '관리자 조정',
};

function HistoryItem({ history }: { history: PointHistory }) {
  const isPositive = history.amount > 0;
  return (
    <div className="flex items-center justify-between py-3 border-b border-gray-100 last:border-0">
      <div>
        <p className="text-sm font-medium text-gray-800">
          {TYPE_LABELS[history.type] ?? history.type}
        </p>
        <p className="text-xs text-gray-400">{history.description}</p>
        <p className="text-xs text-gray-400 mt-0.5">
          {new Date(history.createdAt).toLocaleString('ko-KR')}
        </p>
      </div>
      <span
        className={cn(
          'text-sm font-semibold',
          isPositive ? 'text-green-600' : 'text-red-500',
        )}
      >
        {isPositive ? '+' : ''}{history.amount.toLocaleString()} P
      </span>
    </div>
  );
}

export default function PointPage() {
  const { data, isLoading } = useQuery({
    queryKey: ['point-summary'],
    queryFn: () => pointApi.getMySummary().then((r) => r.data.data),
  });

  if (isLoading) {
    return <div className="text-center py-20 text-gray-400">로딩 중...</div>;
  }

  return (
    <div className="space-y-6">
      <div className="bg-indigo-600 rounded-2xl p-6 text-white text-center">
        <p className="text-sm opacity-80">보유 포인트</p>
        <p className="text-3xl font-bold mt-1">
          {(data?.currentPoint ?? 0).toLocaleString()} P
        </p>
      </div>

      <div className="bg-white rounded-xl p-4 shadow-sm">
        <h3 className="text-sm font-semibold text-gray-600 mb-2">최근 이력</h3>
        {data?.histories.length === 0 ? (
          <p className="text-center text-gray-400 text-sm py-6">
            포인트 이력이 없습니다.
          </p>
        ) : (
          data?.histories.map((h) => <HistoryItem key={h.id} history={h} />)
        )}
      </div>
    </div>
  );
}