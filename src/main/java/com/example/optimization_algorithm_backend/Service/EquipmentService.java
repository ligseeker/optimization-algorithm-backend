package com.example.optimization_algorithm_backend.Service;

import java.util.Map;

public interface EquipmentService {
    // 添加装备
    void addEquipment(String equipmentName, String color, String example,String description);
    // 重命名装备
    void renameEquipment(String equipmentName, String newName, String example);
    // 删除装备
    void deleteEquipment(String equipmentName, String example);
    // 查找装备
    boolean findEquipment(String equipmentName, String example);
    // 修改装备颜色
    void modifyEquipmentColor(String equipmentName, String color, String example);
    // 获取单个装备及节点
    Map<String, Object> getEquipment(String equipmentName, String example);
    // 获取所有装备及节点
    Map<String, Object> getAllEquipments(String srcPath);

    void uploadEquipmentImage(String equipmentName, String example, String fileName);

    void modifyEquipmentDescription(String equipmentName, String description, String example);
}
