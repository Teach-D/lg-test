import { useRef, useEffect } from 'react';
import type { Segment } from '@/api/roulette';

const COLORS = [
  '#6366f1', '#f59e0b', '#10b981', '#ef4444',
  '#8b5cf6', '#f97316', '#14b8a6', '#ec4899',
];

interface Props {
  segments: Segment[];
  rotation: number;
}

export default function RouletteWheel({ segments, rotation }: Props) {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const count = segments.length;
  const arc = (2 * Math.PI) / count;

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas || count === 0) return;

    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const size = canvas.width;
    const center = size / 2;
    const radius = center - 4;

    ctx.clearRect(0, 0, size, size);
    ctx.save();
    ctx.translate(center, center);
    // rotation 기준: 12시 방향이 pointer → -90도 오프셋
    ctx.rotate((rotation * Math.PI) / 180 - Math.PI / 2);

    segments.forEach((seg, i) => {
      const startAngle = i * arc;
      const endAngle = startAngle + arc;

      // 세그먼트 그리기
      ctx.beginPath();
      ctx.moveTo(0, 0);
      ctx.arc(0, 0, radius, startAngle, endAngle);
      ctx.closePath();
      ctx.fillStyle = COLORS[i % COLORS.length];
      ctx.fill();
      ctx.strokeStyle = '#fff';
      ctx.lineWidth = 2;
      ctx.stroke();

      // 라벨
      ctx.save();
      ctx.rotate(startAngle + arc / 2);
      ctx.textAlign = 'center';
      ctx.fillStyle = '#fff';
      ctx.font = 'bold 14px sans-serif';
      ctx.fillText(seg.label, radius * 0.6, 5);
      ctx.restore();
    });

    ctx.restore();
  }, [segments, rotation, count, arc]);

  return (
    <div className="relative inline-block">
      {/* 포인터 (12시 방향) */}
      <div className="absolute top-0 left-1/2 -translate-x-1/2 -translate-y-2 z-10 text-2xl">
        ▼
      </div>
      <canvas
        ref={canvasRef}
        width={300}
        height={300}
        className="rounded-full shadow-lg"
      />
    </div>
  );
}