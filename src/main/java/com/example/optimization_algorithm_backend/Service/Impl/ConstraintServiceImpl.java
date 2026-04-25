package com.example.optimization_algorithm_backend.Service.Impl;


import com.example.optimization_algorithm_backend.Service.ConstraintService;
import com.example.optimization_algorithm_backend.algorithm.model.Constant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.*;

@Service
public class ConstraintServiceImpl implements ConstraintService {
    @Value("${path.historyPath}")
    String historyPath;
    @Override
    public void addConstraint(String nodeId1, String nodeId2, Constant type, String example) {
        String path = historyPath + example + ".yaml";
        Map<String, Object> data = readData(path);
        List<Map<String, Object>> constraints = (List<Map<String, Object>>) data.get("ConstraintConditions");

        // 创建新的约束对象
        Map<String, Object> newConstraint = new LinkedHashMap<>();
        newConstraint.put("conditionID", nodeId1 + nodeId2);
        newConstraint.put("conditionType", type.name());
        newConstraint.put("conditionDescription", "This is a constraint between " + nodeId1 + " and " + nodeId2);
        newConstraint.put("nodeID1", nodeId1);
        newConstraint.put("nodeID2", nodeId2);

        // 检查是否已经存在该约束
        for (Map<String, Object> constraint : constraints) {
            if (constraint.get("conditionID").equals(nodeId1 + nodeId2) && constraint.get("conditionType").equals(type.name())) {
                System.out.println("Constraint already exists");
                return;
            }
        }

        // 添加新约束
        constraints.add(newConstraint);
        data.put("ConstraintConditions", constraints);
        saveData(data, path);
    }

    @Override
    public void deleteConstraint(String nodeId1, String nodeId2, Constant type, String example) {
        String path = historyPath + example + ".yaml";
        Map<String, Object> data = readData(path);
        List<Map<String, Object>> constraintList = (List<Map<String, Object>>) data.get("ConstraintConditions");

        // 查找并删除约束
        constraintList.removeIf(constraint ->
                constraint.get("nodeID1").equals(nodeId1) &&
                        constraint.get("nodeID2").equals(nodeId2) &&
                        constraint.get("conditionType").equals(type.name())
        );
        data.put("ConstraintConditions", constraintList);
        saveData(data, path);

    }

    @Override
    public void modifyConstraint(String nodeId1, String nodeId2, Constant type, String example) {
        String path = historyPath + example + ".yaml";
        Map<String, Object> data = readData(path);
        List<Map<String, Object>> constraints = (List<Map<String, Object>>) data.get("ConstraintConditions");

        // 查找并修改约束
        for (Map<String, Object> constraint : constraints) {
            if (constraint.get("nodeID1").equals(nodeId1) && constraint.get("nodeID2").equals(nodeId2)) {
                constraint.put("conditionType", type.name());
                data.put("ConstraintConditions", constraints);
                saveData(data, path);
            }
        }

    }

    @Override
    public boolean findConstraint(String nodeId1, String nodeId2, String example) {
        String path = historyPath + example + ".yaml";
        Map<String, Object> data = readData(path);
        List<Map<String, Object>> constraints = (List<Map<String, Object>>) data.get("ConstraintConditions");

        // 查找约束
        for (Map<String, Object> constraint : constraints) {
            if (constraint.get("nodeID1").equals(nodeId1) && constraint.get("nodeID2").equals(nodeId2)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean findNode(String nodeId, String example) {
        String path = historyPath + example + ".yaml";
        Map<String, Object> data = readData(path);
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) data.get("ProcessNodes");

        // 查找节点
        for (Map<String, Object> node : nodes) {
            if (node.get("nodeID").equals(nodeId)) {
                return true;
            }
        }

        return false;
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

    // 保存修改后的数据回到 input.yaml 文件 (标准格式)
    public void saveData(Map<String, Object> data, String path) {
        // 确保 ConstraintConditions 中的每个条目都使用 LinkedHashMap 以保持顺序
        List<Map<String, Object>> constraints = (List<Map<String, Object>>) data.get("ConstraintConditions");
        if (constraints != null) {
            // 使用 LinkedHashMap 确保字段顺序
            for (Map<String, Object> constraint : constraints) {
                Map<String, Object> sortedConstraint = new LinkedHashMap<>();
                sortedConstraint.put("conditionID", constraint.get("conditionID"));
                sortedConstraint.put("conditionType", constraint.get("conditionType"));
                sortedConstraint.put("conditionDescription", constraint.get("conditionDescription"));
                sortedConstraint.put("nodeID1", constraint.get("nodeID1"));
                sortedConstraint.put("nodeID2", constraint.get("nodeID2"));
            }
            data.put("ConstraintConditions", constraints);
        }

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

        try (FileWriter writer = new FileWriter(path)) {
            yaml.dump(data, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
