import apiClient from './client';
import type { ApiResponse } from './types';

export type PeriodType = 'DAILY' | 'MONTHLY';

export interface Budget {
  id: number;
  periodType: PeriodType;
  periodDate: string;
  limitAmount: number;
  spentAmount: number;
  remainingAmount: number;
}

export interface BudgetSummary {
  dailyBudget: Budget | null;
  monthlyBudget: Budget | null;
}

export interface SetBudgetParams {
  periodType: PeriodType;
  periodDate: string;
  limitAmount: number;
}

export const budgetApi = {
  getSummary: () =>
    apiClient.get<ApiResponse<BudgetSummary>>('/budgets/summary'),

  set: (data: SetBudgetParams) =>
    apiClient.put<ApiResponse<Budget>>('/budgets', data),
};
