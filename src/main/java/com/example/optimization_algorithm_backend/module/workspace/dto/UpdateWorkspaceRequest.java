package com.example.optimization_algorithm_backend.module.workspace.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UpdateWorkspaceRequest {

    @NotBlank(message = "工作空间名称不能为空")
    @Size(max = 128, message = "工作空间名称长度不能超过128位")
    private String name;

    @Size(max = 500, message = "工作空间描述长度不能超过500位")
    private String description;

    @Min(value = 0, message = "工作空间状态只能是0或1")
    @Max(value = 1, message = "工作空间状态只能是0或1")
    private Integer status;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
