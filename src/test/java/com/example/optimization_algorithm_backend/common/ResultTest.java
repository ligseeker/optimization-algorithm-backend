package com.example.optimization_algorithm_backend.common;

import com.example.optimization_algorithm_backend.common.response.ErrorCode;
import com.example.optimization_algorithm_backend.common.response.PageResult;
import com.example.optimization_algorithm_backend.common.response.Result;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ResultTest {

    @Test
    void shouldBuildSuccessResult() {
        Result<String> result = Result.success("ok");

        assertEquals(ErrorCode.SUCCESS.getCode(), result.getCode());
        assertEquals(ErrorCode.SUCCESS.getMessage(), result.getMessage());
        assertEquals("ok", result.getData());
    }

    @Test
    void shouldBuildFailureResultFromErrorCode() {
        Result<Void> result = Result.fail(ErrorCode.PARAM_INVALID);

        assertEquals(ErrorCode.PARAM_INVALID.getCode(), result.getCode());
        assertEquals(ErrorCode.PARAM_INVALID.getMessage(), result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void shouldBuildPageResult() {
        PageResult<String> pageResult = PageResult.of(Arrays.asList("a", "b"), 1L, 10L, 2L);

        assertEquals(1L, pageResult.getPageNo());
        assertEquals(10L, pageResult.getPageSize());
        assertEquals(2L, pageResult.getTotal());
        assertEquals(2, pageResult.getRecords().size());
    }
}
