package com.example.optimization_algorithm_backend.module.constraint.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.optimization_algorithm_backend.common.response.PageResult;
import com.example.optimization_algorithm_backend.common.response.Result;
import com.example.optimization_algorithm_backend.module.log.annotation.OperationLog;
import com.example.optimization_algorithm_backend.module.constraint.dto.ConstraintQueryRequest;
import com.example.optimization_algorithm_backend.module.constraint.dto.CreateConstraintRequest;
import com.example.optimization_algorithm_backend.module.constraint.dto.UpdateConstraintRequest;
import com.example.optimization_algorithm_backend.module.constraint.service.ConstraintAppService;
import com.example.optimization_algorithm_backend.module.constraint.vo.ConstraintVO;
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
@RequestMapping("/api/graphs/{graphId}/constraints")
@Tag(name = "Constraint", description = "约束管理接口")
public class ApiConstraintController {

    private final ConstraintAppService constraintAppService;

    public ApiConstraintController(ConstraintAppService constraintAppService) {
        this.constraintAppService = constraintAppService;
    }

    @PostMapping
    @Operation(summary = "新增约束")
    @OperationLog(operationType = "CREATE_CONSTRAINT", objectType = "CONSTRAINT", objectIdParam = "graphId")
    public Result<ConstraintVO> createConstraint(@PathVariable @Min(value = 1, message = "graphId必须大于0") Long graphId,
                                                 @Valid @RequestBody CreateConstraintRequest request) {
        return Result.success("创建成功", constraintAppService.createConstraint(graphId, request));
    }

    @GetMapping
    @Operation(summary = "分页查询约束")
    public Result<PageResult<ConstraintVO>> listConstraints(@PathVariable @Min(value = 1, message = "graphId必须大于0") Long graphId,
                                                            @Valid @ModelAttribute ConstraintQueryRequest request) {
        return Result.success(constraintAppService.listConstraints(graphId, request));
    }

    @GetMapping("/{constraintId}")
    @Operation(summary = "查询约束详情")
    public Result<ConstraintVO> getConstraint(@PathVariable @Min(value = 1, message = "graphId必须大于0") Long graphId,
                                              @PathVariable @Min(value = 1, message = "constraintId必须大于0") Long constraintId) {
        return Result.success(constraintAppService.getConstraint(graphId, constraintId));
    }

    @PutMapping("/{constraintId}")
    @Operation(summary = "修改约束")
    @OperationLog(operationType = "UPDATE_CONSTRAINT", objectType = "CONSTRAINT", objectIdParam = "constraintId")
    public Result<ConstraintVO> updateConstraint(@PathVariable @Min(value = 1, message = "graphId必须大于0") Long graphId,
                                                 @PathVariable @Min(value = 1, message = "constraintId必须大于0") Long constraintId,
                                                 @Valid @RequestBody UpdateConstraintRequest request) {
        return Result.success("更新成功", constraintAppService.updateConstraint(graphId, constraintId, request));
    }

    @DeleteMapping("/{constraintId}")
    @Operation(summary = "删除约束")
    @OperationLog(operationType = "DELETE_CONSTRAINT", objectType = "CONSTRAINT", objectIdParam = "constraintId")
    public Result<Boolean> deleteConstraint(@PathVariable @Min(value = 1, message = "graphId必须大于0") Long graphId,
                                            @PathVariable @Min(value = 1, message = "constraintId必须大于0") Long constraintId) {
        return Result.success("删除成功", constraintAppService.deleteConstraint(graphId, constraintId));
    }
}
