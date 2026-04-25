package com.example.optimization_algorithm_backend.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;

@TableName("process_node")
public class ProcessNodeEntity extends BaseEntity {

    @TableField("graph_id")
    private Long graphId;

    @TableField("node_code")
    private String nodeCode;

    @TableField("node_name")
    private String nodeName;

    @TableField("node_description")
    private String nodeDescription;

    @TableField("equipment_id")
    private Long equipmentId;

    @TableField("time_cost")
    private Integer timeCost;

    @TableField("precision_value")
    private BigDecimal precisionValue;

    @TableField("cost_value")
    private Integer costValue;

    @TableField("sort_no")
    private Integer sortNo;

    public Long getGraphId() {
        return graphId;
    }

    public void setGraphId(Long graphId) {
        this.graphId = graphId;
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeDescription() {
        return nodeDescription;
    }

    public void setNodeDescription(String nodeDescription) {
        this.nodeDescription = nodeDescription;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Integer getTimeCost() {
        return timeCost;
    }

    public void setTimeCost(Integer timeCost) {
        this.timeCost = timeCost;
    }

    public BigDecimal getPrecisionValue() {
        return precisionValue;
    }

    public void setPrecisionValue(BigDecimal precisionValue) {
        this.precisionValue = precisionValue;
    }

    public Integer getCostValue() {
        return costValue;
    }

    public void setCostValue(Integer costValue) {
        this.costValue = costValue;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }
}
