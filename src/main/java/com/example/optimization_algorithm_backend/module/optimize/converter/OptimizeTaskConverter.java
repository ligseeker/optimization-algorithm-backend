package com.example.optimization_algorithm_backend.module.optimize.converter;

import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.OptimizeResultEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.OptimizeTaskEntity;
import com.example.optimization_algorithm_backend.module.optimize.vo.OptimizeResultVO;
import com.example.optimization_algorithm_backend.module.optimize.vo.OptimizeTaskSubmitVO;
import com.example.optimization_algorithm_backend.module.optimize.vo.OptimizeTaskVO;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class OptimizeTaskConverter {

    private OptimizeTaskConverter() {
    }

    public static OptimizeTaskSubmitVO toSubmitVO(OptimizeTaskEntity task) {
        OptimizeTaskSubmitVO vo = new OptimizeTaskSubmitVO();
        vo.setTaskId(task.getId());
        vo.setTaskNo(task.getTaskNo());
        vo.setTaskStatus(task.getTaskStatus());
        return vo;
    }

    public static OptimizeTaskVO toTaskVO(OptimizeTaskEntity task) {
        OptimizeTaskVO vo = new OptimizeTaskVO();
        vo.setId(task.getId());
        vo.setTaskNo(task.getTaskNo());
        vo.setWorkspaceId(task.getWorkspaceId());
        vo.setGraphId(task.getGraphId());
        vo.setUserId(task.getUserId());
        vo.setAlgorithmType(task.getAlgorithmType());
        vo.setAlgorithmMode(task.getAlgorithmMode());
        vo.setTimeWeight(task.getTimeWeight());
        vo.setPrecisionWeight(task.getPrecisionWeight());
        vo.setCostWeight(task.getCostWeight());
        vo.setTaskStatus(task.getTaskStatus());
        vo.setRetryCount(task.getRetryCount());
        vo.setMaxRetryCount(task.getMaxRetryCount());
        vo.setQueueTime(task.getQueueTime());
        vo.setStartedAt(task.getStartedAt());
        vo.setFinishedAt(task.getFinishedAt());
        vo.setErrorCode(task.getErrorCode());
        vo.setErrorMessage(task.getErrorMessage());
        vo.setResultId(task.getResultId());
        vo.setCreatedAt(task.getCreatedAt());
        vo.setUpdatedAt(task.getUpdatedAt());
        return vo;
    }

    public static OptimizeResultVO toResultVO(OptimizeResultEntity result, ObjectMapper objectMapper) {
        OptimizeResultVO vo = new OptimizeResultVO();
        vo.setId(result.getId());
        vo.setTaskId(result.getTaskId());
        vo.setWorkspaceId(result.getWorkspaceId());
        vo.setSourceGraphId(result.getSourceGraphId());
        vo.setResultName(result.getResultName());
        vo.setResultGraph(parseJson(result.getResultGraphJson(), objectMapper));
        vo.setDiff(parseJson(result.getDiffJson(), objectMapper));
        vo.setMapCode(result.getMapCode());
        vo.setTotalTimeBefore(result.getTotalTimeBefore());
        vo.setTotalPrecisionBefore(result.getTotalPrecisionBefore());
        vo.setTotalCostBefore(result.getTotalCostBefore());
        vo.setTotalTimeAfter(result.getTotalTimeAfter());
        vo.setTotalPrecisionAfter(result.getTotalPrecisionAfter());
        vo.setTotalCostAfter(result.getTotalCostAfter());
        vo.setScoreRatio(result.getScoreRatio());
        vo.setCreatedAt(result.getCreatedAt());
        return vo;
    }

    private static Object parseJson(String raw, ObjectMapper objectMapper) {
        if (raw == null) {
            return null;
        }
        try {
            return objectMapper.readValue(raw, Object.class);
        } catch (Exception ignore) {
            return raw;
        }
    }
}
