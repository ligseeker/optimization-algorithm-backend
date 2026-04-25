package com.example.optimization_algorithm_backend.module.auth.converter;

import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.SysUserEntity;
import com.example.optimization_algorithm_backend.module.auth.vo.CurrentUserVO;
import com.example.optimization_algorithm_backend.module.auth.vo.LoginResponseVO;

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
