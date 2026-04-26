package com.example.optimization_algorithm_backend.module.path.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UpdatePathRequest {

    @Schema(description = "起点节点ID", example = "3001")
    @NotNull(message = "startNodeId不能为空")
    @Min(value = 1, message = "startNodeId必须大于0")
    private Long startNodeId;

    @Schema(description = "终点节点ID", example = "3003")
    @NotNull(message = "endNodeId不能为空")
    @Min(value = 1, message = "endNodeId必须大于0")
    private Long endNodeId;

    @Schema(description = "关系类型", example = "NORMAL")
    @Size(max = 32, message = "relationType长度不能超过32位")
    private String relationType;

    @Schema(description = "备注", example = "调整后的链路")
    @Size(max = 255, message = "remark长度不能超过255位")
    private String remark;

    public Long getStartNodeId() {
        return startNodeId;
    }

    public void setStartNodeId(Long startNodeId) {
        this.startNodeId = startNodeId;
    }

    public Long getEndNodeId() {
        return endNodeId;
    }

    public void setEndNodeId(Long endNodeId) {
        this.endNodeId = endNodeId;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
