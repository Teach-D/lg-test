import { useState, useCallback } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { motion, useAnimation } from 'framer-motion';
import { toast } from 'sonner';
import { rouletteApi } from '@/api/roulette';
import type { SpinResult } from '@/api/roulette';
import { useAuthStore } from '@/stores/auth-store';
import RouletteWheel from './roulette-wheel';

export default function RoulettePage() {
  const queryClient = useQueryClient();
  const user = useAuthStore((s) => s.user);
  const setUser = useAuthStore((s) => s.setUser);
  const controls = useAnimation();
  const [spinning, setSpinning] = useState(false);
  const [rotation, setRotation] = useState(0);
  const [result, setResult] = useState<SpinResult | null>(null);

  const { data: config } = useQuery({
    queryKey: ['roulette-config'],
    queryFn: () => rouletteApi.getConfig().then((r) => r.data.data),
  });

  const { data: status } = useQuery({
    queryKey: ['roulette-status'],
    queryFn: () => rouletteApi.getStatus().then((r) => r.data.data),
  });

  const hasSpunToday = status?.hasSpunToday ?? false;
  const dailyBudgetRemaining = status?.dailyBudgetRemaining ?? -1;

  const spinMutation = useMutation({
    mutationFn: () => rouletteApi.spin(),
    onSuccess: async (res) => {
      const data = res.data.data;

      // 랜덤 위치에 멈추도록 회전 (5바퀴 + 랜덤 각도)
      const totalRotation = 360 * 5 + Math.random() * 360;

      await controls.start({
        rotate: rotation + totalRotation,
        transition: { duration: 4, ease: [0.17, 0.67, 0.12, 0.99] },
      });

      setRotation((prev) => prev + totalRotation);
      setResult(data);
      setSpinning(false);

      // 포인트 상태 업데이트
      if (user) {
        setUser({ ...user, point: data.remainingPoint });
      }
      queryClient.invalidateQueries({ queryKey: ['point-summary'] });
      queryClient.invalidateQueries({ queryKey: ['roulette-status'] });
      queryClient.invalidateQueries({ queryKey: ['me'] });
    },
    onError: () => {
      setSpinning(false);
      toast.error('스핀에 실패했습니다.');
    },
  });

  const handleSpin = useCallback(() => {
    if (spinning || !config || hasSpunToday) return;
    setSpinning(true);
    setResult(null);
    spinMutation.mutate();
  }, [spinning, config, hasSpunToday, spinMutation]);

  const closeResult = () => setResult(null);

  if (!config || config.segments.length === 0) {
    return (
      <div className="text-center py-20 text-gray-400">
        룰렛이 설정되지 않았습니다.
      </div>
    );
  }

  const spinDisabled = spinning || hasSpunToday;

  return (
    <div className="flex flex-col items-center gap-6">
      <div className="flex items-center gap-6">
        <div className="text-center">
          <p className="text-sm text-gray-500">보유 포인트</p>
          <p className="text-2xl font-bold text-indigo-600">
            {(user?.point ?? 0).toLocaleString()} P
          </p>
        </div>
        {dailyBudgetRemaining >= 0 && (
          <div className="text-center">
            <p className="text-sm text-gray-500">오늘 잔여 예산</p>
            <p className="text-2xl font-bold text-amber-600">
              {dailyBudgetRemaining.toLocaleString()} P
            </p>
          </div>
        )}
      </div>

      <motion.div animate={controls}>
        <RouletteWheel segments={config.segments} rotation={0} />
      </motion.div>

      <button
        onClick={handleSpin}
        disabled={spinDisabled}
        className="px-8 py-3 bg-indigo-600 text-white rounded-full text-lg font-bold shadow-lg hover:bg-indigo-700 disabled:opacity-50 transition-all active:scale-95"
      >
        {spinning
          ? '돌아가는 중...'
          : hasSpunToday
            ? '오늘은 이미 참여했어요'
            : '스핀!'}
      </button>

      {/* 결과 모달 */}
      {result && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 px-4">
          <div className="bg-white rounded-2xl p-8 text-center max-w-sm w-full shadow-xl">
            <p className="text-5xl mb-4">🎉</p>
            <h3 className="text-xl font-bold text-gray-800">
              {result.rewardPoint.toLocaleString()}P 당첨!
            </h3>
            <p className="text-sm text-gray-400 mt-1">
              잔여 포인트: {result.remainingPoint.toLocaleString()}P
            </p>
            <button
              onClick={closeResult}
              className="mt-6 px-6 py-2 bg-indigo-600 text-white rounded-lg font-medium hover:bg-indigo-700 transition-colors"
            >
              확인
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
