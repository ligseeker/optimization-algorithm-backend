package com.example.optimization_algorithm_backend.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;

@TableName("optimize_result")
public class OptimizeResultEntity extends BaseEntity {

    @TableField("task_id")
    private Long taskId;

    @TableField("workspace_id")
    private Long workspaceId;

    @TableField("source_graph_id")
    private Long sourceGraphId;

    @TableField("result_name")
    private String resultName;

    @TableField("result_graph_json")
    private String resultGraphJson;

    @TableField("diff_json")
    private String diffJson;

    @TableField("map_code")
    private String mapCode;

    @TableField("total_time_before")
    private Integer totalTimeBefore;

    @TableField("total_precision_before")
    private BigDecimal totalPrecisionBefore;

    @TableField("total_cost_before")
    private Integer totalCostBefore;

    @TableField("total_time_after")
    private Integer totalTimeAfter;

    @TableField("total_precision_after")
    private BigDecimal totalPrecisionAfter;

    @TableField("total_cost_after")
    private Integer totalCostAfter;

    @TableField("score_ratio")
    private BigDecimal scoreRatio;

    @TableField("yaml_export_path")
    private String yamlExportPath;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public Long getSourceGraphId() {
        return sourceGraphId;
    }

    public void setSourceGraphId(Long sourceGraphId) {
        this.sourceGraphId = sourceGraphId;
    }

    public String getResultName() {
        return resultName;
    }

    public void setResultName(String resultName) {
        this.resultName = resultName;
    }

    public String getResultGraphJson() {
        return resultGraphJson;
    }

    public void setResultGraphJson(String resultGraphJson) {
        this.resultGraphJson = resultGraphJson;
    }

    public String getDiffJson() {
        return diffJson;
    }

    public void setDiffJson(String diffJson) {
        this.diffJson = diffJson;
    }

    public String getMapCode() {
        return mapCode;
    }

    public void setMapCode(String mapCode) {
        this.mapCode = mapCode;
    }

    public Integer getTotalTimeBefore() {
        return totalTimeBefore;
    }

    public void setTotalTimeBefore(Integer totalTimeBefore) {
        this.totalTimeBefore = totalTimeBefore;
    }

    public BigDecimal getTotalPrecisionBefore() {
        return totalPrecisionBefore;
    }

    public void setTotalPrecisionBefore(BigDecimal totalPrecisionBefore) {
        this.totalPrecisionBefore = totalPrecisionBefore;
    }

    public Integer getTotalCostBefore() {
        return totalCostBefore;
    }

    public void setTotalCostBefore(Integer totalCostBefore) {
        this.totalCostBefore = totalCostBefore;
    }

    public Integer getTotalTimeAfter() {
        return totalTimeAfter;
    }

    public void setTotalTimeAfter(Integer totalTimeAfter) {
        this.totalTimeAfter = totalTimeAfter;
    }

    public BigDecimal getTotalPrecisionAfter() {
        return totalPrecisionAfter;
    }

    public void setTotalPrecisionAfter(BigDecimal totalPrecisionAfter) {
        this.totalPrecisionAfter = totalPrecisionAfter;
    }

    public Integer getTotalCostAfter() {
        return totalCostAfter;
    }

    public void setTotalCostAfter(Integer totalCostAfter) {
        this.totalCostAfter = totalCostAfter;
    }

    public BigDecimal getScoreRatio() {
        return scoreRatio;
    }

    public void setScoreRatio(BigDecimal scoreRatio) {
        this.scoreRatio = scoreRatio;
    }

    public String getYamlExportPath() {
        return yamlExportPath;
    }

    public void setYamlExportPath(String yamlExportPath) {
        this.yamlExportPath = yamlExportPath;
    }
}
