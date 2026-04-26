package com.example.optimization_algorithm_backend.module.path.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class PathQueryRequest {

    @Schema(description = "页码", example = "1")
    @Min(value = 1, message = "页码必须大于等于1")
    private Long pageNo;

    @Schema(description = "每页条数", example = "20")
    @Min(value = 1, message = "每页条数必须大于等于1")
    @Max(value = 100, message = "每页条数不能超过100")
    private Long pageSize;

    public Long getPageNo() {
        return pageNo == null ? 1L : pageNo;
    }

    public void setPageNo(Long pageNo) {
        this.pageNo = pageNo;
    }

    public Long getPageSize() {
        return pageSize == null ? 20L : pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }
}
