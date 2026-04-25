package com.example.optimization_algorithm_backend.common.response;

public enum ErrorCode {

    SUCCESS(0, "success"),
    PARAM_INVALID(400001, "参数错误"),
    UNAUTHORIZED(401001, "未登录"),
    FORBIDDEN(403001, "无权限"),
    RESOURCE_NOT_FOUND(404001, "资源不存在"),
    CONFLICT(409001, "数据冲突"),
    SYSTEM_ERROR(500001, "系统异常"),
    TASK_EXECUTION_FAILED(600001, "任务执行失败"),
    FILE_PARSE_FAILED(700001, "文件解析失败");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
