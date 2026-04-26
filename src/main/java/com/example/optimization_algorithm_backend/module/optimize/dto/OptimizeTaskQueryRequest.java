package com.example.optimization_algorithm_backend.module.optimize.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

public class OptimizeTaskQueryRequest {

    @Min(value = 1, message = "页码必须大于等于1")
    private Long pageNo;

    @Min(value = 1, message = "每页条数必须大于等于1")
    @Max(value = 100, message = "每页条数不能超过100")
    private Long pageSize;

    @Min(value = 1, message = "workspaceId必须大于0")
    private Long workspaceId;

    @Min(value = 1, message = "graphId必须大于0")
    private Long graphId;

    @Pattern(
            regexp = "^(|PENDING|RUNNING|SUCCESS|FAILED|ALL)$",
            message = "taskStatus必须是PENDING、RUNNING、SUCCESS、FAILED、ALL之一，或不传"
    )
    private String taskStatus;

    public Long getPageNo() {
        return pageNo == null ? 1L : pageNo;
    }

    public void setPageNo(Long pageNo) {
        this.pageNo = pageNo;
    }

    public Long getPageSize() {
        return pageSize == null ? 10L : pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
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

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }
}
