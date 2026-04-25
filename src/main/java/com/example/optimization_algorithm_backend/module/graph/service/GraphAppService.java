package com.example.optimization_algorithm_backend.module.graph.service;

import com.example.optimization_algorithm_backend.common.response.PageResult;
import com.example.optimization_algorithm_backend.module.graph.dto.CreateGraphRequest;
import com.example.optimization_algorithm_backend.module.graph.dto.GraphQueryRequest;
import com.example.optimization_algorithm_backend.module.graph.dto.UpdateGraphRequest;
import com.example.optimization_algorithm_backend.module.graph.vo.GraphVO;

public interface GraphAppService {

    GraphVO createGraph(Long workspaceId, CreateGraphRequest request);

    PageResult<GraphVO> listGraphs(Long workspaceId, GraphQueryRequest request);

    GraphVO getGraph(Long graphId);

    GraphVO updateGraph(Long graphId, UpdateGraphRequest request);

    boolean deleteGraph(Long graphId);
}
