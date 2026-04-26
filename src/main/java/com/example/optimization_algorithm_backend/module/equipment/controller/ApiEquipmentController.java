package com.example.optimization_algorithm_backend.module.equipment.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.optimization_algorithm_backend.common.response.PageResult;
import com.example.optimization_algorithm_backend.common.response.Result;
import com.example.optimization_algorithm_backend.module.log.annotation.OperationLog;
import com.example.optimization_algorithm_backend.module.equipment.dto.CreateEquipmentRequest;
import com.example.optimization_algorithm_backend.module.equipment.dto.EquipmentQueryRequest;
import com.example.optimization_algorithm_backend.module.equipment.dto.UpdateEquipmentRequest;
import com.example.optimization_algorithm_backend.module.equipment.service.EquipmentAppService;
import com.example.optimization_algorithm_backend.module.equipment.vo.EquipmentVO;
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
@RequestMapping("/api/graphs/{graphId}/equipments")
@Tag(name = "Equipment", description = "装备管理接口")
public class ApiEquipmentController {

    private final EquipmentAppService equipmentAppService;

    public ApiEquipmentController(EquipmentAppService equipmentAppService) {
        this.equipmentAppService = equipmentAppService;
    }

    @PostMapping
    @Operation(summary = "新增装备")
    @OperationLog(operationType = "CREATE_EQUIPMENT", objectType = "EQUIPMENT", objectIdParam = "graphId")
    public Result<EquipmentVO> createEquipment(@PathVariable @Min(value = 1, message = "graphId必须大于0") Long graphId,
                                               @Valid @RequestBody CreateEquipmentRequest request) {
        return Result.success("创建成功", equipmentAppService.createEquipment(graphId, request));
    }

    @GetMapping
    @Operation(summary = "分页查询装备")
    public Result<PageResult<EquipmentVO>> listEquipments(@PathVariable @Min(value = 1, message = "graphId必须大于0") Long graphId,
                                                          @Valid @ModelAttribute EquipmentQueryRequest request) {
        return Result.success(equipmentAppService.listEquipments(graphId, request));
    }

    @GetMapping("/{equipmentId}")
    @Operation(summary = "查询装备详情")
    public Result<EquipmentVO> getEquipment(@PathVariable @Min(value = 1, message = "graphId必须大于0") Long graphId,
                                            @PathVariable @Min(value = 1, message = "equipmentId必须大于0") Long equipmentId) {
        return Result.success(equipmentAppService.getEquipment(graphId, equipmentId));
    }

    @PutMapping("/{equipmentId}")
    @Operation(summary = "修改装备")
    @OperationLog(operationType = "UPDATE_EQUIPMENT", objectType = "EQUIPMENT", objectIdParam = "equipmentId")
    public Result<EquipmentVO> updateEquipment(@PathVariable @Min(value = 1, message = "graphId必须大于0") Long graphId,
                                               @PathVariable @Min(value = 1, message = "equipmentId必须大于0") Long equipmentId,
                                               @Valid @RequestBody UpdateEquipmentRequest request) {
        return Result.success("更新成功", equipmentAppService.updateEquipment(graphId, equipmentId, request));
    }

    @DeleteMapping("/{equipmentId}")
    @Operation(summary = "删除装备")
    @OperationLog(operationType = "DELETE_EQUIPMENT", objectType = "EQUIPMENT", objectIdParam = "equipmentId")
    public Result<Boolean> deleteEquipment(@PathVariable @Min(value = 1, message = "graphId必须大于0") Long graphId,
                                           @PathVariable @Min(value = 1, message = "equipmentId必须大于0") Long equipmentId) {
        return Result.success("删除成功", equipmentAppService.deleteEquipment(graphId, equipmentId));
    }
}
