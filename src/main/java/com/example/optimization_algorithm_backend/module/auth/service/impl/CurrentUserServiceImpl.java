package com.example.optimization_algorithm_backend.module.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.example.optimization_algorithm_backend.common.exception.BusinessException;
import com.example.optimization_algorithm_backend.common.response.ErrorCode;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.SysUserEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.service.SysUserPersistenceService;
import com.example.optimization_algorithm_backend.module.auth.constant.UserRoleCode;
import com.example.optimization_algorithm_backend.module.auth.service.CurrentUserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Objects;

@Service
@ConditionalOnBean(DataSource.class)
public class CurrentUserServiceImpl implements CurrentUserService {

    private static final int USER_STATUS_ENABLED = 1;

    private final SysUserPersistenceService sysUserPersistenceService;

    public CurrentUserServiceImpl(SysUserPersistenceService sysUserPersistenceService) {
        this.sysUserPersistenceService = sysUserPersistenceService;
    }

    @Override
    public Long getCurrentUserId() {
        StpUtil.checkLogin();
        return Long.valueOf(StpUtil.getLoginIdAsString());
    }

    @Override
    public SysUserEntity getCurrentUserEntity() {
        SysUserEntity user = sysUserPersistenceService.getById(getCurrentUserId());
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
}
