import apiClient from './client';
import type { ApiResponse } from './types';

export interface DashboardSummary {
  todayRevenue: number;
  todayOrderCount: number;
  totalUsers: number;
  totalProducts: number;
  orderStatusCounts: Record<string, number>;
}

export const dashboardApi = {
  getSummary: () =>
    apiClient.get<ApiResponse<DashboardSummary>>('/admin/dashboard/summary'),
};