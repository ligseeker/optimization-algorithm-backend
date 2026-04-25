package com.example.optimization_algorithm_backend.module.auth.service;

import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.SysUserEntity;

public interface CurrentUserService {

    Long getCurrentUserId();

    SysUserEntity getCurrentUserEntity();

    boolean isAdmin();
}
