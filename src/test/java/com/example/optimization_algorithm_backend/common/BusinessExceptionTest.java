package com.example.optimization_algorithm_backend.common;

import com.example.optimization_algorithm_backend.common.exception.BusinessException;
import com.example.optimization_algorithm_backend.common.response.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BusinessExceptionTest {

    @Test
    void shouldExposeErrorCodeAndMessage() {
        BusinessException exception = new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "流程图不存在");

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND.getCode(), exception.getCode());
        assertEquals("流程图不存在", exception.getMessage());
    }
}
