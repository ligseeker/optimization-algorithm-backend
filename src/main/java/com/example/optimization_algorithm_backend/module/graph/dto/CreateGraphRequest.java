package com.example.optimization_algorithm_backend.module.graph.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CreateGraphRequest {

    @Schema(description = "流程图名称", example = "装配流程图A")
    @NotBlank(message = "流程图名称不能为空")
    @Size(max = 128, message = "流程图名称长度不能超过128位")
    private String name;

    @Schema(description = "流程图描述", example = "用于演示的流程图")
    @Size(max = 500, message = "流程图描述长度不能超过500位")
    private String description;

    @Schema(description = "来源类型", example = "MANUAL")
    @Size(max = 32, message = "sourceType长度不能超过32位")
    private String sourceType;

    @Schema(description = "流程图状态", example = "DRAFT")
    @Size(max = 32, message = "graphStatus长度不能超过32位")
    private String graphStatus;

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

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getGraphStatus() {
        return graphStatus;
    }

    public void setGraphStatus(String graphStatus) {
        this.graphStatus = graphStatus;
    }
}
