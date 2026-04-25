package com.example.optimization_algorithm_backend.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("flow_graph")
public class FlowGraphEntity extends BaseEntity {

    @TableField("workspace_id")
    private Long workspaceId;

    @TableField("name")
    private String name;

    @TableField("description")
    private String description;

    @TableField("source_type")
    private String sourceType;

    @TableField("graph_status")
    private String graphStatus;

    @TableField("total_time")
    private Integer totalTime;

    @TableField("total_precision")
    private BigDecimal totalPrecision;

    @TableField("total_cost")
    private Integer totalCost;

    @TableField("last_import_at")
    private LocalDateTime lastImportAt;

    @TableField("last_export_at")
    private LocalDateTime lastExportAt;

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

    public LocalDateTime getLastImportAt() {
        return lastImportAt;
    }

    public void setLastImportAt(LocalDateTime lastImportAt) {
        this.lastImportAt = lastImportAt;
    }

    public LocalDateTime getLastExportAt() {
        return lastExportAt;
    }

    public void setLastExportAt(LocalDateTime lastExportAt) {
        this.lastExportAt = lastExportAt;
    }
}
