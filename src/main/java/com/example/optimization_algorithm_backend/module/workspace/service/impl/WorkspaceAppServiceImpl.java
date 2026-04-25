package com.example.optimization_algorithm_backend.module.workspace.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.optimization_algorithm_backend.common.exception.BusinessException;
import com.example.optimization_algorithm_backend.common.response.ErrorCode;
import com.example.optimization_algorithm_backend.common.response.PageResult;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.WorkspaceEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.service.WorkspacePersistenceService;
import com.example.optimization_algorithm_backend.module.auth.service.CurrentUserService;
import com.example.optimization_algorithm_backend.module.workspace.converter.WorkspaceConverter;
import com.example.optimization_algorithm_backend.module.workspace.dto.CreateWorkspaceRequest;
import com.example.optimization_algorithm_backend.module.workspace.dto.UpdateWorkspaceRequest;
import com.example.optimization_algorithm_backend.module.workspace.dto.WorkspaceQueryRequest;
import com.example.optimization_algorithm_backend.module.workspace.service.WorkspaceAppService;
import com.example.optimization_algorithm_backend.module.workspace.vo.WorkspaceVO;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@ConditionalOnBean(DataSource.class)
public class WorkspaceAppServiceImpl implements WorkspaceAppService {

    private static final int WORKSPACE_STATUS_ENABLED = 1;

    private final WorkspacePersistenceService workspacePersistenceService;
    private final CurrentUserService currentUserService;

    public WorkspaceAppServiceImpl(WorkspacePersistenceService workspacePersistenceService,
                                   CurrentUserService currentUserService) {
        this.workspacePersistenceService = workspacePersistenceService;
        this.currentUserService = currentUserService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WorkspaceVO createWorkspace(CreateWorkspaceRequest request) {
        Long currentUserId = currentUserService.getCurrentUserId();
        String workspaceName = request.getName().trim();
        ensureWorkspaceNameUnique(currentUserId, workspaceName, null);

        WorkspaceEntity entity = new WorkspaceEntity();
        entity.setOwnerUserId(currentUserId);
        entity.setName(workspaceName);
        entity.setDescription(request.getDescription());
        entity.setStatus(WORKSPACE_STATUS_ENABLED);
        workspacePersistenceService.save(entity);
        return WorkspaceConverter.toWorkspaceVO(entity);
    }

    @Override
    public PageResult<WorkspaceVO> listWorkspaces(WorkspaceQueryRequest request) {
        Page<WorkspaceEntity> page = new Page<>(request.getPageNo(), request.getPageSize());
        LambdaQueryWrapper<WorkspaceEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(request.getKeyword())) {
            queryWrapper.like(WorkspaceEntity::getName, request.getKeyword().trim());
        }
        if (!currentUserService.isAdmin()) {
            queryWrapper.eq(WorkspaceEntity::getOwnerUserId, currentUserService.getCurrentUserId());
        }
        queryWrapper.orderByDesc(WorkspaceEntity::getCreatedAt);

        Page<WorkspaceEntity> resultPage = workspacePersistenceService.page(page, queryWrapper);
        List<WorkspaceVO> records = resultPage.getRecords()
                .stream()
                .map(WorkspaceConverter::toWorkspaceVO)
                .collect(Collectors.toList());
        return PageResult.of(records, resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
    }

    @Override
    public WorkspaceVO getWorkspace(Long workspaceId) {
        return WorkspaceConverter.toWorkspaceVO(getAccessibleWorkspace(workspaceId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WorkspaceVO updateWorkspace(Long workspaceId, UpdateWorkspaceRequest request) {
        WorkspaceEntity workspace = getAccessibleWorkspace(workspaceId);
        String workspaceName = request.getName().trim();
        if (!Objects.equals(workspace.getName(), workspaceName)) {
            ensureWorkspaceNameUnique(workspace.getOwnerUserId(), workspaceName, workspaceId);
        }

        workspace.setName(workspaceName);
        workspace.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            workspace.setStatus(request.getStatus());
        }
        workspacePersistenceService.updateById(workspace);
        return WorkspaceConverter.toWorkspaceVO(workspace);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteWorkspace(Long workspaceId) {
        WorkspaceEntity workspace = getAccessibleWorkspace(workspaceId);
        return workspacePersistenceService.removeById(workspace.getId());
    }

    private WorkspaceEntity getAccessibleWorkspace(Long workspaceId) {
        WorkspaceEntity workspace = workspacePersistenceService.getById(workspaceId);
        if (workspace == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "工作空间不存在");
        }
        if (currentUserService.isAdmin()) {
            return workspace;
        }
        if (!Objects.equals(workspace.getOwnerUserId(), currentUserService.getCurrentUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限访问该工作空间");
        }
        return workspace;
    }

    private void ensureWorkspaceNameUnique(Long ownerUserId, String workspaceName, Long excludeId) {
        LambdaQueryWrapper<WorkspaceEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WorkspaceEntity::getOwnerUserId, ownerUserId)
                .eq(WorkspaceEntity::getName, workspaceName);
        if (excludeId != null) {
            queryWrapper.ne(WorkspaceEntity::getId, excludeId);
        }
        long count = workspacePersistenceService.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "工作空间名称已存在");
        }
    }
}
