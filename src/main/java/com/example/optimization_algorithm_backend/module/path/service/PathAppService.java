package com.example.optimization_algorithm_backend.module.path.service;

import com.example.optimization_algorithm_backend.common.response.PageResult;
import com.example.optimization_algorithm_backend.module.path.dto.CreatePathRequest;
import com.example.optimization_algorithm_backend.module.path.dto.PathQueryRequest;
import com.example.optimization_algorithm_backend.module.path.dto.UpdatePathRequest;
import com.example.optimization_algorithm_backend.module.path.vo.PathVO;

public interface PathAppService {

    PathVO createPath(Long graphId, CreatePathRequest request);

    PageResult<PathVO> listPaths(Long graphId, PathQueryRequest request);

    PathVO getPath(Long graphId, Long pathId);

    PathVO updatePath(Long graphId, Long pathId, UpdatePathRequest request);

    boolean deletePath(Long graphId, Long pathId);
}
