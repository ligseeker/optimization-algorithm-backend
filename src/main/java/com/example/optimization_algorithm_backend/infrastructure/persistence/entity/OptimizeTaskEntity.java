package com.example.optimization_algorithm_backend.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("optimize_task")
public class OptimizeTaskEntity extends BaseEntity {

    @TableField("task_no")
    private String taskNo;

    @TableField("workspace_id")
    private Long workspaceId;

    @TableField("graph_id")
    private Long graphId;

    @TableField("user_id")
    private Long userId;

    @TableField("algorithm_type")
    private Integer algorithmType;

    @TableField("algorithm_mode")
    private Integer algorithmMode;

    @TableField("time_weight")
    private Integer timeWeight;

    @TableField("precision_weight")
    private Integer precisionWeight;

    @TableField("cost_weight")
    private Integer costWeight;

    @TableField("task_status")
    private String taskStatus;

    @TableField("retry_count")
    private Integer retryCount;

    @TableField("max_retry_count")
    private Integer maxRetryCount;

    @TableField("queue_time")
    private LocalDateTime queueTime;

    @TableField("started_at")
    private LocalDateTime startedAt;

    @TableField("finished_at")
    private LocalDateTime finishedAt;

    @TableField("error_code")
    private String errorCode;

    @TableField("error_message")
    private String errorMessage;

    @TableField("result_id")
    private Long resultId;

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
}
