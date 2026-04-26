package com.example.optimization_algorithm_backend.module.graph.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.optimization_algorithm_backend.common.exception.BusinessException;
import com.example.optimization_algorithm_backend.common.response.ErrorCode;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.FlowGraphEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.FlowGraphMapper;
import com.example.optimization_algorithm_backend.module.graph.service.GraphVersionService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
public class GraphVersionServiceImpl implements GraphVersionService {

    private static final String DB_UNAVAILABLE_MESSAGE = "数据库未配置或不可用，流程图版本更新暂不可用";

    private final ObjectProvider<FlowGraphMapper> flowGraphMapperProvider;

    public GraphVersionServiceImpl(ObjectProvider<FlowGraphMapper> flowGraphMapperProvider) {
        this.flowGraphMapperProvider = flowGraphMapperProvider;
    }

    @Override
    public void increaseVersion(Long graphId) {
        FlowGraphMapper mapper = getFlowGraphMapper();
        mapper.update(null, new LambdaUpdateWrapper<FlowGraphEntity>()
                .eq(FlowGraphEntity::getId, graphId)
                .setSql("graph_version = graph_version + 1"));
    }

    private FlowGraphMapper getFlowGraphMapper() {
        FlowGraphMapper mapper = flowGraphMapperProvider.getIfAvailable();
        if (mapper == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, DB_UNAVAILABLE_MESSAGE);
        }
        return mapper;
    }
}
