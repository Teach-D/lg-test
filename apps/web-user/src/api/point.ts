import apiClient from './client';
import type { ApiResponse } from './types';

export interface PointHistory {
  id: number;
  amount: number;
  type: string;
  description: string;
  expiresAt: string | null;
  createdAt: string;
}

export interface PointSummary {
  currentPoint: number;
  expiringPointIn7Days: number;
  histories: PointHistory[];
}

export const pointApi = {
  getMySummary: () =>
    apiClient.get<ApiResponse<PointSummary>>('/points/me'),
};
