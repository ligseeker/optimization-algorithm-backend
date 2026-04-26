import React, { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuthStore } from '../store/useAuthStore';
import { getMe } from '../api/auth';
import { Spin } from 'antd';

interface AuthGuardProps {
  children: React.ReactNode;
}

const AuthGuard: React.FC<AuthGuardProps> = ({ children }) => {
  const { token, userInfo, setUserInfo, logout } = useAuthStore();
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    if (!token) {
      navigate(`/login?redirect=${location.pathname}`, { replace: true });
      return;
    }

    if (!userInfo) {
      // 尝试获取用户信息
      getMe()
        .then((res) => {
          setUserInfo(res);
        })
        .catch(() => {
          logout();
          navigate('/login', { replace: true });
        });
    }
  }, [token, userInfo, navigate, location.pathname, setUserInfo, logout]);

  if (!token) {
    return null;
  }

  if (!userInfo) {
    return (
      <div
        style={{ height: '100vh', display: 'flex', justifyContent: 'center', alignItems: 'center' }}
      >
        <Spin size="large" tip="加载用户信息..." />
      </div>
    );
  }

  return <>{children}</>;
};

export default AuthGuard;
