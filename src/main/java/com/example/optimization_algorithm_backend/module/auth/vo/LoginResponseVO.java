package com.example.optimization_algorithm_backend.module.auth.vo;

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
