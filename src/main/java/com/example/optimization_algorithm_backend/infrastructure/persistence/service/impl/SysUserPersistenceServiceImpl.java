package com.example.optimization_algorithm_backend.infrastructure.persistence.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.SysUserEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.SysUserMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.service.SysUserPersistenceService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
@ConditionalOnBean(DataSource.class)
public class SysUserPersistenceServiceImpl extends ServiceImpl<SysUserMapper, SysUserEntity> implements SysUserPersistenceService {
}
