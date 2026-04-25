package com.example.optimization_algorithm_backend.algorithm.model;

public enum Constant {
    CONNECT,  // 衔接关系，表示两个节点必须相邻，合并
    SAME,  // 同一关系，表示两个节点互为替换，合并
    CONTAIN,  // 包含关系，表示一个节点包含另一个节点，待定
    FOLLOW,  // 承接关系，表明一个节点必须在另一个节点之后，不合并
    CALL,  // 调用关系，表示一个节点调用另一个节点，合并
    PARTICIPATE,  // 参与关系，表示一个节点参与另一个节点，合并
    NORMAL // 普通节点(不是一种约束)
}
