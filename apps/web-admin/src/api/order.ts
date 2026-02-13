import apiClient from './client';
import type { ApiResponse, PageResponse } from './types';

export type OrderStatus =
  | 'PENDING'
  | 'CONFIRMED'
  | 'SHIPPED'
  | 'COMPLETED'
  | 'CANCELLED';

export interface OrderItem {
  id: number;
  userId: number;
  productId: number;
  productName: string;
  pointCost: number;
  status: OrderStatus;
  orderType: string;
  createdAt: string;
}

export const orderApi = {
  getAll: (page: number, size: number, status?: OrderStatus) =>
    apiClient.get<ApiResponse<PageResponse<OrderItem>>>('/admin/orders', {
      params: { page, size, ...(status ? { status } : {}) },
    }),

  updateStatus: (id: number, status: OrderStatus) =>
    apiClient.patch<ApiResponse<OrderItem>>(`/admin/orders/${id}/status`, {
      status,
    }),
};