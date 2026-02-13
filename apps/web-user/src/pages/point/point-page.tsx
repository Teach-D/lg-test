import { useQuery } from '@tanstack/react-query';
import { pointApi } from '@/api/point';
import type { PointHistory } from '@/api/point';
import { cn } from '@/lib/utils';

const TYPE_LABELS: Record<string, string> = {
  SIGNUP_BONUS: '가입 보너스',
  ROULETTE_WIN: '룰렛 당첨',
  PRODUCT_EXCHANGE: '상품 교환',
  PRODUCT_REFUND: '상품 환불',
  ADMIN_ADJUST: '관리자 조정',
  EXPIRED: '만료 차감',
};

function isExpired(expiresAt: string | null): boolean {
  if (!expiresAt) return false;
  return new Date(expiresAt) < new Date();
}

function HistoryItem({ history }: { history: PointHistory }) {
  const isPositive = history.amount > 0;
  const expired = isExpired(history.expiresAt);

  return (
    <div className="flex items-center justify-between py-3 border-b border-gray-100 last:border-0">
      <div className="min-w-0 flex-1">
        <div className="flex items-center gap-1.5">
          <p className="text-sm font-medium text-gray-800">
            {TYPE_LABELS[history.type] ?? history.type}
          </p>
          {expired && (
            <span className="shrink-0 text-[10px] font-semibold px-1.5 py-0.5 rounded bg-gray-200 text-gray-500">
              만료됨
            </span>
          )}
        </div>
        <p className="text-xs text-gray-400 truncate">{history.description}</p>
        <div className="flex items-center gap-2 mt-0.5">
          <span className="text-xs text-gray-400">
            {new Date(history.createdAt).toLocaleString('ko-KR')}
          </span>
          {history.expiresAt && !expired && (
            <span className="text-xs text-amber-500">
              ~{new Date(history.expiresAt).toLocaleDateString('ko-KR')} 까지
            </span>
          )}
        </div>
      </div>
      <span
        className={cn(
          'text-sm font-semibold shrink-0 ml-3',
          expired ? 'text-gray-400 line-through' : isPositive ? 'text-green-600' : 'text-red-500',
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

  const expiringPoints = data?.expiringPointIn7Days ?? 0;

  return (
    <div className="space-y-4">
      <div className="bg-indigo-600 rounded-2xl p-6 text-white text-center">
        <p className="text-sm opacity-80">보유 포인트</p>
        <p className="text-3xl font-bold mt-1">
          {(data?.currentPoint ?? 0).toLocaleString()} P
        </p>
      </div>

      {expiringPoints > 0 && (
        <div className="bg-amber-50 border border-amber-200 rounded-xl px-4 py-3 flex items-start gap-2">
          <span className="text-amber-500 text-lg leading-none mt-0.5">⚠</span>
          <p className="text-sm text-amber-700">
            <span className="font-bold">{expiringPoints.toLocaleString()}P</span>가
            7일 이내에 만료됩니다.
          </p>
        </div>
      )}

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
