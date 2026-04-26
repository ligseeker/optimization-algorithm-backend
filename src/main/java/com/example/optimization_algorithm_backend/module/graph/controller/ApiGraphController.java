package com.example.optimization_algorithm_backend.module.graph.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.optimization_algorithm_backend.common.response.PageResult;
import com.example.optimization_algorithm_backend.common.response.Result;
import com.example.optimization_algorithm_backend.module.graph.dto.CreateGraphRequest;
import com.example.optimization_algorithm_backend.module.graph.dto.GraphQueryRequest;
import com.example.optimization_algorithm_backend.module.graph.dto.UpdateGraphRequest;
import com.example.optimization_algorithm_backend.module.graph.service.GraphAppService;
import com.example.optimization_algorithm_backend.module.graph.vo.GraphDetailVO;
import com.example.optimization_algorithm_backend.module.graph.vo.GraphVO;
import com.example.optimization_algorithm_backend.module.log.annotation.OperationLog;
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
@RequestMapping("/api")
@Tag(name = "FlowGraph", description = "流程图管理接口")
public class ApiGraphController {

    private final GraphAppService graphAppService;

    public ApiGraphController(GraphAppService graphAppService) {
        this.graphAppService = graphAppService;
    }

    @PostMapping("/workspaces/{workspaceId}/graphs")
    @Operation(summary = "创建流程图")
    @OperationLog(operationType = "CREATE_GRAPH", objectType = "GRAPH")
    public Result<GraphVO> createGraph(@PathVariable @Min(value = 1, message = "workspaceId必须大于0") Long workspaceId,
                                       @Valid @RequestBody CreateGraphRequest request) {
        return Result.success("创建成功", graphAppService.createGraph(workspaceId, request));
    }

    @GetMapping("/workspaces/{workspaceId}/graphs")
    @Operation(summary = "分页查询流程图")
    public Result<PageResult<GraphVO>> listGraphs(@PathVariable @Min(value = 1, message = "workspaceId必须大于0") Long workspaceId,
                                                  @Valid @ModelAttribute GraphQueryRequest request) {
        return Result.success(graphAppService.listGraphs(workspaceId, request));
    }

    @GetMapping("/graphs/{graphId}")
    @Operation(summary = "查询流程图基础信息")
    public Result<GraphVO> getGraph(@PathVariable @Min(value = 1, message = "graphId必须大于0") Long graphId) {
        return Result.success(graphAppService.getGraph(graphId));
    }

    @GetMapping("/graphs/{graphId}/detail")
    @Operation(summary = "查询流程图聚合详情（展示态缓存）")
    public Result<GraphDetailVO> getGraphDetail(@PathVariable @Min(value = 1, message = "graphId必须大于0") Long graphId) {
        return Result.success(graphAppService.getGraphDetail(graphId));
    }

    @PutMapping("/graphs/{graphId}")
    @Operation(summary = "更新流程图基础信息")
    public Result<GraphVO> updateGraph(@PathVariable @Min(value = 1, message = "graphId必须大于0") Long graphId,
                                       @Valid @RequestBody UpdateGraphRequest request) {
        return Result.success("更新成功", graphAppService.updateGraph(graphId, request));
    }

    @DeleteMapping("/graphs/{graphId}")
    @Operation(summary = "删除流程图")
    public Result<Boolean> deleteGraph(@PathVariable @Min(value = 1, message = "graphId必须大于0") Long graphId) {
        return Result.success("删除成功", graphAppService.deleteGraph(graphId));
    }
}
