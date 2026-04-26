package com.example.optimization_algorithm_backend.module.yaml.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Min;

public class GraphImportRequest {

    @Schema(description = "目标工作空间ID", example = "1")
    @Min(value = 1, message = "workspaceId必须大于0")
    private Long workspaceId;

    @Schema(description = "导入后流程图名称，不传则用文件名", example = "yaml导入图")
    private String graphName;

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getGraphName() {
        return graphName;
    }

    public void setGraphName(String graphName) {
        this.graphName = graphName;
    }
}
