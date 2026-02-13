import apiClient from './client';
import type { ApiResponse } from './types';

export interface LoginParams {
  email: string;
  password: string;
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
  login: (data: LoginParams) =>
    apiClient.post<ApiResponse<LoginResult>>('/auth/login', data),
};
