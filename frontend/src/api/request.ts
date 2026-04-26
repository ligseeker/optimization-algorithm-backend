import axios from 'axios';
import type { AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from 'axios';
import { message } from 'antd';
import type { Result } from '../types/api';
import { ErrorCode } from '../types/api';

// 创建 axios 实例
const request: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('satoken');
    if (token && config.headers) {
      config.headers['satoken'] = token;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse<Result>) => {
    const { code, message: msg, data } = response.data;

    // 业务成功
    if (code === ErrorCode.SUCCESS) {
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      return data as any;
    }

    // 授权失败，跳转登录
    if (code === ErrorCode.UNAUTHORIZED) {
      localStorage.removeItem('satoken');
      localStorage.removeItem('user-storage');
      if (!window.location.pathname.includes('/login')) {
        message.error('登录已过期，请重新登录');
        window.location.href = '/login';
      }
      return Promise.reject(new Error(msg || '未登录'));
    }

    // 其他业务错误
    message.error(msg || '请求失败');
    return Promise.reject(new Error(msg || '未知错误'));
  },
  (error) => {
    const msg = error.response?.data?.message || error.message || '网络异常';
    message.error(msg);
    return Promise.reject(error);
  },
);

export default request;
