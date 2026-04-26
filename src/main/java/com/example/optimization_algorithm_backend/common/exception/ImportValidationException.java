package com.example.optimization_algorithm_backend.common.exception;

import com.example.optimization_algorithm_backend.module.yaml.vo.ImportErrorReport;

public class ImportValidationException extends RuntimeException {

    private final ImportErrorReport errorReport;

    public ImportValidationException(String message, ImportErrorReport errorReport) {
        super(message);
        this.errorReport = errorReport;
    }

    public ImportErrorReport getErrorReport() {
        return errorReport;
    }
}
