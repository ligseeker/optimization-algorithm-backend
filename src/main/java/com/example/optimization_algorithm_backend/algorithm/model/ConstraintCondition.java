package com.example.optimization_algorithm_backend.algorithm.model;

// ConstraintCondition 类, 用于存储约束条件信息
public class ConstraintCondition {
    String conditionID;           // 约束条件 ID
    String conditionDescription;  // 约束条件描述
    Constant conditionType;         // 约束条件类型
    String nodeID1;                // 节点 ID1
    String nodeID2;                // 节点 ID2

    public ConstraintCondition(String conditionID, String conditionDescription, Constant conditionType, String nodeID1, String nodeID2) {
        this.conditionID = conditionID;
        this.conditionDescription = conditionDescription;
        this.conditionType = conditionType;
        this.nodeID1 = nodeID1;
        this.nodeID2 = nodeID2;
    }

    public String getConditionID() {
        return conditionID;
    }

    public String getConditionDescription() {
        return conditionDescription;
    }

    public String toString() {
        return "ConstraintCondition{" +
                "conditionID='" + conditionID + '\'' +
                ", conditionDescription='" + conditionDescription + '\'' +
                ", conditionType=" + conditionType +
                ", nodeID1='" + nodeID1 + '\'' +
                ", nodeID2='" + nodeID2 + '\'' +
                '}';
    }
    public String getNodeID1() {
        return nodeID1;
    }

    public String getNodeID2() {
        return nodeID2;
    }
    public Constant getConditionType() {
        return conditionType;
    }

    public void setConditionType(Constant conditionType) {
        this.conditionType = conditionType;
    }

    public void setConditionID(String conditionID) {
        this.conditionID = conditionID;
    }

    public void setNodeID2(String nodeID2) {
        this.nodeID2 = nodeID2;
    }

    public void setNodeID1(String nodeID1) {
        this.nodeID1 = nodeID1;
    }
}
