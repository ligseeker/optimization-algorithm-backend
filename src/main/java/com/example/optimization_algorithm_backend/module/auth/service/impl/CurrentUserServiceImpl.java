package com.example.optimization_algorithm_backend.module.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.example.optimization_algorithm_backend.common.exception.BusinessException;
import com.example.optimization_algorithm_backend.common.response.ErrorCode;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.SysUserEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.SysUserMapper;
import com.example.optimization_algorithm_backend.module.auth.constant.UserRoleCode;
import com.example.optimization_algorithm_backend.module.auth.service.CurrentUserService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CurrentUserServiceImpl implements CurrentUserService {

    private static final int USER_STATUS_ENABLED = 1;
    private static final String DB_UNAVAILABLE_MESSAGE = "数据库未配置或不可用，认证接口暂不可用";

    private final ObjectProvider<SysUserMapper> sysUserMapperProvider;

    public CurrentUserServiceImpl(ObjectProvider<SysUserMapper> sysUserMapperProvider) {
        this.sysUserMapperProvider = sysUserMapperProvider;
    }

    @Override
    public Long getCurrentUserId() {
        StpUtil.checkLogin();
        return Long.valueOf(StpUtil.getLoginIdAsString());
    }

    @Override
    public SysUserEntity getCurrentUserEntity() {
        SysUserEntity user = getSysUserMapper().selectById(getCurrentUserId());
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户不存在或已被删除");
        }
        if (!Objects.equals(user.getStatus(), USER_STATUS_ENABLED)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "用户已被禁用");
        }
        return user;
    }

    @Override
    public boolean isAdmin() {
        return UserRoleCode.ADMIN.equalsIgnoreCase(getCurrentUserEntity().getRoleCode());
    }

    private SysUserMapper getSysUserMapper() {
        SysUserMapper mapper = sysUserMapperProvider.getIfAvailable();
        if (mapper == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, DB_UNAVAILABLE_MESSAGE);
        }
        return mapper;
    }
}
