package com.example.optimization_algorithm_backend.module.auth.vo;
// 作用：当前用户信息对象，包含了用户的基本信息，如用户ID、用户名、昵称和角色编码。这个类用于在系统中传递当前登录用户的信息，方便在不同的业务逻辑中获取和使用当前用户的数据。通过这个对象，系统可以实现基于用户信息的权限控制和个性化功能。
// VO（Value Object）是一种设计模式，用于封装数据并提供访问这些数据的方法。CurrentUserVO作为一个VO类，主要用于在系统中传递当前用户的信息，避免直接暴露数据库实体对象，提高系统的安全性和灵活性。
public class CurrentUserVO {

    private Long userId;
    private String username;
    private String nickname;
    private String roleCode;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }
}
