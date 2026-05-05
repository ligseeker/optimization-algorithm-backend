package com.example.optimization_algorithm_backend.module.auth.vo;
// 作用：登录响应对象，包含了当前用户的信息以及登录成功后返回的Token信息。这个类继承自CurrentUserVO，包含了用户的基本信息，同时新增了token和tokenName字段，用于存储登录成功后生成的Token值和Token名称，方便前端在后续请求中使用Token进行认证和授权。
public class LoginResponseVO extends CurrentUserVO {

    private String token;
    private String tokenName;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }
}
