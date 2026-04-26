package com.example.optimization_algorithm_backend.module.yaml.dto;

import javax.validation.constraints.Min;

public class GraphImportRequest {

    @Min(value = 1, message = "workspaceId必须大于0")
    private Long workspaceId;

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
