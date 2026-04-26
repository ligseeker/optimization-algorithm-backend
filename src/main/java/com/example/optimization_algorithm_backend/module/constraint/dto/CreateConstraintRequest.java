package com.example.optimization_algorithm_backend.module.constraint.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateConstraintRequest {

    @Schema(description = "约束编码，同图唯一", example = "CND001")
    @NotBlank(message = "conditionCode不能为空")
    @Size(max = 64, message = "conditionCode长度不能超过64位")
    private String conditionCode;

    @Schema(description = "约束类型", example = "FOLLOW")
    @NotBlank(message = "conditionType不能为空")
    @Size(max = 32, message = "conditionType长度不能超过32位")
    private String conditionType;

    @Schema(description = "约束描述", example = "A必须先于B")
    @Size(max = 255, message = "conditionDescription长度不能超过255位")
    private String conditionDescription;

    @Schema(description = "节点1 ID", example = "3001")
    @NotNull(message = "nodeId1不能为空")
    @Min(value = 1, message = "nodeId1必须大于0")
    private Long nodeId1;

    @Schema(description = "节点2 ID", example = "3002")
    @NotNull(message = "nodeId2不能为空")
    @Min(value = 1, message = "nodeId2必须大于0")
    private Long nodeId2;

    @Schema(description = "是否启用：1启用，0禁用", example = "1")
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
