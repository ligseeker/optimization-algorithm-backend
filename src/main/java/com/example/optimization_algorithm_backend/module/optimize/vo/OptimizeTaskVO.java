package com.example.optimization_algorithm_backend.module.optimize.vo;

import java.time.LocalDateTime;

public class OptimizeTaskVO {

    private Long id;
    private String taskNo;
    private Long workspaceId;
    private Long graphId;
    private Long userId;
    private Integer algorithmType;
    private Integer algorithmMode;
    private Integer timeWeight;
    private Integer precisionWeight;
    private Integer costWeight;
    private String taskStatus;
    private Integer retryCount;
    private Integer maxRetryCount;
    private LocalDateTime queueTime;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private String errorCode;
    private String errorMessage;
    private Long resultId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskNo() {
        return taskNo;
    }

    public void setTaskNo(String taskNo) {
        this.taskNo = taskNo;
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public Long getGraphId() {
        return graphId;
    }

    public void setGraphId(Long graphId) {
        this.graphId = graphId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getAlgorithmType() {
        return algorithmType;
    }

    public void setAlgorithmType(Integer algorithmType) {
        this.algorithmType = algorithmType;
    }

    public Integer getAlgorithmMode() {
        return algorithmMode;
    }

    public void setAlgorithmMode(Integer algorithmMode) {
        this.algorithmMode = algorithmMode;
    }

    public Integer getTimeWeight() {
        return timeWeight;
    }

    public void setTimeWeight(Integer timeWeight) {
        this.timeWeight = timeWeight;
    }

    public Integer getPrecisionWeight() {
        return precisionWeight;
    }

    public void setPrecisionWeight(Integer precisionWeight) {
        this.precisionWeight = precisionWeight;
    }

    public Integer getCostWeight() {
        return costWeight;
    }

    public void setCostWeight(Integer costWeight) {
        this.costWeight = costWeight;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Integer getMaxRetryCount() {
        return maxRetryCount;
    }

    public void setMaxRetryCount(Integer maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public LocalDateTime getQueueTime() {
        return queueTime;
    }

    public void setQueueTime(LocalDateTime queueTime) {
        this.queueTime = queueTime;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Long getResultId() {
        return resultId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
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
