package com.example.optimization_algorithm_backend.module.auth.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.optimization_algorithm_backend.common.response.Result;
import com.example.optimization_algorithm_backend.module.auth.dto.LoginRequest;
import com.example.optimization_algorithm_backend.module.auth.service.AuthService;
import com.example.optimization_algorithm_backend.module.auth.vo.CurrentUserVO;
import com.example.optimization_algorithm_backend.module.auth.vo.LoginResponseVO;
import com.example.optimization_algorithm_backend.module.log.annotation.OperationLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "登录认证接口")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "登录")
    @OperationLog(operationType = "LOGIN", objectType = "AUTH")
    public Result<LoginResponseVO> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    @SaCheckLogin
    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public Result<Boolean> logout() {
        return Result.success("退出成功", authService.logout());
    }

    @SaCheckLogin
    @GetMapping("/me")
    @Operation(summary = "获取当前用户")
    public Result<CurrentUserVO> me() {
        return Result.success(authService.getCurrentUser());
    }
}
