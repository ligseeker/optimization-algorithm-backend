package com.example.optimization_algorithm_backend.module.constraint.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UpdateConstraintRequest {

    @NotBlank(message = "conditionCode不能为空")
    @Size(max = 64, message = "conditionCode长度不能超过64位")
    private String conditionCode;

    @NotBlank(message = "conditionType不能为空")
    @Size(max = 32, message = "conditionType长度不能超过32位")
    private String conditionType;

    @Size(max = 255, message = "conditionDescription长度不能超过255位")
    private String conditionDescription;

    @NotNull(message = "nodeId1不能为空")
    @Min(value = 1, message = "nodeId1必须大于0")
    private Long nodeId1;

    @NotNull(message = "nodeId2不能为空")
    @Min(value = 1, message = "nodeId2必须大于0")
    private Long nodeId2;

    @Min(value = 0, message = "enabled只能是0或1")
    @Max(value = 1, message = "enabled只能是0或1")
    private Integer enabled;

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
