import request from './request';

export interface LoginParams {
  username: string;
  password: string;
}

export interface LoginResult {
  userId: number;
  username: string;
  nickname: string;
  roleCode: string;
  token: string;
  tokenName: string;
}

export interface UserInfo {
  userId: number;
  username: string;
  nickname: string;
  roleCode: string;
}

/**
 * 登录
 */
export const login = (params: LoginParams): Promise<LoginResult> => {
  return request.post('/api/auth/login', params);
};

/**
 * 退出登录
 */
export const logout = (): Promise<boolean> => {
  return request.post('/api/auth/logout');
};

/**
 * 获取当前用户信息
 */
export const getMe = (): Promise<UserInfo> => {
  return request.get('/api/auth/me');
};
