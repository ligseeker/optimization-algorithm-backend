package com.example.optimization_algorithm_backend.Controller;

import com.example.optimization_algorithm_backend.Service.AlgorithmService;
import com.example.optimization_algorithm_backend.Service.PathService;
import com.example.optimization_algorithm_backend.algorithm.model.ProcessMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class PathController {

    @Autowired
    private PathService pathService;
    @Autowired
    private AlgorithmService algorithmService;
    @Value("${path.historyPath}")
    String historyPath;

    @PostMapping("/addPath")
    public Map<String, Object> addPath(@RequestParam Map<String, Object> path) {
        Map<String, Object> res = new HashMap<>();
        String nodeId1 = (String) path.get("nodeID1");
        String nodeId2 = (String) path.get("nodeID2");
        String example = (String) path.get("example");

        String srcPath = historyPath + example + ".yaml";
        File file = new File(srcPath);
        if(!file.exists()){
            res.put("code", 500);
            res.put("msg", "实例不存在");
            return res;
        }

        // 查找input.yaml中的Paths是否含有从nodeId1到nodeId2的路径，如果没有则添加，返回流程图的字符串信息
        // 如果有则返回错误信息
        if(nodeId1.equals(nodeId2)){
            res.put("code", 500);
            res.put("msg", "起点和终点相同");
        }else if(pathService.findPathInMap(nodeId1, nodeId2, example)){
            res.put("code", 500);
            res.put("msg", "路径已存在");
        }else{
            pathService.addPathInMap(nodeId1, nodeId2, example);
            String filePath = historyPath + example + ".yaml";
            ProcessMap map = algorithmService.pathToMap(filePath);
            String mapCode = algorithmService.getMapCode(map);
            res.put("mapCode", mapCode);
            res.put("code", 200);
            res.put("msg", "添加成功");
        }
        return res;
    }

    @PostMapping("/deletePath")
    public Map<String, Object> deletePath(@RequestParam Map<String, Object> path) {
        Map<String, Object> res = new HashMap<>();
        String nodeId1 = (String) path.get("nodeID1");
        String nodeId2 = (String) path.get("nodeID2");
        String example = (String) path.get("example");

        String srcPath = historyPath + example + ".yaml";
        File file = new File(srcPath);
        if(!file.exists()){
            res.put("code", 500);
            res.put("msg", "实例不存在");
            return res;
        }

        // 查找input.yaml中的Paths是否含有从nodeId1到nodeId2的路径，如果有则删除，返回流程图的字符串信息
        // 如果没有则返回错误信息
        if(nodeId1.equals(nodeId2)){
            res.put("code", 500);
            res.put("msg", "起点和终点相同");
        }else if(pathService.findPathInMap(nodeId1, nodeId2, example)){
            pathService.deletePathInMap(nodeId1, nodeId2, example);
            String filePath = historyPath + example + ".yaml";
            ProcessMap map = algorithmService.pathToMap(filePath);
            String mapCode = algorithmService.getMapCode(map);
            res.put("mapCode", mapCode);
            res.put("code", 200);
            res.put("msg", "删除成功");
        }else {
            res.put("code", 500);
            res.put("msg", "路径不存在");
        }
        return res;
    }

    // 返回与当前节点相连的节点列表
    @PostMapping("/getPathNodeList")
    public Map<String, Object> getPathNodeList(@RequestParam Map<String, Object> path){
        Map<String, Object> res = new HashMap<>();
        ArrayList<String> pathNodeList;
        String nodeID = (String) path.get("nodeID");
        String example = (String) path.get("example");

        String srcPath = historyPath + example + ".yaml";
        File file = new File(srcPath);
        if(!file.exists()){
            res.put("code", 500);
            res.put("msg", "实例不存在");
            return res;
        }
        pathNodeList = pathService.findPathNodeList(nodeID, example);
        res.put("code", 200);
        res.put("msg", "查询成功");
        res.put("data", pathNodeList);

        return res;
    }
}
