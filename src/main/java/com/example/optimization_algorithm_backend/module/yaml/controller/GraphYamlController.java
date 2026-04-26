package com.example.optimization_algorithm_backend.module.yaml.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.optimization_algorithm_backend.common.response.Result;
import com.example.optimization_algorithm_backend.module.log.annotation.OperationLog;
import com.example.optimization_algorithm_backend.module.yaml.dto.GraphImportRequest;
import com.example.optimization_algorithm_backend.module.yaml.service.GraphYamlService;
import com.example.optimization_algorithm_backend.module.yaml.vo.GraphImportResponse;
import com.example.optimization_algorithm_backend.module.yaml.vo.GraphYamlExportResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Validated
@SaCheckLogin
@RestController
@RequestMapping("/api")
@Tag(name = "ImportExport", description = "YAML 导入导出接口")
public class GraphYamlController {

    private final GraphYamlService graphYamlService;

    public GraphYamlController(GraphYamlService graphYamlService) {
        this.graphYamlService = graphYamlService;
    }

    @PostMapping(value = "/import/graphs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "导入YAML为流程图")
    @OperationLog(operationType = "IMPORT_YAML", objectType = "GRAPH")
    public Result<GraphImportResponse> importGraph(@RequestPart("file") MultipartFile file,
                                                   @Valid @ModelAttribute GraphImportRequest request) {
        return Result.success("导入成功", graphYamlService.importGraph(request.getWorkspaceId(), request.getGraphName(), file));
    }

    @GetMapping("/export/graphs/{graphId}/yaml")
    @Operation(summary = "导出流程图为YAML")
    @OperationLog(operationType = "EXPORT_YAML", objectType = "GRAPH", objectIdParam = "graphId")
    public Result<GraphYamlExportResponse> exportGraphYaml(
            @PathVariable @Min(value = 1, message = "graphId必须大于0") Long graphId) {
        return Result.success(graphYamlService.exportGraphYaml(graphId));
    }
}
