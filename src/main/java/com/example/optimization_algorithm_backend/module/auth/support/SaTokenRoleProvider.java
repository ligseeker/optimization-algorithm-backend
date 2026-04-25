package com.example.optimization_algorithm_backend.module.auth.support;

import cn.dev33.satoken.stp.StpInterface;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.SysUserEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.service.SysUserPersistenceService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

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
