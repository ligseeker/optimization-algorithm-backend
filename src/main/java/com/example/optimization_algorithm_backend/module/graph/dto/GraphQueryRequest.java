package com.example.optimization_algorithm_backend.module.graph.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

public class GraphQueryRequest {

    @Schema(description = "页码", example = "1")
    @Min(value = 1, message = "页码必须大于等于1")
    private Long pageNo;

    @Schema(description = "每页条数", example = "10")
    @Min(value = 1, message = "每页条数必须大于等于1")
    @Max(value = 100, message = "每页条数不能超过100")
    private Long pageSize;

    @Schema(description = "关键字（流程图名称模糊匹配）", example = "装配")
    @Size(max = 128, message = "关键字长度不能超过128位")
    private String keyword;

    public Long getPageNo() {
        return pageNo == null ? 1L : pageNo;
    }

    public void setPageNo(Long pageNo) {
        this.pageNo = pageNo;
    }

    public Long getPageSize() {
        return pageSize == null ? 10L : pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
