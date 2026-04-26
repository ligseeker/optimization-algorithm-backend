package com.example.optimization_algorithm_backend.module.optimize.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class CreateOptimizeTaskRequest {

    @NotNull(message = "graphId不能为空")
    @Min(value = 1, message = "graphId必须大于0")
    private Long graphId;

    @NotNull(message = "algorithmType不能为空")
    @Min(value = 1, message = "algorithmType必须在1-3之间")
    @Max(value = 3, message = "algorithmType必须在1-3之间")
    private Integer algorithmType;

    @NotNull(message = "algorithmMode不能为空")
    @Min(value = 0, message = "algorithmMode必须在0-2之间")
    @Max(value = 2, message = "algorithmMode必须在0-2之间")
    private Integer algorithmMode;

    @Min(value = 1, message = "timeWeight必须大于等于1")
    @Max(value = 100, message = "timeWeight不能超过100")
    private Integer timeWeight;

    @Min(value = 1, message = "precisionWeight必须大于等于1")
    @Max(value = 100, message = "precisionWeight不能超过100")
    private Integer precisionWeight;

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
