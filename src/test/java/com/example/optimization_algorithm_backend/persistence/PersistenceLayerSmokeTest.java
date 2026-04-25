package com.example.optimization_algorithm_backend.persistence;

import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.ConstraintConditionEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.EquipmentEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.FlowGraphEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.OperationLogEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.OptimizeResultEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.OptimizeTaskEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.ProcessNodeEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.ProcessPathEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.SysUserEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.WorkspaceEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.FlowGraphMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.SysUserMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.service.FlowGraphPersistenceService;
import com.example.optimization_algorithm_backend.infrastructure.persistence.service.SysUserPersistenceService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PersistenceLayerSmokeTest {

    @Test
    void shouldLoadPersistenceLayerTypes() {
        assertNotNull(SysUserEntity.class);
        assertNotNull(WorkspaceEntity.class);
        assertNotNull(FlowGraphEntity.class);
        assertNotNull(EquipmentEntity.class);
        assertNotNull(ProcessNodeEntity.class);
        assertNotNull(ProcessPathEntity.class);
        assertNotNull(ConstraintConditionEntity.class);
        assertNotNull(OptimizeTaskEntity.class);
        assertNotNull(OptimizeResultEntity.class);
        assertNotNull(OperationLogEntity.class);

        assertNotNull(SysUserMapper.class);
        assertNotNull(FlowGraphMapper.class);
        assertNotNull(SysUserPersistenceService.class);
        assertNotNull(FlowGraphPersistenceService.class);
    }
}
