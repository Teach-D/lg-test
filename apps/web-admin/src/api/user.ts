import apiClient from './client';
import type { ApiResponse, PageResponse } from './types';

export interface AdminUserResponse {
  id: number;
  email: string;
  nickname: string;
  point: number;
  role: 'ADMIN' | 'USER';
  createdAt: string;
}

export const userApi = {
  getList: (page: number, size: number, search?: string) =>
    apiClient.get<ApiResponse<PageResponse<AdminUserResponse>>>('/admin/users', {
      params: { page, size, ...(search ? { search } : {}) },
    }),
};
