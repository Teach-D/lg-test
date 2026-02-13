import apiClient from './client';
import type { ApiResponse } from './types';

export interface Segment {
  id: number;
  label: string;
  rewardPoint: number;
  displayOrder: number;
}

export interface RouletteConfig {
  spinCost: number;
  segments: Segment[];
}

export interface SpinResult {
  rewardPoint: number;
  remainingPoint: number;
}

export interface RouletteStatus {
  hasSpunToday: boolean;
  dailyBudgetRemaining: number;
  spinCost: number;
}

export const rouletteApi = {
  getConfig: () =>
    apiClient.get<ApiResponse<RouletteConfig>>('/roulette/config'),

  getStatus: () =>
    apiClient.get<ApiResponse<RouletteStatus>>('/roulette/status'),

  spin: () =>
    apiClient.post<ApiResponse<SpinResult>>('/roulette/spin'),
};