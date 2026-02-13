import apiClient from './client';
import type { ApiResponse, PageResponse } from './types';

export interface Product {
  id: number;
  name: string;
  imageUrl: string | null;
  pointCost: number;
  stock: number;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreateProductParams {
  name: string;
  imageUrl?: string | null;
  pointCost: number;
  stock: number;
  active: boolean;
}

export type UpdateProductParams = CreateProductParams;

export const productApi = {
  getList: (page: number, size: number) =>
    apiClient.get<ApiResponse<PageResponse<Product>>>('/products', {
      params: { page, size },
    }),

  getById: (id: number) =>
    apiClient.get<ApiResponse<Product>>(`/products/${id}`),

  create: (data: CreateProductParams) =>
    apiClient.post<ApiResponse<Product>>('/products', data),

  update: (id: number, data: UpdateProductParams) =>
    apiClient.put<ApiResponse<Product>>(`/products/${id}`, data),

  delete: (id: number) =>
    apiClient.delete(`/products/${id}`),
};
