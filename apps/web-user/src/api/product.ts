import apiClient from './client';
import type { ApiResponse, PageResponse } from './types';

export interface Product {
  id: number;
  name: string;
  imageUrl: string | null;
  pointCost: number;
  stock: number;
  active: boolean;
}

export const productApi = {
  getList: (page: number, size: number) =>
    apiClient.get<ApiResponse<PageResponse<Product>>>('/products', {
      params: { page, size },
    }),
};