package com.example.optimization_algorithm_backend.module.node.service;

import com.example.optimization_algorithm_backend.common.response.PageResult;
import com.example.optimization_algorithm_backend.module.node.dto.CreateNodeRequest;
import com.example.optimization_algorithm_backend.module.node.dto.NodeQueryRequest;
import com.example.optimization_algorithm_backend.module.node.dto.UpdateNodeRequest;
import com.example.optimization_algorithm_backend.module.node.vo.NodeVO;

public interface NodeAppService {

    NodeVO createNode(Long graphId, CreateNodeRequest request);

    PageResult<NodeVO> listNodes(Long graphId, NodeQueryRequest request);

    NodeVO getNode(Long graphId, Long nodeId);

    NodeVO updateNode(Long graphId, Long nodeId, UpdateNodeRequest request);

    boolean deleteNode(Long graphId, Long nodeId);
}
