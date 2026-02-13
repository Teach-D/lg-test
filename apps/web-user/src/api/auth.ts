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

export interface RegisterParams {
  email: string;
  password: string;
  nickname: string;
}

export interface LoginParams {
  email: string;
  password: string;
}

export const authApi = {
  register: (data: RegisterParams) =>
    apiClient.post<ApiResponse<LoginResponse>>('/auth/register', data),

  login: (data: LoginParams) =>
    apiClient.post<ApiResponse<LoginResponse>>('/auth/login', data),

  getMe: () =>
    apiClient.get<ApiResponse<UserInfo>>('/users/me'),
};
