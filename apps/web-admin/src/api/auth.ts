import apiClient from './client';
import type { ApiResponse } from './types';

export interface MockLoginParams {
  nickname: string;
}

export interface LoginResult {
  accessToken: string;
  user: {
    id: number;
    email: string;
    nickname: string;
    point: number;
    role: string;
  };
}

export const authApi = {
  mockAdminLogin: (data: MockLoginParams) =>
    apiClient.post<ApiResponse<LoginResult>>('/auth/mock-admin-login', data),
};
