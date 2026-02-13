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
  segmentId: number;
  segmentLabel: string;
  rewardPoint: number;
  segmentIndex: number;
  remainingPoint: number;
}

export const rouletteApi = {
  getConfig: () =>
    apiClient.get<ApiResponse<RouletteConfig>>('/roulette/config'),

  spin: () =>
    apiClient.post<ApiResponse<SpinResult>>('/roulette/spin'),
};