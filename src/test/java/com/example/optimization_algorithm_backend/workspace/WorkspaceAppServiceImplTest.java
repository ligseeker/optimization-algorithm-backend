package com.example.optimization_algorithm_backend.workspace;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.optimization_algorithm_backend.common.exception.BusinessException;
import com.example.optimization_algorithm_backend.common.response.ErrorCode;
import com.example.optimization_algorithm_backend.common.response.PageResult;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.WorkspaceEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.WorkspaceMapper;
import com.example.optimization_algorithm_backend.module.auth.service.CurrentUserService;
import com.example.optimization_algorithm_backend.module.workspace.dto.CreateWorkspaceRequest;
import com.example.optimization_algorithm_backend.module.workspace.dto.WorkspaceQueryRequest;
import com.example.optimization_algorithm_backend.module.workspace.service.impl.WorkspaceAppServiceImpl;
import com.example.optimization_algorithm_backend.module.workspace.vo.WorkspaceVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.ObjectProvider;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkspaceAppServiceImplTest {

    @Mock
    private WorkspaceMapper workspaceMapper;

    @Mock
    private ObjectProvider<WorkspaceMapper> workspaceMapperProvider;

    @Mock
    private CurrentUserService currentUserService;

    private WorkspaceAppServiceImpl workspaceAppService;

    @BeforeEach
    void setUp() {
        when(workspaceMapperProvider.getIfAvailable()).thenReturn(workspaceMapper);
        workspaceAppService = new WorkspaceAppServiceImpl(workspaceMapperProvider, currentUserService);
    }

    @Test
    void shouldRejectDuplicateWorkspaceNameWhenCreatingWorkspace() {
        CreateWorkspaceRequest request = new CreateWorkspaceRequest();
        request.setName("默认工作空间");

        when(currentUserService.getCurrentUserId()).thenReturn(2L);
        when(workspaceMapper.selectCount(ArgumentMatchers.any())).thenReturn(1L);

        BusinessException exception = Assertions.assertThrows(BusinessException.class,
                () -> workspaceAppService.createWorkspace(request));

        Assertions.assertEquals(ErrorCode.CONFLICT.getCode(), exception.getCode());
    }

    @Test
    void shouldReturnForbiddenWhenUserReadsOtherUsersWorkspace() {
        WorkspaceEntity workspace = new WorkspaceEntity();
        workspace.setId(10L);
        workspace.setOwnerUserId(1L);
        workspace.setName("admin-space");

        when(workspaceMapper.selectById(10L)).thenReturn(workspace);
        when(currentUserService.isAdmin()).thenReturn(false);
        when(currentUserService.getCurrentUserId()).thenReturn(2L);

        BusinessException exception = Assertions.assertThrows(BusinessException.class,
                () -> workspaceAppService.getWorkspace(10L));

        Assertions.assertEquals(ErrorCode.FORBIDDEN.getCode(), exception.getCode());
    }

    @Test
    void shouldListCurrentUsersWorkspaces() {
        WorkspaceEntity workspace = new WorkspaceEntity();
        workspace.setId(3L);
        workspace.setOwnerUserId(2L);
        workspace.setName("user-space");
        workspace.setStatus(1);

        Page<WorkspaceEntity> resultPage = new Page<>(1, 10);
        resultPage.setRecords(Collections.singletonList(workspace));
        resultPage.setTotal(1L);

        WorkspaceQueryRequest request = new WorkspaceQueryRequest();

        when(currentUserService.isAdmin()).thenReturn(false);
        when(currentUserService.getCurrentUserId()).thenReturn(2L);
        when(workspaceMapper.selectPage(ArgumentMatchers.any(Page.class), ArgumentMatchers.any()))
                .thenReturn(resultPage);

        PageResult<WorkspaceVO> result = workspaceAppService.listWorkspaces(request);

        Assertions.assertEquals(1L, result.getTotal());
        Assertions.assertEquals(1, result.getRecords().size());
        Assertions.assertEquals(2L, result.getRecords().get(0).getOwnerUserId());
        verify(workspaceMapper).selectPage(ArgumentMatchers.any(Page.class), ArgumentMatchers.any());
    }
}
