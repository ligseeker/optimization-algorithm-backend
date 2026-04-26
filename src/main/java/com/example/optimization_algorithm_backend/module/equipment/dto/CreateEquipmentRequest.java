package com.example.optimization_algorithm_backend.module.equipment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CreateEquipmentRequest {

    @Schema(description = "装备名称", example = "equipment-01")
    @NotBlank(message = "设备名称不能为空")
    @Size(max = 128, message = "设备名称长度不能超过128位")
    private String name;

    @Schema(description = "装备描述", example = "高精度设备")
    @Size(max = 500, message = "设备描述长度不能超过500位")
    private String description;

    @Schema(description = "颜色", example = "#1677ff")
    @Size(max = 32, message = "color长度不能超过32位")
    private String color;

    @Schema(description = "图片路径", example = "equipment/20260426/a.png")
    @Size(max = 255, message = "imagePath长度不能超过255位")
    private String imagePath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
