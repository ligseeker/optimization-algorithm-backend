package com.example.optimization_algorithm_backend.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("operation_log")
public class OperationLogEntity extends BaseEntity {

    @TableField("user_id")
    private Long userId;

    @TableField("workspace_id")
    private Long workspaceId;

    @TableField("graph_id")
    private Long graphId;

    @TableField("task_id")
    private Long taskId;

    @TableField("operation_type")
    private String operationType;

    @TableField("object_type")
    private String objectType;

    @TableField("object_id")
    private Long objectId;

    @TableField("request_method")
    private String requestMethod;

    @TableField("request_uri")
    private String requestUri;

    @TableField("request_params")
    private String requestParams;

    @TableField("response_code")
    private Integer responseCode;

    @TableField("success_flag")
    private Integer successFlag;

    @TableField("error_message")
    private String errorMessage;

    @TableField("cost_time_ms")
    private Integer costTimeMs;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(String requestParams) {
        this.requestParams = requestParams;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public Integer getSuccessFlag() {
        return successFlag;
    }

    public void setSuccessFlag(Integer successFlag) {
        this.successFlag = successFlag;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getCostTimeMs() {
        return costTimeMs;
    }

    public void setCostTimeMs(Integer costTimeMs) {
        this.costTimeMs = costTimeMs;
    }
}
