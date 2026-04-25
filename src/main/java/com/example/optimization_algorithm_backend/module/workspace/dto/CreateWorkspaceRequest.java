package com.example.optimization_algorithm_backend.module.workspace.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CreateWorkspaceRequest {

    @NotBlank(message = "工作空间名称不能为空")
    @Size(max = 128, message = "工作空间名称长度不能超过128位")
    private String name;

    @Size(max = 500, message = "工作空间描述长度不能超过500位")
    private String description;

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
}
