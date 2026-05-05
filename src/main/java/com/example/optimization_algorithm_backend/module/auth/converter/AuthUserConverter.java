package com.example.optimization_algorithm_backend.module.auth.converter;

import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.SysUserEntity;
import com.example.optimization_algorithm_backend.module.auth.vo.CurrentUserVO;
import com.example.optimization_algorithm_backend.module.auth.vo.LoginResponseVO;
// 作用: 该类是一个工具类，提供了将SysUserEntity对象转换为CurrentUserVO和LoginResponseVO对象的方法。通过这些转换方法，可以将数据库中的用户实体转换为适合在认证模块中使用的视图对象，方便在登录响应和当前用户信息展示中使用，提高代码的可读性和维护性。
public final class AuthUserConverter {

    private AuthUserConverter() {
    }

    public static CurrentUserVO toCurrentUserVO(SysUserEntity entity) {
        CurrentUserVO vo = new CurrentUserVO();
        vo.setUserId(entity.getId());
        vo.setUsername(entity.getUsername());
        vo.setNickname(entity.getNickname());
        vo.setRoleCode(entity.getRoleCode());
        return vo;
    }
    
    public static LoginResponseVO toLoginResponseVO(SysUserEntity entity, String tokenName, String tokenValue) {
        LoginResponseVO vo = new LoginResponseVO();
        vo.setUserId(entity.getId());
        vo.setUsername(entity.getUsername());
        vo.setNickname(entity.getNickname());
        vo.setRoleCode(entity.getRoleCode());
        vo.setTokenName(tokenName);
        vo.setToken(tokenValue);
        return vo;
    }
}
