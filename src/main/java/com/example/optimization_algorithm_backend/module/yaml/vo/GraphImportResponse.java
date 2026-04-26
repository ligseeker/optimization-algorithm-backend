package com.example.optimization_algorithm_backend.module.yaml.vo;

public class GraphImportResponse {

    private Long graphId;
    private Long workspaceId;
    private String graphName;
    private String sourceType;
    private Integer nodeCount;
    private Integer pathCount;
    private Integer equipmentCount;
    private Integer constraintCount;

    public Long getGraphId() {
        return graphId;
    }

    public void setGraphId(Long graphId) {
        this.graphId = graphId;
    }

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

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getNodeCount() {
        return nodeCount;
    }

    public void setNodeCount(Integer nodeCount) {
        this.nodeCount = nodeCount;
    }

    public Integer getPathCount() {
        return pathCount;
    }

    public void setPathCount(Integer pathCount) {
        this.pathCount = pathCount;
    }

    public Integer getEquipmentCount() {
        return equipmentCount;
    }

    public void setEquipmentCount(Integer equipmentCount) {
        this.equipmentCount = equipmentCount;
    }

    public Integer getConstraintCount() {
        return constraintCount;
    }

    public void setConstraintCount(Integer constraintCount) {
        this.constraintCount = constraintCount;
    }
}
