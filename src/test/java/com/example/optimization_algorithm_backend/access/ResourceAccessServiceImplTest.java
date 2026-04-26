package com.example.optimization_algorithm_backend.access;

import com.example.optimization_algorithm_backend.common.exception.BusinessException;
import com.example.optimization_algorithm_backend.common.response.ErrorCode;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.FlowGraphEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.WorkspaceEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.FlowGraphMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.WorkspaceMapper;
import com.example.optimization_algorithm_backend.module.auth.service.CurrentUserService;
import com.example.optimization_algorithm_backend.module.common.service.impl.ResourceAccessServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceAccessServiceImplTest {

    @Mock
    private ObjectProvider<WorkspaceMapper> workspaceMapperProvider;
    @Mock
    private ObjectProvider<FlowGraphMapper> flowGraphMapperProvider;
    @Mock
    private WorkspaceMapper workspaceMapper;
    @Mock
    private FlowGraphMapper flowGraphMapper;
    @Mock
    private CurrentUserService currentUserService;

    private ResourceAccessServiceImpl resourceAccessService;

    @BeforeEach
    void setUp() {
        lenient().when(workspaceMapperProvider.getIfAvailable()).thenReturn(workspaceMapper);
        lenient().when(flowGraphMapperProvider.getIfAvailable()).thenReturn(flowGraphMapper);
        resourceAccessService = new ResourceAccessServiceImpl(workspaceMapperProvider, flowGraphMapperProvider, currentUserService);
    }

    @Test
    void shouldAllowAdminAccessAnyWorkspace() {
        WorkspaceEntity workspace = new WorkspaceEntity();
        workspace.setId(1L);
        workspace.setOwnerUserId(2L);
        when(workspaceMapper.selectById(1L)).thenReturn(workspace);
        when(currentUserService.isAdmin()).thenReturn(true);

        WorkspaceEntity result = resourceAccessService.getAccessibleWorkspace(1L);
        Assertions.assertEquals(1L, result.getId());
    }

    @Test
    void shouldAllowUserAccessOwnWorkspace() {
        WorkspaceEntity workspace = new WorkspaceEntity();
        workspace.setId(1L);
        workspace.setOwnerUserId(3L);
        when(workspaceMapper.selectById(1L)).thenReturn(workspace);
        when(currentUserService.isAdmin()).thenReturn(false);
        when(currentUserService.getCurrentUserId()).thenReturn(3L);

        WorkspaceEntity result = resourceAccessService.getAccessibleWorkspace(1L);
        Assertions.assertEquals(1L, result.getId());
    }

    @Test
    void shouldRejectUserAccessOthersWorkspace() {
        WorkspaceEntity workspace = new WorkspaceEntity();
        workspace.setId(1L);
        workspace.setOwnerUserId(2L);
        when(workspaceMapper.selectById(1L)).thenReturn(workspace);
        when(currentUserService.isAdmin()).thenReturn(false);
        when(currentUserService.getCurrentUserId()).thenReturn(3L);

        BusinessException ex = Assertions.assertThrows(BusinessException.class,
                () -> resourceAccessService.getAccessibleWorkspace(1L));
        Assertions.assertEquals(ErrorCode.FORBIDDEN.getCode(), ex.getCode());
    }

    @Test
    void shouldRejectUserAccessOthersGraph() {
        FlowGraphEntity graph = new FlowGraphEntity();
        graph.setId(11L);
        graph.setWorkspaceId(1L);
        when(flowGraphMapper.selectById(11L)).thenReturn(graph);

        WorkspaceEntity workspace = new WorkspaceEntity();
        workspace.setId(1L);
        workspace.setOwnerUserId(2L);
        when(workspaceMapper.selectById(1L)).thenReturn(workspace);

        when(currentUserService.isAdmin()).thenReturn(false);
        when(currentUserService.getCurrentUserId()).thenReturn(5L);

        BusinessException ex = Assertions.assertThrows(BusinessException.class,
                () -> resourceAccessService.getAccessibleGraph(11L));
        Assertions.assertEquals(ErrorCode.FORBIDDEN.getCode(), ex.getCode());
    }
}
