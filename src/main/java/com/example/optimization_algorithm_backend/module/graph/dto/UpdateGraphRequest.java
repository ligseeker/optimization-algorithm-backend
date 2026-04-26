package com.example.optimization_algorithm_backend.module.graph.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public class UpdateGraphRequest {

    @Schema(description = "流程图名称", example = "装配流程图A-更新")
    @NotBlank(message = "流程图名称不能为空")
    @Size(max = 128, message = "流程图名称长度不能超过128位")
    private String name;

    @Schema(description = "流程图描述", example = "更新后的描述")
    @Size(max = 500, message = "流程图描述长度不能超过500位")
    private String description;

    @Schema(description = "流程图状态", example = "READY")
    @Size(max = 32, message = "graphStatus长度不能超过32位")
    private String graphStatus;

    @Schema(description = "统计总时间", example = "120")
    @Min(value = 0, message = "totalTime不能小于0")
    private Integer totalTime;

    @Schema(description = "统计总精度", example = "0.8600")
    @DecimalMin(value = "0", message = "totalPrecision不能小于0")
    private BigDecimal totalPrecision;

    @Schema(description = "统计总成本", example = "320")
    @Min(value = 0, message = "totalCost不能小于0")
    private Integer totalCost;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGraphStatus() {
        return graphStatus;
    }

    public void setGraphStatus(String graphStatus) {
        this.graphStatus = graphStatus;
    }

    public Integer getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Integer totalTime) {
        this.totalTime = totalTime;
    }

    public BigDecimal getTotalPrecision() {
        return totalPrecision;
    }

    public void setTotalPrecision(BigDecimal totalPrecision) {
        this.totalPrecision = totalPrecision;
    }

    public Integer getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Integer totalCost) {
        this.totalCost = totalCost;
    }
}
