package com.example.optimization_algorithm_backend.module.path.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UpdatePathRequest {

    @NotNull(message = "startNodeId不能为空")
    @Min(value = 1, message = "startNodeId必须大于0")
    private Long startNodeId;

    @NotNull(message = "endNodeId不能为空")
    @Min(value = 1, message = "endNodeId必须大于0")
    private Long endNodeId;

    @Size(max = 32, message = "relationType长度不能超过32位")
    private String relationType;

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
