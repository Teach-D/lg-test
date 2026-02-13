import apiClient from './client';
import type { ApiResponse, PageResponse } from './types';

export interface OrderItem {
  id: number;
  productId: number;
  productName: string;
  pointCost: number;
  status: string;
  orderType: string;
  createdAt: string;
}

export const orderApi = {
  create: (productId: number) =>
    apiClient.post<ApiResponse<OrderItem>>('/orders', { productId }),

  getMyOrders: (page: number, size: number, status?: string) =>
    apiClient.get<ApiResponse<PageResponse<OrderItem>>>('/orders/me', {
      params: { page, size, ...(status ? { status } : {}) },
    }),
};