package com.example.optimization_algorithm_backend.module.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.example.optimization_algorithm_backend.common.exception.BusinessException;
import com.example.optimization_algorithm_backend.common.response.ErrorCode;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.SysUserEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.service.SysUserPersistenceService;
import com.example.optimization_algorithm_backend.module.auth.converter.AuthUserConverter;
import com.example.optimization_algorithm_backend.module.auth.dto.LoginRequest;
import com.example.optimization_algorithm_backend.module.auth.service.AuthService;
import com.example.optimization_algorithm_backend.module.auth.service.CurrentUserService;
import com.example.optimization_algorithm_backend.module.auth.util.PasswordUtils;
import com.example.optimization_algorithm_backend.module.auth.vo.CurrentUserVO;
import com.example.optimization_algorithm_backend.module.auth.vo.LoginResponseVO;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@ConditionalOnBean(DataSource.class)
public class AuthServiceImpl implements AuthService {

    private static final int USER_STATUS_ENABLED = 1;

    private final SysUserPersistenceService sysUserPersistenceService;
    private final CurrentUserService currentUserService;

    public AuthServiceImpl(SysUserPersistenceService sysUserPersistenceService, CurrentUserService currentUserService) {
        this.sysUserPersistenceService = sysUserPersistenceService;
        this.currentUserService = currentUserService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponseVO login(LoginRequest request) {
        String username = request.getUsername().trim();
        SysUserEntity user = sysUserPersistenceService.lambdaQuery()
                .eq(SysUserEntity::getUsername, username)
                .last("limit 1")
                .one();
        if (user == null || !StringUtils.hasText(user.getPasswordHash())
                || !PasswordUtils.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户名或密码错误");
        }
        if (!Objects.equals(user.getStatus(), USER_STATUS_ENABLED)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "用户已被禁用");
        }

        StpUtil.login(user.getId());

        SysUserEntity updateEntity = new SysUserEntity();
        updateEntity.setId(user.getId());
        updateEntity.setLastLoginAt(LocalDateTime.now());
        sysUserPersistenceService.updateById(updateEntity);

        return AuthUserConverter.toLoginResponseVO(user, StpUtil.getTokenName(), StpUtil.getTokenValue());
    }

    @Override
    public boolean logout() {
        StpUtil.checkLogin();
        StpUtil.logout();
        return true;
    }

    @Override
    public CurrentUserVO getCurrentUser() {
        return AuthUserConverter.toCurrentUserVO(currentUserService.getCurrentUserEntity());
    }
}
