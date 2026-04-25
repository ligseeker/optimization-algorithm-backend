package com.example.optimization_algorithm_backend.module.graph.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CreateGraphRequest {

    @NotBlank(message = "流程图名称不能为空")
    @Size(max = 128, message = "流程图名称长度不能超过128位")
    private String name;

    @Size(max = 500, message = "流程图描述长度不能超过500位")
    private String description;

    @Size(max = 32, message = "sourceType长度不能超过32位")
    private String sourceType;

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
