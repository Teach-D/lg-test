import apiClient from './client';
import type { ApiResponse } from './types';

export interface UserInfo {
  id: number;
  email: string;
  nickname: string;
  point: number;
}

export interface LoginResponse {
  accessToken: string;
  user: UserInfo;
}

export interface MockLoginParams {
  nickname: string;
}

export const authApi = {
  mockLogin: (data: MockLoginParams) =>
    apiClient.post<ApiResponse<LoginResponse>>('/auth/mock-login', data),

  getMe: () =>
    apiClient.get<ApiResponse<UserInfo>>('/users/me'),
};
