package com.example.optimization_algorithm_backend.module.constraint.support;

import com.example.optimization_algorithm_backend.algorithm.model.Constant;
import com.example.optimization_algorithm_backend.common.exception.BusinessException;
import com.example.optimization_algorithm_backend.common.response.ErrorCode;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public final class ConstraintTypeSupport {

    private static final Set<Constant> ALLOWED_CONSTANTS = Collections.unmodifiableSet(new LinkedHashSet<>(
            Arrays.asList(Constant.CONNECT, Constant.SAME, Constant.FOLLOW, Constant.CONTAIN, Constant.CALL, Constant.PARTICIPATE)
    ));
    private static final Set<String> ALLOWED_CODES = buildAllowedCodes();
    private static final String ALLOWED_TYPES_MESSAGE = String.join(", ", ALLOWED_CODES);

    private ConstraintTypeSupport() {
    }

    public static boolean isAllowed(Constant constant) {
        return constant != null && ALLOWED_CONSTANTS.contains(constant);
    }

    public static String normalizeRequestType(String rawType) {
        if (!StringUtils.hasText(rawType)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "conditionType不能为空");
        }
        String normalized = rawType.trim().toUpperCase(Locale.ROOT);
        if (!ALLOWED_CODES.contains(normalized)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "conditionType仅支持: " + ALLOWED_TYPES_MESSAGE);
        }
        return normalized;
    }

    public static Constant parseForExport(String rawType) {
        if (!StringUtils.hasText(rawType)) {
            throw new BusinessException(ErrorCode.CONFLICT, "流程图包含空的约束类型，无法导出YAML");
        }
        String normalized = rawType.trim().toUpperCase(Locale.ROOT);
        Constant constant;
        try {
            constant = Constant.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(ErrorCode.CONFLICT, "流程图包含非法约束类型，无法导出YAML: " + rawType.trim());
        }
        if (!ALLOWED_CONSTANTS.contains(constant)) {
            throw new BusinessException(ErrorCode.CONFLICT, "流程图包含非法约束类型，无法导出YAML: " + normalized);
        }
        return constant;
    }

    public static String allowedTypesMessage() {
        return ALLOWED_TYPES_MESSAGE;
    }

    private static Set<String> buildAllowedCodes() {
        LinkedHashSet<String> codes = new LinkedHashSet<>();
        for (Constant constant : ALLOWED_CONSTANTS) {
            codes.add(constant.name());
        }
        return Collections.unmodifiableSet(codes);
    }
}
