package com.example.optimization_algorithm_backend.module.optimize.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OptimizeResultVO {

    private Long id;
    private Long taskId;
    private Long workspaceId;
    private Long sourceGraphId;
    private String resultName;
    private Object resultGraph;
    private Object diff;
    private String mapCode;
    private Integer totalTimeBefore;
    private BigDecimal totalPrecisionBefore;
    private Integer totalCostBefore;
    private Integer totalTimeAfter;
    private BigDecimal totalPrecisionAfter;
    private Integer totalCostAfter;
    private BigDecimal scoreRatio;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public Long getSourceGraphId() {
        return sourceGraphId;
    }

    public void setSourceGraphId(Long sourceGraphId) {
        this.sourceGraphId = sourceGraphId;
    }

    public String getResultName() {
        return resultName;
    }

    public void setResultName(String resultName) {
        this.resultName = resultName;
    }

    public Object getResultGraph() {
        return resultGraph;
    }

    public void setResultGraph(Object resultGraph) {
        this.resultGraph = resultGraph;
    }

    public Object getDiff() {
        return diff;
    }

    public void setDiff(Object diff) {
        this.diff = diff;
    }

    public String getMapCode() {
        return mapCode;
    }

    public void setMapCode(String mapCode) {
        this.mapCode = mapCode;
    }

    public Integer getTotalTimeBefore() {
        return totalTimeBefore;
    }

    public void setTotalTimeBefore(Integer totalTimeBefore) {
        this.totalTimeBefore = totalTimeBefore;
    }

    public BigDecimal getTotalPrecisionBefore() {
        return totalPrecisionBefore;
    }

    public void setTotalPrecisionBefore(BigDecimal totalPrecisionBefore) {
        this.totalPrecisionBefore = totalPrecisionBefore;
    }

    public Integer getTotalCostBefore() {
        return totalCostBefore;
    }

    public void setTotalCostBefore(Integer totalCostBefore) {
        this.totalCostBefore = totalCostBefore;
    }

    public Integer getTotalTimeAfter() {
        return totalTimeAfter;
    }

    public void setTotalTimeAfter(Integer totalTimeAfter) {
        this.totalTimeAfter = totalTimeAfter;
    }

    public BigDecimal getTotalPrecisionAfter() {
        return totalPrecisionAfter;
    }

    public void setTotalPrecisionAfter(BigDecimal totalPrecisionAfter) {
        this.totalPrecisionAfter = totalPrecisionAfter;
    }

    public Integer getTotalCostAfter() {
        return totalCostAfter;
    }

    public void setTotalCostAfter(Integer totalCostAfter) {
        this.totalCostAfter = totalCostAfter;
    }

    public BigDecimal getScoreRatio() {
        return scoreRatio;
    }

    public void setScoreRatio(BigDecimal scoreRatio) {
        this.scoreRatio = scoreRatio;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
