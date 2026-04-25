package com.example.optimization_algorithm_backend.module.auth.service;

import com.example.optimization_algorithm_backend.module.auth.dto.LoginRequest;
import com.example.optimization_algorithm_backend.module.auth.vo.CurrentUserVO;
import com.example.optimization_algorithm_backend.module.auth.vo.LoginResponseVO;

public interface AuthService {

    LoginResponseVO login(LoginRequest request);

    boolean logout();

    CurrentUserVO getCurrentUser();
}
