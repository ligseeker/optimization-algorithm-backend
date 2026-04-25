package com.example.optimization_algorithm_backend.Service.Impl;

import com.example.optimization_algorithm_backend.Service.PathService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PathServiceImpl implements PathService {
    @Value("${path.historyPath}")
    String historyPath;
    @Override
    public void addPathInMap(String nodeId1, String nodeId2, String example) {
        String path = historyPath + example + ".yaml";
        Map<String, Object> data = readData(path);
        List<Map<String, String>> pathList = (List<Map<String, String>>) data.get("Paths");

        // 创建新的路径对象
        Map<String, String> newPath = new HashMap<>();
        newPath.put("from", nodeId1);
        newPath.put("to", nodeId2);

        // 添加新路径
        pathList.add(newPath);
        data.put("Paths", pathList);
        saveData(data, path);

    }

    @Override
    public void deletePathInMap(String nodeId1, String nodeId2, String example) {
        String path = historyPath + example + ".yaml";
        Map<String, Object> data = readData(path);
        List<Map<String, String>> pathList = (List<Map<String, String>>) data.get("Paths");

        // 查找并删除路径
        pathList.removeIf(p -> p.get("from").equals(nodeId1) && p.get("to").equals(nodeId2));
        data.put("Paths", pathList);
        saveData(data, path);

    }
    @Override
    public boolean findPathInMap(String nodeId1, String nodeId2, String example) {
        String path = historyPath + example + ".yaml";
        Map<String, Object> data = readData(path);
        List<Map<String, String>> pathList = (List<Map<String, String>>) data.get("Paths");

        // 查找路径
        for (Map<String, String> p : pathList) {
            if (p.get("from").equals(nodeId1) && p.get("to").equals(nodeId2)) {
                return true;
            }
        }

        return false;
    }
    // 返回与当前节点相连的节点列表
    @Override
    public ArrayList<String> findPathNodeList(String nodeID, String example) {
        ArrayList<String> res = new ArrayList<>();

        String path = historyPath + example + ".yaml";
        Map<String, Object> data = readData(path);
        List<Map<String, String>> pathList = (List<Map<String, String>>) data.get("Paths");

        for(Map<String, String> p : pathList){
            if(p.get("from").equals(nodeID)){
                res.add(p.get("to"));
            }
        }
        return res;
    }

    // 读取input.yaml文件
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
    // 保存修改后的数据回到input.yaml文件(紧凑格式)
//    public void saveData(Map<String, Object> data, String path) {
//        Yaml yaml = new Yaml();
//        try (FileWriter writer = new FileWriter(path)) {
//            yaml.dump(data, writer);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    // 保存修改后的数据回到input.yaml文件(标准格式)
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

        try (FileWriter writer = new FileWriter(path)) {
            yaml.dump(data, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
