import apiClient from './client';
import type { ApiResponse, PageResponse } from './types';

export interface AdminSpin {
  id: number;
  userId: number;
  segmentLabel: string;
  rewardPoint: number;
  costPoint: number;
  cancelled: boolean;
  createdAt: string;
}

export const rouletteApi = {
  getSpins: (page: number, size: number) =>
    apiClient.get<ApiResponse<PageResponse<AdminSpin>>>('/admin/roulette/spins', {
      params: { page, size },
    }),

  cancelSpin: (id: number) =>
    apiClient.delete(`/admin/roulette/spins/${id}`),
};
