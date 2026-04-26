package com.example.optimization_algorithm_backend.workspace;

import com.example.optimization_algorithm_backend.common.exception.BusinessException;
import com.example.optimization_algorithm_backend.common.response.ErrorCode;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.WorkspaceEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.WorkspaceMapper;
import com.example.optimization_algorithm_backend.module.auth.service.CurrentUserService;
import com.example.optimization_algorithm_backend.module.workspace.dto.CreateWorkspaceRequest;
import com.example.optimization_algorithm_backend.module.workspace.dto.UpdateWorkspaceRequest;
import com.example.optimization_algorithm_backend.module.workspace.service.impl.WorkspaceAppServiceImpl;
import com.example.optimization_algorithm_backend.module.workspace.vo.WorkspaceVO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkspaceAppServiceCrudTest {

    @Mock
    private ObjectProvider<WorkspaceMapper> workspaceMapperProvider;
    @Mock
    private WorkspaceMapper workspaceMapper;
    @Mock
    private CurrentUserService currentUserService;

    private WorkspaceAppServiceImpl workspaceAppService;

    @BeforeEach
    void setUp() {
        when(workspaceMapperProvider.getIfAvailable()).thenReturn(workspaceMapper);
        workspaceAppService = new WorkspaceAppServiceImpl(workspaceMapperProvider, currentUserService);
    }

    @Test
    void shouldCreateWorkspaceSuccessfully() {
        CreateWorkspaceRequest request = new CreateWorkspaceRequest();
        request.setName("ws-a");
        request.setDescription("d1");
        when(currentUserService.getCurrentUserId()).thenReturn(9L);
        when(workspaceMapper.selectCount(any())).thenReturn(0L);
        when(workspaceMapper.insert(any())).thenAnswer(invocation -> {
            WorkspaceEntity entity = invocation.getArgument(0);
            entity.setId(101L);
            return 1;
        });

        WorkspaceVO vo = workspaceAppService.createWorkspace(request);
        Assertions.assertEquals(101L, vo.getId());
        Assertions.assertEquals("ws-a", vo.getName());
        Assertions.assertEquals(9L, vo.getOwnerUserId());
    }

    @Test
    void shouldUpdateWorkspaceSuccessfully() {
        WorkspaceEntity existing = new WorkspaceEntity();
        existing.setId(11L);
        existing.setOwnerUserId(9L);
        existing.setName("old");
        existing.setStatus(1);
        when(workspaceMapper.selectById(11L)).thenReturn(existing);
        when(currentUserService.isAdmin()).thenReturn(false);
        when(currentUserService.getCurrentUserId()).thenReturn(9L);
        when(workspaceMapper.selectCount(any())).thenReturn(0L);

        UpdateWorkspaceRequest request = new UpdateWorkspaceRequest();
        request.setName("new");
        request.setDescription("new-desc");
        request.setStatus(1);

        WorkspaceVO vo = workspaceAppService.updateWorkspace(11L, request);
        Assertions.assertEquals("new", vo.getName());
        Assertions.assertEquals("new-desc", vo.getDescription());
    }

    @Test
    void shouldNotFindWorkspaceAfterDelete() {
        WorkspaceEntity existing = new WorkspaceEntity();
        existing.setId(11L);
        existing.setOwnerUserId(9L);
        existing.setName("to-del");
        when(workspaceMapper.selectById(11L)).thenReturn(existing).thenReturn(null);
        when(currentUserService.isAdmin()).thenReturn(false);
        when(currentUserService.getCurrentUserId()).thenReturn(9L);
        when(workspaceMapper.deleteById(11L)).thenReturn(1);

        boolean deleted = workspaceAppService.deleteWorkspace(11L);
        Assertions.assertTrue(deleted);

        BusinessException ex = Assertions.assertThrows(BusinessException.class,
                () -> workspaceAppService.getWorkspace(11L));
        Assertions.assertEquals(ErrorCode.RESOURCE_NOT_FOUND.getCode(), ex.getCode());
    }
}
