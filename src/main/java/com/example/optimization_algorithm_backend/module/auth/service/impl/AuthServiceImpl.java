package com.example.optimization_algorithm_backend.module.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.optimization_algorithm_backend.common.exception.BusinessException;
import com.example.optimization_algorithm_backend.common.response.ErrorCode;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.SysUserEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.SysUserMapper;
import com.example.optimization_algorithm_backend.module.auth.converter.AuthUserConverter;
import com.example.optimization_algorithm_backend.module.auth.dto.LoginRequest;
import com.example.optimization_algorithm_backend.module.auth.service.AuthService;
import com.example.optimization_algorithm_backend.module.auth.service.CurrentUserService;
import com.example.optimization_algorithm_backend.module.auth.util.PasswordUtils;
import com.example.optimization_algorithm_backend.module.auth.vo.CurrentUserVO;
import com.example.optimization_algorithm_backend.module.auth.vo.LoginResponseVO;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class AuthServiceImpl implements AuthService {

    private static final int USER_STATUS_ENABLED = 1;
    private static final String DB_UNAVAILABLE_MESSAGE = "数据库未配置或不可用，认证接口暂不可用";

    private final ObjectProvider<SysUserMapper> sysUserMapperProvider;
    private final CurrentUserService currentUserService;

    public AuthServiceImpl(ObjectProvider<SysUserMapper> sysUserMapperProvider, CurrentUserService currentUserService) {
        this.sysUserMapperProvider = sysUserMapperProvider;
        this.currentUserService = currentUserService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponseVO login(LoginRequest request) {
        SysUserMapper sysUserMapper = getSysUserMapper();
        String username = request.getUsername().trim();
        LambdaQueryWrapper<SysUserEntity> queryWrapper = new LambdaQueryWrapper<SysUserEntity>()
                .eq(SysUserEntity::getUsername, username)
                .last("limit 1");
        SysUserEntity user = sysUserMapper.selectOne(queryWrapper);
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
        sysUserMapper.updateById(updateEntity);

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

    private SysUserMapper getSysUserMapper() {
        SysUserMapper mapper = sysUserMapperProvider.getIfAvailable();
        if (mapper == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, DB_UNAVAILABLE_MESSAGE);
        }
        return mapper;
    }
}
