import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import { productApi } from '@/api/product';
import { orderApi } from '@/api/order';
import type { Product } from '@/api/product';
import { useAuthStore } from '@/stores/auth-store';
import { cn } from '@/lib/utils';

function ProductCard({ product, onExchange, isPending }: {
  product: Product;
  onExchange: () => void;
  isPending: boolean;
}) {
  const user = useAuthStore((s) => s.user);
  const canAfford = (user?.point ?? 0) >= product.pointCost;
  const inStock = product.stock > 0;

  return (
    <div className="bg-white rounded-xl shadow-sm overflow-hidden">
      <div className="h-40 bg-gray-100 flex items-center justify-center">
        {product.imageUrl ? (
          <img
            src={product.imageUrl}
            alt={product.name}
            className="h-full w-full object-cover"
          />
        ) : (
          <span className="text-4xl">🎁</span>
        )}
      </div>
      <div className="p-4">
        <h3 className="font-semibold text-gray-800 truncate">{product.name}</h3>
        <p className="text-indigo-600 font-bold mt-1">
          {product.pointCost.toLocaleString()} P
        </p>
        <p className="text-xs text-gray-400 mt-0.5">재고 {product.stock}개</p>
        <button
          onClick={onExchange}
          disabled={!canAfford || !inStock || isPending}
          className={cn(
            'w-full mt-3 py-2 rounded-lg text-sm font-medium transition-colors',
            canAfford && inStock
              ? 'bg-indigo-600 text-white hover:bg-indigo-700'
              : 'bg-gray-200 text-gray-400 cursor-not-allowed',
          )}
        >
          {!inStock ? '품절' : !canAfford ? '포인트 부족' : '교환하기'}
        </button>
      </div>
    </div>
  );
}

export default function ProductPage() {
  const queryClient = useQueryClient();

  const { data, isLoading } = useQuery({
    queryKey: ['products'],
    queryFn: () => productApi.getList(0, 20).then((r) => r.data.data),
  });

  const exchangeMutation = useMutation({
    mutationFn: (productId: number) => orderApi.create(productId),
    onSuccess: () => {
      toast.success('상품을 교환했습니다!');
      queryClient.invalidateQueries({ queryKey: ['products'] });
      queryClient.invalidateQueries({ queryKey: ['point-summary'] });
      queryClient.invalidateQueries({ queryKey: ['me'] });
    },
    onError: () => {
      toast.error('교환에 실패했습니다. 포인트나 재고를 확인해주세요.');
    },
  });

  if (isLoading) {
    return <div className="text-center py-20 text-gray-400">로딩 중...</div>;
  }

  const products = data?.content?.filter((p) => p.active) ?? [];

  return (
    <div>
      <h2 className="text-lg font-bold text-gray-800 mb-4">상품 목록</h2>
      {products.length === 0 ? (
        <p className="text-center text-gray-400 py-20">등록된 상품이 없습니다.</p>
      ) : (
        <div className="grid grid-cols-2 gap-3">
          {products.map((product) => (
            <ProductCard
              key={product.id}
              product={product}
              onExchange={() => exchangeMutation.mutate(product.id)}
              isPending={exchangeMutation.isPending}
            />
          ))}
        </div>
      )}
    </div>
  );
}