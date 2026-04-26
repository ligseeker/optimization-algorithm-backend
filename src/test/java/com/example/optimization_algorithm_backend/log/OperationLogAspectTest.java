package com.example.optimization_algorithm_backend.log;

import com.example.optimization_algorithm_backend.common.response.Result;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.OperationLogEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.OperationLogMapper;
import com.example.optimization_algorithm_backend.module.log.annotation.OperationLog;
import com.example.optimization_algorithm_backend.module.log.aspect.OperationLogAspect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperationLogAspectTest {

    @Mock
    private ObjectProvider<OperationLogMapper> mapperProvider;
    @Mock
    private OperationLogMapper operationLogMapper;

    private OperationLogAspect operationLogAspect;

    @BeforeEach
    void setUp() {
        when(mapperProvider.getIfAvailable()).thenReturn(operationLogMapper);
        operationLogAspect = new OperationLogAspect(mapperProvider);
    }

    @Test
    void shouldWriteSuccessOperationLog() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/test/success");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        DummyService target = new DummyService();
        AspectJProxyFactory proxyFactory = new AspectJProxyFactory(target);
        proxyFactory.addAspect(operationLogAspect);
        DummyService proxy = proxyFactory.getProxy();

        Result<Long> result = proxy.success(99L);
        Assertions.assertEquals(0, result.getCode());

        ArgumentCaptor<OperationLogEntity> captor = ArgumentCaptor.forClass(OperationLogEntity.class);
        verify(operationLogMapper).insert(captor.capture());
        OperationLogEntity entity = captor.getValue();
        Assertions.assertEquals("UNIT_SUCCESS", entity.getOperationType());
        Assertions.assertEquals("UNIT", entity.getObjectType());
        Assertions.assertEquals(99L, entity.getObjectId());
        Assertions.assertEquals("POST", entity.getRequestMethod());
        Assertions.assertEquals("/api/test/success", entity.getRequestUri());
        Assertions.assertEquals(1, entity.getSuccessFlag());
    }

    @Test
    void shouldWriteFailedOperationLog() {
        MockHttpServletRequest request = new MockHttpServletRequest("DELETE", "/api/test/fail");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        DummyService target = new DummyService();
        AspectJProxyFactory proxyFactory = new AspectJProxyFactory(target);
        proxyFactory.addAspect(operationLogAspect);
        DummyService proxy = proxyFactory.getProxy();

        Assertions.assertThrows(IllegalStateException.class, proxy::fail);

        ArgumentCaptor<OperationLogEntity> captor = ArgumentCaptor.forClass(OperationLogEntity.class);
        verify(operationLogMapper).insert(captor.capture());
        OperationLogEntity entity = captor.getValue();
        Assertions.assertEquals("UNIT_FAIL", entity.getOperationType());
        Assertions.assertEquals(0, entity.getSuccessFlag());
        Assertions.assertNotNull(entity.getErrorMessage());
        Assertions.assertNull(entity.getRequestParams());
    }

    static class DummyService {
        @OperationLog(operationType = "UNIT_SUCCESS", objectType = "UNIT", objectIdParam = "id")
        public Result<Long> success(Long id) {
            return Result.success(id);
        }

        @OperationLog(operationType = "UNIT_FAIL", objectType = "UNIT")
        public Result<Void> fail() {
            throw new IllegalStateException("failed");
        }
    }
}
