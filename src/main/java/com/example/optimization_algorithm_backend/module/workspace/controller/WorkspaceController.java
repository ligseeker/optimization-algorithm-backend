package com.example.optimization_algorithm_backend.module.workspace.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.optimization_algorithm_backend.common.response.PageResult;
import com.example.optimization_algorithm_backend.common.response.Result;
import com.example.optimization_algorithm_backend.module.workspace.dto.CreateWorkspaceRequest;
import com.example.optimization_algorithm_backend.module.workspace.dto.UpdateWorkspaceRequest;
import com.example.optimization_algorithm_backend.module.workspace.dto.WorkspaceQueryRequest;
import com.example.optimization_algorithm_backend.module.workspace.service.WorkspaceAppService;
import com.example.optimization_algorithm_backend.module.workspace.vo.WorkspaceVO;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import javax.validation.Valid;
import javax.validation.constraints.Min;

@Validated
@SaCheckLogin
@RestController
@RequestMapping("/api/workspaces")
@ConditionalOnBean(DataSource.class)
public class WorkspaceController {

    private final WorkspaceAppService workspaceAppService;

    public WorkspaceController(WorkspaceAppService workspaceAppService) {
        this.workspaceAppService = workspaceAppService;
    }

    @PostMapping
    public Result<WorkspaceVO> createWorkspace(@Valid @RequestBody CreateWorkspaceRequest request) {
        return Result.success("创建成功", workspaceAppService.createWorkspace(request));
    }

    @GetMapping
    public Result<PageResult<WorkspaceVO>> listWorkspaces(@Valid @ModelAttribute WorkspaceQueryRequest request) {
        return Result.success(workspaceAppService.listWorkspaces(request));
    }

    @GetMapping("/{workspaceId}")
    public Result<WorkspaceVO> getWorkspace(@PathVariable @Min(value = 1, message = "workspaceId必须大于0") Long workspaceId) {
        return Result.success(workspaceAppService.getWorkspace(workspaceId));
    }

    @PutMapping("/{workspaceId}")
    public Result<WorkspaceVO> updateWorkspace(@PathVariable @Min(value = 1, message = "workspaceId必须大于0") Long workspaceId,
                                               @Valid @RequestBody UpdateWorkspaceRequest request) {
        return Result.success("更新成功", workspaceAppService.updateWorkspace(workspaceId, request));
    }

    @DeleteMapping("/{workspaceId}")
    public Result<Boolean> deleteWorkspace(@PathVariable @Min(value = 1, message = "workspaceId必须大于0") Long workspaceId) {
        return Result.success("删除成功", workspaceAppService.deleteWorkspace(workspaceId));
    }
}
