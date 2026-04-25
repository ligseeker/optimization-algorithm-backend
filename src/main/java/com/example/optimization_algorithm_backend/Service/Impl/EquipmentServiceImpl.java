package com.example.optimization_algorithm_backend.Service.Impl;

import com.example.optimization_algorithm_backend.Service.EquipmentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;


@Service
public class EquipmentServiceImpl implements EquipmentService {
    @Value("${path.historyPath}")
    String historyPath;
    @Value("${path.equipmentPath}")
    String equipmentPath;

    @Override
    public void addEquipment(String equipmentName, String color, String example, String description) {
        String filePath = historyPath + example + ".yaml";
        Map<String, Object> data = readData(filePath);
        List<Map<String, Object>> equipments = (List<Map<String, Object>>) data.get("Equipments");


        // 创建新的装备对象
        Map<String, Object> newEquipment = new HashMap<>();
        newEquipment.put("name", equipmentName);
        newEquipment.put("color", color);
        newEquipment.put("nodes", new ArrayList<String>());
        newEquipment.put("description", description);
        newEquipment.put("imagePath", "");

        // 添加新装备
        equipments.add(newEquipment);
        data.put("Equipments", equipments);
        saveData(data, filePath);
    }

    @Override
    public void renameEquipment(String equipmentName, String newName, String example) {
        String filePath = historyPath + example + ".yaml";
        Map<String, Object> data = readData(filePath);
        List<Map<String, Object>> equipments = (List<Map<String, Object>>) data.get("Equipments");

        // 查找并修改装备名称
        for (Map<String, Object> equipment : equipments) {
            if (equipment.get("name").equals(equipmentName)) {
                equipment.put("name", newName);
                break;
            }
        }
        // 查找装备名为equipmentName的节点，将其装备名字改为newName
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) data.get("ProcessNodes");
        for (Map<String, Object> node : nodes) {
            if (node.get("equipmentName").equals(equipmentName)) {
                node.put("equipmentName", newName);
            }
        }
        data.put("Equipments", equipments);
        data.put("ProcessNodes", nodes);
        saveData(data, filePath);
    }

    @Override
    public void deleteEquipment(String equipmentName, String example) {
        String filePath = historyPath + example + ".yaml";
        Map<String, Object> data = readData(filePath);
        List<Map<String, Object>> equipments = (List<Map<String, Object>>) data.get("Equipments");

        // 查找并删除装备
        equipments.removeIf(equipment -> equipment.get("name").equals(equipmentName));
        data.put("Equipments", equipments);
        // 查找装备名为equipmentName的节点，将其装备名字改为""
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) data.get("ProcessNodes");
        for (Map<String, Object> node : nodes) {
            if (node.get("equipmentName").equals(equipmentName)) {
                node.put("equipmentName", "");
            }
        }
        data.put("ProcessNodes", nodes);
        saveData(data, filePath);

    }

    @Override
    public boolean findEquipment(String equipmentName, String example) {
        String filePath = historyPath + example + ".yaml";
        Map<String, Object> data = readData(filePath);
        List<Map<String, Object>> equipments = (List<Map<String, Object>>) data.get("Equipments");

        // 查找装备
        if (equipments == null) {
            return false;
        }
        for (Map<String, Object> equipment : equipments) {
            Object name = equipment.get("name");
            if (name != null && name.equals(equipmentName)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void modifyEquipmentColor(String equipmentName, String color, String example) {
        String filePath = historyPath + example + ".yaml";
        Map<String, Object> data = readData(filePath);
        List<Map<String, Object>> equipments = (List<Map<String, Object>>) data.get("Equipments");

        // 查找并修改装备颜色
        for (Map<String, Object> equipment : equipments) {
            if (equipment.get("name").equals(equipmentName)) {
                equipment.put("color", color);
                data.put("Equipments", equipments);
                saveData(data, filePath);
                break;
            }
        }
    }

    @Override
    public Map<String, Object> getEquipment(String equipmentName, String example) {
        String filePath = historyPath + example + ".yaml";
        Map<String, Object> data = readData(filePath);
        List<Map<String, Object>> equipments = (List<Map<String, Object>>) data.get("Equipments");

        // 查找装备
        for (Map<String, Object> equipment : equipments) {
            if (equipment.get("name").equals(equipmentName)) {
                return equipment;
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> getAllEquipments(String filePath) {
        Map<String, Object> res = readData(filePath);
        return res;
    }

    @Override
    public void uploadEquipmentImage(String equipmentName, String example, String fileName) {
        String filePath = historyPath + example + ".yaml";
        Map<String, Object> data = readData(filePath);
        List<Map<String, Object>> equipments = (List<Map<String, Object>>) data.get("Equipments");

        // 查找并修改装备图片路径
        for (Map<String, Object> equipment : equipments) {
            if (equipment.get("name").equals(equipmentName)) {
                // 如果图片存在则删除
                String imagePath = (String) equipment.get("imagePath");
                if (!imagePath.isEmpty()) {
                    try{
                        File file = new File(equipmentPath + imagePath);
                        if (file.exists()) {
                            file.delete();
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                equipment.put("imagePath", fileName);
                data.put("Equipments", equipments);
                saveData(data, filePath);
                break;
            }
        }
    }

    @Override
    public void modifyEquipmentDescription(String equipmentName, String description, String example) {
        String filePath = historyPath + example + ".yaml";
        Map<String, Object> data = readData(filePath);
        List<Map<String, Object>> equipments = (List<Map<String, Object>>) data.get("Equipments");

        // 查找并修改装备描述
        for (Map<String, Object> equipment : equipments) {
            if (equipment.get("name").equals(equipmentName)) {
                equipment.put("description", description);
                data.put("Equipments", equipments);
                saveData(data, filePath);
                break;
            }
        }
    }

    public Map<String, Object> readData(String path) {
        Map<String, Object> res = new HashMap<>();
        try {
            InputStream input = new FileInputStream(path);
            Yaml yaml = new Yaml();
            res = yaml.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public void saveData(Map<String, Object> data, String path) {
        // 创建DumperOptions对象
        DumperOptions options = new DumperOptions();
        // 设置流样式为BLOCK，表示使用标准格式
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        // 设置缩进宽度为 2 个空格
        options.setIndent(2);
        // 设置短横线前的缩进宽度为 2 个空格
        options.setIndicatorIndent(2);
        // 设置子元素相对于其父元素的缩进宽度
        options.setIndentWithIndicator(true);
        // 将DumperOptions传递给Yaml对象
        Yaml yaml = new Yaml(options);

//        // 对Equipments条目重新排序
//        List<Map<String, Object>> equipments = (List<Map<String, Object>>) data.get("Equipments");
//        List<Map<String, Object>> sortedEquipments = new ArrayList<>();
//
//        for (Map<String, Object> equipment : equipments) {
//            Map<String, Object> sortedEquipment = new LinkedHashMap<>();
//            if (equipment.containsKey("name")) {
//                sortedEquipment.put("name", equipment.get("name"));
//            }
//            if (equipment.containsKey("color")) {
//                sortedEquipment.put("color", equipment.get("color"));
//            }
//            if (equipment.containsKey("nodes")) {
//                sortedEquipment.put("nodes", equipment.get("nodes"));
//            }
//            sortedEquipments.add(sortedEquipment);
//        }
//
//        // 替换原始的Equipments列表
//        data.put("Equipments", sortedEquipments);

        // 保存数据
        try (FileWriter writer = new FileWriter(path)) {
            yaml.dump(data, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
