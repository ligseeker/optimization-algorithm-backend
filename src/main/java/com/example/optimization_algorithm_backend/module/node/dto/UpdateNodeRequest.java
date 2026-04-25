package com.example.optimization_algorithm_backend.module.node.dto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public class UpdateNodeRequest {

    @NotBlank(message = "nodeCode不能为空")
    @Size(max = 64, message = "nodeCode长度不能超过64位")
    private String nodeCode;

    @Size(max = 128, message = "nodeName长度不能超过128位")
    private String nodeName;

    @Size(max = 255, message = "nodeDescription长度不能超过255位")
    private String nodeDescription;

    private Long equipmentId;

    @Min(value = 0, message = "timeCost不能小于0")
    private Integer timeCost;

    @DecimalMin(value = "0", message = "precisionValue不能小于0")
    private BigDecimal precisionValue;

    @Min(value = 0, message = "costValue不能小于0")
    private Integer costValue;

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
