package com.example.optimization_algorithm_backend.module.workspace.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CreateWorkspaceRequest {

    @Schema(description = "工作空间名称", example = "默认工作空间")
    @NotBlank(message = "工作空间名称不能为空")
    @Size(max = 128, message = "工作空间名称长度不能超过128位")
    private String name;

    @Schema(description = "工作空间描述", example = "管理员默认空间")
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
