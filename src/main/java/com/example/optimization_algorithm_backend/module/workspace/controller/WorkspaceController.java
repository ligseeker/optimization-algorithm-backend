package com.example.optimization_algorithm_backend.module.workspace.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.optimization_algorithm_backend.common.response.PageResult;
import com.example.optimization_algorithm_backend.common.response.Result;
import com.example.optimization_algorithm_backend.module.workspace.dto.CreateWorkspaceRequest;
import com.example.optimization_algorithm_backend.module.workspace.dto.UpdateWorkspaceRequest;
import com.example.optimization_algorithm_backend.module.workspace.dto.WorkspaceQueryRequest;
import com.example.optimization_algorithm_backend.module.log.annotation.OperationLog;
import com.example.optimization_algorithm_backend.module.workspace.service.WorkspaceAppService;
import com.example.optimization_algorithm_backend.module.workspace.vo.WorkspaceVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Validated
@SaCheckLogin
@RestController
@RequestMapping("/api/workspaces")
@Tag(name = "Workspace", description = "工作空间管理接口")
public class WorkspaceController {

    private final WorkspaceAppService workspaceAppService;

    public WorkspaceController(WorkspaceAppService workspaceAppService) {
        this.workspaceAppService = workspaceAppService;
    }

    @PostMapping
    @Operation(summary = "创建工作空间")
    @OperationLog(operationType = "CREATE_WORKSPACE", objectType = "WORKSPACE")
    public Result<WorkspaceVO> createWorkspace(@Valid @RequestBody CreateWorkspaceRequest request) {
        // @Valid 作用：启用对CreateWorkspaceRequest对象的验证，确保请求体中的数据符合定义的约束条件，如工作空间名称不能为空且长度限制等。如果验证失败，Spring会自动返回错误响应。
        // @RequestBody 作用：将HTTP请求体中的JSON数据转换为CreateWorkspaceRequest对象，方便在控制器方法中直接使用。
        return Result.success("创建成功", workspaceAppService.createWorkspace(request));
    }

    @GetMapping
    @Operation(summary = "分页查询工作空间")
    public Result<PageResult<WorkspaceVO>> listWorkspaces(@Valid @ModelAttribute WorkspaceQueryRequest request) {
        // @ModelAttribute 作用：将HTTP请求中的查询参数绑定到WorkspaceQueryRequest对象上，方便在控制器方法中直接使用。通过这种方式，可以将分页查询的参数（如页码、页大小、搜索关键词等）封装在一个对象中，提升代码的清晰度和可维护性。
        return Result.success(workspaceAppService.listWorkspaces(request));
    }

    @GetMapping("/{workspaceId}")
    @Operation(summary = "查询工作空间详情")
    public Result<WorkspaceVO> getWorkspace(@PathVariable @Min(value = 1, message = "workspaceId必须大于0") Long workspaceId) {
        // @Min注解用于验证workspaceId必须大于0，确保传入的ID是有效的。value属性指定了最小值，这里是1，message属性定义了当验证失败时返回的错误消息。
        // @PathVariable 作用：将URL路径中的workspaceId参数绑定到方法的workspaceId参数上，方便在控制器方法中直接使用。@Min注解用于验证workspaceId必须大于0，确保传入的ID是有效的，提升接口的健壮性和安全性。
        return Result.success(workspaceAppService.getWorkspace(workspaceId));
    }

    @PutMapping("/{workspaceId}")
    @Operation(summary = "更新工作空间")
    public Result<WorkspaceVO> updateWorkspace(@PathVariable @Min(value = 1, message = "workspaceId必须大于0") Long workspaceId,
                                               @Valid @RequestBody UpdateWorkspaceRequest request) {
        return Result.success("更新成功", workspaceAppService.updateWorkspace(workspaceId, request));
    }

    @DeleteMapping("/{workspaceId}")
    @Operation(summary = "删除工作空间")
    public Result<Boolean> deleteWorkspace(@PathVariable @Min(value = 1, message = "workspaceId必须大于0") Long workspaceId) {
        return Result.success("删除成功", workspaceAppService.deleteWorkspace(workspaceId));
    }
}
