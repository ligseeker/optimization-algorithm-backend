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
    // 作用: 定义一个常量，表示用户状态为启用。通过这个常量，可以在代码中清晰地表达用户状态的含义，避免使用魔法数字，提高代码的可读性和维护性。
    private static final int USER_STATUS_ENABLED = 1;
    private static final String DB_UNAVAILABLE_MESSAGE = "数据库未配置或不可用，认证接口暂不可用";

    private final ObjectProvider<SysUserMapper> sysUserMapperProvider;
    private final CurrentUserService currentUserService;
    // 作用: 构造函数注入SysUserMapper的ObjectProvider和CurrentUserService。通过构造函数注入，可以确保在创建AuthServiceImpl实例时，所需的依赖项已经准备就绪，并且可以通过ObjectProvider灵活地获取SysUserMapper实例，提升系统的可测试性和灵活性。
    public AuthServiceImpl(ObjectProvider<SysUserMapper> sysUserMapperProvider, CurrentUserService currentUserService) {
        this.sysUserMapperProvider = sysUserMapperProvider;
        this.currentUserService = currentUserService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    // 作用：声明式事务管理，确保在登录过程中如果发生任何异常，数据库的状态能够回滚到之前的状态，避免数据不一致的问题。rollbackFor = Exception.class表示当抛出任何异常时都进行回滚，提升系统的可靠性和数据的完整性。
    public LoginResponseVO login(LoginRequest request) {
        // 作用: 获取SysUserMapper实例，如果未配置或不可用，则抛出异常。通过ObjectProvider的getIfAvailable方法，可以安全地获取SysUserMapper实例，避免在没有配置数据库时导致系统崩溃，并提供了明确的错误信息，提升系统的健壮性。
        SysUserMapper sysUserMapper = getSysUserMapper();
        // 作用: 获取登录请求中的用户名，并去除首尾的空格。通过调用trim方法，可以确保用户名在进行数据库查询时不会因为多余的空格而导致匹配失败，提高用户体验和系统的容错性。
        String username = request.getUsername().trim();
        // 作用: 构建一个查询条件，查询数据库中是否存在与提供的用户名匹配的用户记录，并限制只返回一条记录。通过LambdaQueryWrapper，可以方便地构建查询条件，使用eq方法指定查询字段和对应的值，last方法添加SQL片段限制结果数量，提高查询效率和系统性能。
        LambdaQueryWrapper<SysUserEntity> queryWrapper = new LambdaQueryWrapper<SysUserEntity>()
                .eq(SysUserEntity::getUsername, username) // :: 是方法引用，表示获取SysUserEntity类的getUsername方法作为查询条件的字段，username是查询条件的值。
                .last("limit 1");// last方法用于在生成的SQL语句末尾添加自定义的SQL片段，这里添加了"limit 1"，表示限制查询结果只返回一条记录。这可以提升查询效率，特别是在用户名字段上有索引的情况下，可以快速定位到匹配的用户记录，避免扫描整个表，提高系统性能。
        // 作用: 执行查询操作，根据构建的查询条件从数据库中获取用户记录。通过调用selectOne方法，可以获取符合条件的单条记录，如果没有找到或找到多条记录，则会返回null或抛出异常，确保系统能够正确处理用户登录请求。limit 1的作用是限制查询结果只返回一条记录，提升查询效率。
        SysUserEntity user = sysUserMapper.selectOne(queryWrapper);
        // 作用: 验证用户是否存在、密码是否匹配以及用户状态是否为启用。
        // hasText方法用于检查字符串是否包含非空白字符，确保密码哈希值有效。PasswordUtils.matches方法用于比较用户输入的密码与数据库中存储的密码哈希值是否匹配，确保用户提供的密码正确。
        if (user == null || !StringUtils.hasText(user.getPasswordHash())
                || !PasswordUtils.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户名或密码错误");
        }
        if (!Objects.equals(user.getStatus(), USER_STATUS_ENABLED)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "用户已被禁用");
        }
        // 作用: 使用Sa-Token框架进行用户登录，生成并管理用户的会话状态。通过调用StpUtil.login方法，并传入用户ID作为参数，可以创建一个新的会话，并生成相应的Token。这个Token将用于后续的认证和授权操作，确保用户在登录后能够访问受保护的资源，同时也提供了安全性和便利性。
        StpUtil.login(user.getId());
        // 作用: 更新用户的最后登录时间，以便记录用户的登录活动。通过创建一个新的SysUserEntity对象，并设置用户ID和当前时间作为最后登录时间，然后调用sysUserMapper.updateById方法更新数据库中的记录，可以保持用户信息的最新状态，提供更好的用户体验和系统监控能力。
        SysUserEntity updateEntity = new SysUserEntity();
        updateEntity.setId(user.getId());
        updateEntity.setLastLoginAt(LocalDateTime.now());
        // 作用: 更新数据库中用户的最后登录时间。其他字段未设置，因此不会被更新。通过调用updateById方法，并传入包含用户ID和最后登录时间的updateEntity对象，可以确保只有指定的字段被更新，避免不必要的数据修改，提高系统的效率和数据的完整性。
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
