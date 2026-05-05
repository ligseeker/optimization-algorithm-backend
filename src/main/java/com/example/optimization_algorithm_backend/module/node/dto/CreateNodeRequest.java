package com.example.optimization_algorithm_backend.module.node.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public class CreateNodeRequest {

    @Schema(description = "节点编码，同图唯一", example = "A1")
    @NotBlank(message = "nodeCode不能为空")
    @Size(max = 64, message = "nodeCode长度不能超过64位")
    private String nodeCode;

    @Schema(description = "节点名称", example = "工序A")
    @Size(max = 128, message = "nodeName长度不能超过128位")
    private String nodeName;

    @Schema(description = "节点描述", example = "原始处理节点")
    @Size(max = 255, message = "nodeDescription长度不能超过255位")
    private String nodeDescription;

    @Schema(description = "关联装备ID", example = "1001")
    private Long equipmentId;

    @Schema(description = "时间成本", example = "10")
    @Min(value = 0, message = "timeCost不能小于0")
    private Integer timeCost;

    @Schema(description = "精度值", example = "0.9500")
    @DecimalMin(value = "0", message = "precisionValue不能小于0")
    @DecimalMax(value = "1", message = "precisionValue不能大于1")
    private BigDecimal precisionValue;

    @Schema(description = "成本值", example = "30")
    @Min(value = 0, message = "costValue不能小于0")
    private Integer costValue;

    @Schema(description = "排序号", example = "1")
    @Min(value = 0, message = "sortNo不能小于0")
    private Integer sortNo;

    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeDescription() {
        return nodeDescription;
    }

    public void setNodeDescription(String nodeDescription) {
        this.nodeDescription = nodeDescription;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Integer getTimeCost() {
        return timeCost;
    }

    public void setTimeCost(Integer timeCost) {
        this.timeCost = timeCost;
    }

    public BigDecimal getPrecisionValue() {
        return precisionValue;
    }

    public void setPrecisionValue(BigDecimal precisionValue) {
        this.precisionValue = precisionValue;
    }

    public Integer getCostValue() {
        return costValue;
    }

    public void setCostValue(Integer costValue) {
        this.costValue = costValue;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }
}
