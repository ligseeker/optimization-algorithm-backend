package com.example.optimization_algorithm_backend.Service;

import com.example.optimization_algorithm_backend.algorithm.model.Constant;

public interface ConstraintService {
    // 添加约束条件
    void addConstraint(String nodeId1, String nodeId2, Constant type, String example);

    // 删除约束条件
    void deleteConstraint(String nodeId1, String nodeId2, Constant type, String example);

    // 修改约束条件
    void modifyConstraint(String nodeId1, String nodeId2, Constant type, String example);

    // 查找约束条件
    boolean findConstraint(String nodeId1, String nodeId2, String example);

    boolean findNode(String nodeId, String example);

}
