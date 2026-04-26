package com.example.optimization_algorithm_backend.module.log.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.example.optimization_algorithm_backend.common.response.Result;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.OperationLogEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.OperationLogMapper;
import com.example.optimization_algorithm_backend.module.log.annotation.OperationLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Aspect
@Component
public class OperationLogAspect {

    private static final Logger log = LoggerFactory.getLogger(OperationLogAspect.class);

    private final ObjectProvider<OperationLogMapper> operationLogMapperProvider;

    public OperationLogAspect(ObjectProvider<OperationLogMapper> operationLogMapperProvider) {
        this.operationLogMapperProvider = operationLogMapperProvider;
    }

    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = null;
        Throwable throwable = null;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable ex) {
            throwable = ex;
            throw ex;
        } finally {
            long cost = System.currentTimeMillis() - start;
            persistOperationLog(joinPoint, operationLog, result, throwable, cost);
        }
    }

    private void persistOperationLog(ProceedingJoinPoint joinPoint,
                                     OperationLog operationLog,
                                     Object result,
                                     Throwable throwable,
                                     long costTime) {
        try {
            OperationLogMapper mapper = operationLogMapperProvider.getIfAvailable();
            if (mapper == null) {
                return;
            }

            HttpServletRequest request = null;
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                request = attributes.getRequest();
            }

            OperationLogEntity entity = new OperationLogEntity();
            entity.setUserId(resolveCurrentUserId());
            entity.setOperationType(operationLog.operationType());
            entity.setObjectType(operationLog.objectType());
            entity.setObjectId(resolveObjectId(joinPoint, operationLog.objectIdParam(), result));
            entity.setRequestMethod(request == null ? null : request.getMethod());
            entity.setRequestUri(request == null ? null : request.getRequestURI());
            entity.setRequestParams(null);
            entity.setCostTimeMs((int) costTime);
            if (throwable == null) {
                entity.setSuccessFlag(1);
                entity.setErrorMessage(null);
                if (result instanceof Result) {
                    entity.setResponseCode(((Result<?>) result).getCode());
                } else {
                    entity.setResponseCode(0);
                }
            } else {
                entity.setSuccessFlag(0);
                entity.setResponseCode(500001);
                entity.setErrorMessage(truncate(throwable.getMessage(), 1000));
            }
            mapper.insert(entity);
        } catch (Exception ex) {
            log.warn("操作日志写入失败: {}", ex.getMessage());
        }
    }

    private Long resolveCurrentUserId() {
        try {
            if (StpUtil.isLogin()) {
                return Long.valueOf(StpUtil.getLoginIdAsString());
            }
        } catch (Exception ignore) {
            return null;
        }
        return null;
    }

    private Long resolveObjectId(ProceedingJoinPoint joinPoint, String objectIdParam, Object result) {
        if (objectIdParam != null && !objectIdParam.trim().isEmpty()) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] parameterNames = signature.getParameterNames();
            Object[] args = joinPoint.getArgs();
            if (parameterNames != null) {
                for (int i = 0; i < parameterNames.length; i++) {
                    if (objectIdParam.equals(parameterNames[i]) && args[i] instanceof Number) {
                        return ((Number) args[i]).longValue();
                    }
                }
            }
        }
        if (result instanceof Result) {
            Object data = ((Result<?>) result).getData();
            Long fromData = tryResolveIdFromResultData(data);
            if (fromData != null) {
                return fromData;
            }
        }
        return null;
    }

    private Long tryResolveIdFromResultData(Object data) {
        if (data == null) {
            return null;
        }
        if (data instanceof Number) {
            return ((Number) data).longValue();
        }
        String[] getterNames = {"getId", "getTaskId", "getGraphId", "getWorkspaceId"};
        for (String getterName : getterNames) {
            try {
                Method method = data.getClass().getMethod(getterName);
                Object id = method.invoke(data);
                if (id instanceof Number) {
                    return ((Number) id).longValue();
                }
            } catch (Exception ignore) {
                // ignore and continue
            }
        }
        return null;
    }

    private String truncate(String value, int maxLength) {
        String raw = value == null ? "unknown error" : value;
        if (raw.length() <= maxLength) {
            return raw;
        }
        return raw.substring(0, maxLength);
    }
}
