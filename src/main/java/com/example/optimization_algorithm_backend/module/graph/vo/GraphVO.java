package com.example.optimization_algorithm_backend.module.graph.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GraphVO {

    private Long id;
    private Long workspaceId;
    private String name;
    private String description;
    private String sourceType;
    private String graphStatus;
    private Long graphVersion;
    private Integer totalTime;
    private BigDecimal totalPrecision;
    private Integer totalCost;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

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

    public Long getGraphVersion() {
        return graphVersion;
    }

    public void setGraphVersion(Long graphVersion) {
        this.graphVersion = graphVersion;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
