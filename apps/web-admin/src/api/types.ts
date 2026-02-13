/** 공통 API 응답 형식 */
export interface ApiResponse<T> {
  success: boolean;
  data: T;
  error?: {
    code: string;
    message: string;
  };
}

/** 페이지네이션 요청 파라미터 */
export interface PageParams {
  page: number;
  size: number;
}

/** 페이지네이션 응답 */
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}
