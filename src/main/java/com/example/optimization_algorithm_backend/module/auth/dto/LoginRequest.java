package com.example.optimization_algorithm_backend.module.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class LoginRequest {

    @Schema(description = "登录用户名", example = "admin")
    @NotBlank(message = "用户名不能为空")
    @Size(max = 64, message = "用户名长度不能超过64位")
    private String username;

    @Schema(description = "登录密码", example = "admin123")
    @NotBlank(message = "密码不能为空")
    @Size(max = 128, message = "密码长度不能超过128位")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
