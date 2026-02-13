import apiClient from './client';
import type { ApiResponse } from './types';

export interface PointHistory {
  id: number;
  amount: number;
  type: string;
  description: string;
  createdAt: string;
}

export interface PointSummary {
  currentPoint: number;
  histories: PointHistory[];
}

export const pointApi = {
  getMySummary: () =>
    apiClient.get<ApiResponse<PointSummary>>('/points/me'),
};
