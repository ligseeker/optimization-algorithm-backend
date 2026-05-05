package com.example.optimization_algorithm_backend.module.auth.support;

import cn.dev33.satoken.stp.StpInterface;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.SysUserEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.service.SysUserPersistenceService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
// 作用: 该类是一个Sa-Token的权限接口实现类，用于提供用户的权限信息和角色信息。通过实现StpInterface接口，可以让Sa-Token框架在进行权限验证时调用这个类的方法来获取用户的权限列表和角色列表。
// @Component注解将这个类注册为Spring Bean，@ConditionalOnBean(DataSource.class)注解确保只有在配置了DataSource（即数据库连接）时才会创建这个Bean，避免在没有数据库的环境中出现错误。通过查询数据库中的用户信息，可以动态地获取用户的角色信息，提升系统的灵活性和安全性。
@Component
@ConditionalOnBean(DataSource.class)
public class SaTokenRoleProvider implements StpInterface {

    private final SysUserPersistenceService sysUserPersistenceService;

    public SaTokenRoleProvider(SysUserPersistenceService sysUserPersistenceService) {
        this.sysUserPersistenceService = sysUserPersistenceService;
    }

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return Collections.emptyList();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        if (loginId == null) {
            return Collections.emptyList();
        }
        SysUserEntity user = sysUserPersistenceService.getById(Long.valueOf(String.valueOf(loginId)));
        if (user == null || user.getRoleCode() == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(user.getRoleCode());
    }
}
