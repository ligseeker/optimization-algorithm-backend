package com.example.optimization_algorithm_backend.module.optimize.constant;

public final class OptimizeTaskStatus {

    public static final String PENDING = "PENDING";     // 待执行
    public static final String RUNNING = "RUNNING";     // 运行中
    public static final String SUCCESS = "SUCCESS";     // 成功
    public static final String FAILED = "FAILED";       // 失败

    private OptimizeTaskStatus() {
    }
}
