package com.example.optimization_algorithm_backend.module.node.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.optimization_algorithm_backend.common.response.PageResult;
import com.example.optimization_algorithm_backend.common.response.Result;
import com.example.optimization_algorithm_backend.module.node.dto.CreateNodeRequest;
import com.example.optimization_algorithm_backend.module.node.dto.NodeQueryRequest;
import com.example.optimization_algorithm_backend.module.node.dto.UpdateNodeRequest;
import com.example.optimization_algorithm_backend.module.node.service.NodeAppService;
import com.example.optimization_algorithm_backend.module.node.vo.NodeVO;
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
@RequestMapping("/api/graphs/{graphId}/nodes")
public class ApiNodeController {

    private final NodeAppService nodeAppService;

    public ApiNodeController(NodeAppService nodeAppService) {
        this.nodeAppService = nodeAppService;
    }

    @PostMapping
    public Result<NodeVO> createNode(@PathVariable @Min(value = 1, message = "graphId必须大于0") Long graphId,
                                     @Valid @RequestBody CreateNodeRequest request) {
        return Result.success("创建成功", nodeAppService.createNode(graphId, request));
    }

    @GetMapping
    public Result<PageResult<NodeVO>> listNodes(@PathVariable @Min(value = 1, message = "graphId必须大于0") Long graphId,
                                                @Valid @ModelAttribute NodeQueryRequest request) {
        return Result.success(nodeAppService.listNodes(graphId, request));
    }

    @GetMapping("/{nodeId}")
    public Result<NodeVO> getNode(@PathVariable @Min(value = 1, message = "graphId必须大于0") Long graphId,
                                  @PathVariable @Min(value = 1, message = "nodeId必须大于0") Long nodeId) {
        return Result.success(nodeAppService.getNode(graphId, nodeId));
    }

    @PutMapping("/{nodeId}")
    public Result<NodeVO> updateNode(@PathVariable @Min(value = 1, message = "graphId必须大于0") Long graphId,
                                     @PathVariable @Min(value = 1, message = "nodeId必须大于0") Long nodeId,
                                     @Valid @RequestBody UpdateNodeRequest request) {
        return Result.success("更新成功", nodeAppService.updateNode(graphId, nodeId, request));
    }

    @DeleteMapping("/{nodeId}")
    public Result<Boolean> deleteNode(@PathVariable @Min(value = 1, message = "graphId必须大于0") Long graphId,
                                      @PathVariable @Min(value = 1, message = "nodeId必须大于0") Long nodeId) {
        return Result.success("删除成功", nodeAppService.deleteNode(graphId, nodeId));
    }
}
