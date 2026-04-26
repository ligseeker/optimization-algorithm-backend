import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export interface UserInfo {
  userId: number;
  username: string;
  nickname: string;
  roleCode: string;
}

interface AuthState {
  token: string | null;
  userInfo: UserInfo | null;
  setToken: (token: string | null) => void;
  setUserInfo: (userInfo: UserInfo | null) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: localStorage.getItem('satoken'),
      userInfo: null,
      setToken: (token) => {
        if (token) {
          localStorage.setItem('satoken', token);
        } else {
          localStorage.removeItem('satoken');
        }
        set({ token });
      },
      setUserInfo: (userInfo) => set({ userInfo }),
      logout: () => {
        localStorage.removeItem('satoken');
        set({ token: null, userInfo: null });
      },
    }),
    {
      name: 'auth-storage',
      // 只持久化 token，userInfo 建议每次打开页面重新拉取
      partialize: (state) => ({ token: state.token }),
    },
  ),
);
