package com.example.optimization_algorithm_backend.module.constraint.service;

import com.example.optimization_algorithm_backend.common.response.PageResult;
import com.example.optimization_algorithm_backend.module.constraint.dto.ConstraintQueryRequest;
import com.example.optimization_algorithm_backend.module.constraint.dto.CreateConstraintRequest;
import com.example.optimization_algorithm_backend.module.constraint.dto.UpdateConstraintRequest;
import com.example.optimization_algorithm_backend.module.constraint.vo.ConstraintVO;

public interface ConstraintAppService {

    ConstraintVO createConstraint(Long graphId, CreateConstraintRequest request);

    PageResult<ConstraintVO> listConstraints(Long graphId, ConstraintQueryRequest request);

    ConstraintVO getConstraint(Long graphId, Long constraintId);

    ConstraintVO updateConstraint(Long graphId, Long constraintId, UpdateConstraintRequest request);

    boolean deleteConstraint(Long graphId, Long constraintId);
}
