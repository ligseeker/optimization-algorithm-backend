package com.example.optimization_algorithm_backend.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("constraint_condition")
public class ConstraintConditionEntity extends BaseEntity {

    @TableField("graph_id")
    private Long graphId;

    @TableField("condition_code")
    private String conditionCode;

    @TableField("condition_type")
    private String conditionType;

    @TableField("condition_description")
    private String conditionDescription;

    @TableField("node_id_1")
    private Long nodeId1;

    @TableField("node_id_2")
    private Long nodeId2;

    @TableField("enabled")
    private Integer enabled;

    public Long getGraphId() {
        return graphId;
    }

    public void setGraphId(Long graphId) {
        this.graphId = graphId;
    }

    public String getConditionCode() {
        return conditionCode;
    }

    public void setConditionCode(String conditionCode) {
        this.conditionCode = conditionCode;
    }

    public String getConditionType() {
        return conditionType;
    }

    public void setConditionType(String conditionType) {
        this.conditionType = conditionType;
    }

    public String getConditionDescription() {
        return conditionDescription;
    }

    public void setConditionDescription(String conditionDescription) {
        this.conditionDescription = conditionDescription;
    }

    public Long getNodeId1() {
        return nodeId1;
    }

    public void setNodeId1(Long nodeId1) {
        this.nodeId1 = nodeId1;
    }

    public Long getNodeId2() {
        return nodeId2;
    }

    public void setNodeId2(Long nodeId2) {
        this.nodeId2 = nodeId2;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }
}
