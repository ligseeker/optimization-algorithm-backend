package com.example.optimization_algorithm_backend.module.workspace.service;

import com.example.optimization_algorithm_backend.common.response.PageResult;
import com.example.optimization_algorithm_backend.module.workspace.dto.CreateWorkspaceRequest;
import com.example.optimization_algorithm_backend.module.workspace.dto.UpdateWorkspaceRequest;
import com.example.optimization_algorithm_backend.module.workspace.dto.WorkspaceQueryRequest;
import com.example.optimization_algorithm_backend.module.workspace.vo.WorkspaceVO;

public interface WorkspaceAppService {

    WorkspaceVO createWorkspace(CreateWorkspaceRequest request);

    PageResult<WorkspaceVO> listWorkspaces(WorkspaceQueryRequest request);

    WorkspaceVO getWorkspace(Long workspaceId);

    WorkspaceVO updateWorkspace(Long workspaceId, UpdateWorkspaceRequest request);

    boolean deleteWorkspace(Long workspaceId);
}
