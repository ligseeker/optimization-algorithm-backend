package com.example.optimization_algorithm_backend.module.path.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.optimization_algorithm_backend.common.response.PageResult;
import com.example.optimization_algorithm_backend.common.response.Result;
import com.example.optimization_algorithm_backend.module.log.annotation.OperationLog;
import com.example.optimization_algorithm_backend.module.path.dto.CreatePathRequest;
import com.example.optimization_algorithm_backend.module.path.dto.PathQueryRequest;
import com.example.optimization_algorithm_backend.module.path.dto.UpdatePathRequest;
import com.example.optimization_algorithm_backend.module.path.service.PathAppService;
import com.example.optimization_algorithm_backend.module.path.vo.PathVO;
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
@RequestMapping("/api/graphs/{graphId}/paths")
@Tag(name = "Path", description = "流程路径管理接口")
public class ApiPathController {

    private final PathAppService pathAppService;

    public ApiPathController(PathAppService pathAppService) {
        this.pathAppService = pathAppService;
    }

    @PostMapping
    @Operation(summary = "新增路径")
    @OperationLog(operationType = "CREATE_PATH", objectType = "PATH", objectIdParam = "graphId")
    public Result<PathVO> createPath(@PathVariable @Min(value = 1, message = "graphId必须大于0") Long graphId,
                                     @Valid @RequestBody CreatePathRequest request) {
        return Result.success("创建成功", pathAppService.createPath(graphId, request));
    }

    @GetMapping
    @Operation(summary = "分页查询路径")
    public Result<PageResult<PathVO>> listPaths(@PathVariable @Min(value = 1, message = "graphId必须大于0") Long graphId,
                                                @Valid @ModelAttribute PathQueryRequest request) {
        return Result.success(pathAppService.listPaths(graphId, request));
    }

    @GetMapping("/{pathId}")
    @Operation(summary = "查询路径详情")
    public Result<PathVO> getPath(@PathVariable @Min(value = 1, message = "graphId必须大于0") Long graphId,
                                  @PathVariable @Min(value = 1, message = "pathId必须大于0") Long pathId) {
        return Result.success(pathAppService.getPath(graphId, pathId));
    }

    @PutMapping("/{pathId}")
    @Operation(summary = "修改路径")
    public Result<PathVO> updatePath(@PathVariable @Min(value = 1, message = "graphId必须大于0") Long graphId,
                                     @PathVariable @Min(value = 1, message = "pathId必须大于0") Long pathId,
                                     @Valid @RequestBody UpdatePathRequest request) {
        return Result.success("更新成功", pathAppService.updatePath(graphId, pathId, request));
    }

    @DeleteMapping("/{pathId}")
    @Operation(summary = "删除路径")
    @OperationLog(operationType = "DELETE_PATH", objectType = "PATH", objectIdParam = "pathId")
    public Result<Boolean> deletePath(@PathVariable @Min(value = 1, message = "graphId必须大于0") Long graphId,
                                      @PathVariable @Min(value = 1, message = "pathId必须大于0") Long pathId) {
        return Result.success("删除成功", pathAppService.deletePath(graphId, pathId));
    }
}
