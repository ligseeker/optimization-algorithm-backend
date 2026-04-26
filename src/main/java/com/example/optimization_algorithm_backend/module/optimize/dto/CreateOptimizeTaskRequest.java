package com.example.optimization_algorithm_backend.module.optimize.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class CreateOptimizeTaskRequest {

    @Schema(description = "流程图ID", example = "1001")
    @NotNull(message = "graphId不能为空")
    @Min(value = 1, message = "graphId必须大于0")
    private Long graphId;

    @Schema(description = "算法类型（1/2/3）", example = "1")
    @NotNull(message = "algorithmType不能为空")
    @Min(value = 1, message = "algorithmType必须在1-3之间")
    @Max(value = 3, message = "algorithmType必须在1-3之间")
    private Integer algorithmType;

    @Schema(description = "算法模式（0/1/2）", example = "2")
    @NotNull(message = "algorithmMode不能为空")
    @Min(value = 0, message = "algorithmMode必须在0-2之间")
    @Max(value = 2, message = "algorithmMode必须在0-2之间")
    private Integer algorithmMode;

    @Schema(description = "时间权重", example = "1")
    @Min(value = 1, message = "timeWeight必须大于等于1")
    @Max(value = 100, message = "timeWeight不能超过100")
    private Integer timeWeight;

    @Schema(description = "精度权重", example = "1")
    @Min(value = 1, message = "precisionWeight必须大于等于1")
    @Max(value = 100, message = "precisionWeight不能超过100")
    private Integer precisionWeight;

    @Schema(description = "成本权重", example = "1")
    @Min(value = 1, message = "costWeight必须大于等于1")
    @Max(value = 100, message = "costWeight不能超过100")
    private Integer costWeight;

    public Long getGraphId() {
        return graphId;
    }

    public void setGraphId(Long graphId) {
        this.graphId = graphId;
    }

    public Integer getAlgorithmType() {
        return algorithmType;
    }

    public void setAlgorithmType(Integer algorithmType) {
        this.algorithmType = algorithmType;
    }

    public Integer getAlgorithmMode() {
        return algorithmMode;
    }

    public void setAlgorithmMode(Integer algorithmMode) {
        this.algorithmMode = algorithmMode;
    }

    public Integer getTimeWeight() {
        return timeWeight == null ? 1 : timeWeight;
    }

    public void setTimeWeight(Integer timeWeight) {
        this.timeWeight = timeWeight;
    }

    public Integer getPrecisionWeight() {
        return precisionWeight == null ? 1 : precisionWeight;
    }

    public void setPrecisionWeight(Integer precisionWeight) {
        this.precisionWeight = precisionWeight;
    }

    public Integer getCostWeight() {
        return costWeight == null ? 1 : costWeight;
    }

    public void setCostWeight(Integer costWeight) {
        this.costWeight = costWeight;
    }
}
