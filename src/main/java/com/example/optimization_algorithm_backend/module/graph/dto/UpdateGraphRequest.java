package com.example.optimization_algorithm_backend.module.graph.dto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public class UpdateGraphRequest {

    @NotBlank(message = "流程图名称不能为空")
    @Size(max = 128, message = "流程图名称长度不能超过128位")
    private String name;

    @Size(max = 500, message = "流程图描述长度不能超过500位")
    private String description;

    @Size(max = 32, message = "graphStatus长度不能超过32位")
    private String graphStatus;

    @Min(value = 0, message = "totalTime不能小于0")
    private Integer totalTime;

    @DecimalMin(value = "0", message = "totalPrecision不能小于0")
    private BigDecimal totalPrecision;

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
