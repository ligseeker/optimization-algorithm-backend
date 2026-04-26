package com.example.optimization_algorithm_backend.module.yaml.vo;

import java.util.ArrayList;
import java.util.List;

public class ImportErrorReport {

    private Integer totalErrors;
    private List<ImportErrorItem> errors;

    public ImportErrorReport() {
        this.totalErrors = 0;
        this.errors = new ArrayList<>();
    }

    public ImportErrorReport(Integer totalErrors, List<ImportErrorItem> errors) {
        this.totalErrors = totalErrors;
        this.errors = errors;
    }

    public Integer getTotalErrors() {
        return totalErrors;
    }

    public void setTotalErrors(Integer totalErrors) {
        this.totalErrors = totalErrors;
    }

    public List<ImportErrorItem> getErrors() {
        return errors;
    }

    public void setErrors(List<ImportErrorItem> errors) {
        this.errors = errors;
    }
}
