package com.example.optimization_algorithm_backend.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.FlowGraphEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FlowGraphMapper extends BaseMapper<FlowGraphEntity> {
}
