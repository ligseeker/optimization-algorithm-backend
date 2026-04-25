package com.example.optimization_algorithm_backend.Service.Impl;

import com.example.optimization_algorithm_backend.Service.AlgorithmService;
import com.example.optimization_algorithm_backend.algorithm.algorithm1.Algorithm1;
import com.example.optimization_algorithm_backend.algorithm.algorithm2.Algorithm2;
import com.example.optimization_algorithm_backend.algorithm.algorithm3.Algorithm3;
import com.example.optimization_algorithm_backend.algorithm.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.example.optimization_algorithm_backend.algorithm.Main.*;
import static com.example.optimization_algorithm_backend.algorithm.Main.writeDataTempFile;

@Service
public class AlgorithmServiceImpl implements AlgorithmService {
    @Value("${path.outputPath}")
    String outputPath;
    @Value("${path.historyPath}")
    String historyPath;
    @Value("${path.inputPath}")
    String inputPath;
    @Value("${path.tempPath}")
    String tempPath;

    public ArrayList<ProcessMap> mapList = new ArrayList<>();
    public InputInfo inputInfo;
    public LinkedList<ProcessPath> oldPaths = new LinkedList<>();

    @Override
    public String saveFile(MultipartFile file, String filePath) {
        String res = "";
        String fileType = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
        System.out.println(fileType);
        if (fileType.equals(".yaml")) {
            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get(filePath);
                File history = new File(historyPath);
                if (!history.exists()) {
                    history.mkdirs();
                }
                File input = new File(inputPath);
                if (!input.exists()) {
                    input.mkdirs();
                }
                Files.write(path, bytes);
                res = path.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (fileType.equals(".json")){
            try{
                String content = new String(file.getBytes());
                InputInfo inputInfo1 = readData1(content);
                ProcessMap map = initMapTest(inputInfo1);
                String path = filePath.substring(0, filePath.lastIndexOf(".")) + ".yaml";
                writeData(map, path);
                res = path;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return res;
    }

    @Override
    public ProcessMap pathToMap(String path) {
        ProcessMap map0;
        inputInfo = readData(path);
        map0 = initMapTest(inputInfo);
        return map0;
    }

    @Override
    public ProcessMap optimizeMap1(ProcessMap map, String example, Algorithm1 algorithm, int[] factors, int x1, int x2) {
        File tempDir = new File(tempPath);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        File [] files = tempDir.listFiles();
        boolean flag = files.length >= 10;
        if(flag){
            Arrays.sort(files, Comparator.comparingLong(File::lastModified));
            boolean flag1 = files[9].getName().startsWith(x1 + "_" + x2);
            if(!flag1) {
                for (int i = 0; i < 5; i++) {
                    files[i].delete();
                }
            }
        }
        String[] filenames = {"0初始链路图", "1节点合并图", "2链路重构图", "3节点拆分图", "4优化结果图"};

        mapList.clear();
        mapList.add(map);
        writeDataTempFile(map, tempPath+ x1 +"_"+x2+"_"+filenames[0] + ".yaml");

        oldPaths.clear();
        for (ProcessPath path : map.getProcessPaths()) {
            ProcessPath temp = new ProcessPath(path.getPathID(), path.getStartNodeID(), path.getEndNodeID());
            oldPaths.add(temp);
        }
        ProcessMap map1 = utils.initProcessMap(inputInfo);
        mapList.add(map1);
        writeDataTempFile(map1, tempPath+ x1 +"_"+x2+"_"+filenames[1] + ".yaml");
        ProcessMap map2 = algorithm.getOptimizationMap(map1, x2);
        mapList.add(map2);
        writeDataTempFile(map2, tempPath+ x1 +"_"+x2+"_"+filenames[2] + ".yaml");
        ProcessMap map3 = utils.restoreProcessMap(map2);
        mapList.add(map3);
        writeDataTempFile(map3, tempPath+ x1 +"_"+x2+"_"+filenames[3] + ".yaml");
        ProcessMap map4 = algorithm.OptimizeMap(map3, factors, x2);
        mapList.add(map4);
        writeDataTempFile(map4, tempPath+ x1 +"_"+x2+"_"+filenames[4] + ".yaml");
        // 如果output目录不存在，创建目录
        File outputDir = new File(outputPath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        String path = outputPath + example + "_" + factors[0] + "_" + factors[1] + "_" + factors[2] + "_" + x1 + "_" + x2 + ".yaml";
        writeData(map4, path);
        return map4;
    }

    @Override
    public ProcessMap optimizeMap2(ProcessMap map, String example, Algorithm2 algorithm, int[] factors, int x1, int x2) {
        File tempDir = new File(tempPath);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        File [] files = tempDir.listFiles();
        boolean flag = files.length >= 10;
        if(flag){
            Arrays.sort(files, Comparator.comparingLong(File::lastModified));
            boolean flag1 = files[9].getName().startsWith(x1 + "_" + x2);
            if(!flag1) {
                for (int i = 0; i < 5; i++) {
                    files[i].delete();
                }
            }
        }
        String[] filenames = {"0初始链路图", "1节点合并图", "2链路重构图", "3节点拆分图", "4优化结果图"};

        mapList.clear();
        mapList.add(map);
        writeDataTempFile(map, tempPath+ x1 +"_"+x2+"_"+filenames[0] + ".yaml");
        oldPaths.clear();
        for (ProcessPath path : map.getProcessPaths()) {
            ProcessPath temp = new ProcessPath(path.getPathID(), path.getStartNodeID(), path.getEndNodeID());
            oldPaths.add(temp);
        }
        ProcessMap map1 = utils.initProcessMap(inputInfo);
        mapList.add(map1);
        writeDataTempFile(map1, tempPath+ x1 +"_"+x2+"_"+filenames[1] + ".yaml");
        ProcessMap map2 = algorithm.getOptimizationMap(map1, factors, x2);
        mapList.add(map2);
        writeDataTempFile(map2, tempPath+ x1 +"_"+x2+"_"+filenames[2] + ".yaml");
        ProcessMap map3 = utils.restoreProcessMap(map2);
        mapList.add(map3);
        writeDataTempFile(map3, tempPath+ x1 +"_"+x2+"_"+filenames[3] + ".yaml");
        ProcessMap map4 = algorithm.OptimizeMap(map3, factors, x2);
        mapList.add(map4);
        writeDataTempFile(map4, tempPath+ x1 +"_"+x2+"_"+filenames[4] + ".yaml");

        // 如果output目录不存在，创建目录
        File outputDir = new File(outputPath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        String path = outputPath + example + "_" + factors[0] + "_" + factors[1] + "_" + factors[2] + "_" + x1 + "_" + x2 + ".yaml";
        writeData(map4, path);
        return map4;
    }

    @Override
    public ProcessMap optimizeMap3(ProcessMap map, String example, Algorithm3 algorithm, int[] factors, int x1, int x2) {
        //如果temp文件夹不存在，创建目录
        File tempDir = new File(tempPath);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        File [] files = tempDir.listFiles();
        boolean flag = files.length >= 10;
        if(flag){
            Arrays.sort(files, Comparator.comparingLong(File::lastModified));
            boolean flag1 = files[9].getName().startsWith(x1 + "_" + x2);
            if(!flag1) {
                for (int i = 0; i < 5; i++) {
                    files[i].delete();
                }
            }
        }
        String[] filenames = {"0初始链路图", "1节点合并图", "2链路重构图", "3节点拆分图", "4优化结果图"};

        mapList.clear();
        mapList.add(map);
        writeDataTempFile(map, tempPath+ x1 +"_"+x2+"_"+filenames[0] + ".yaml");

        oldPaths.clear();
        for (ProcessPath path : map.getProcessPaths()) {
            ProcessPath temp = new ProcessPath(path.getPathID(), path.getStartNodeID(), path.getEndNodeID());
            oldPaths.add(temp);
        }
        ProcessMap map1 = utils.initProcessMap(inputInfo);
        mapList.add(map1);
        writeDataTempFile(map1, tempPath+ x1 +"_"+x2+"_"+filenames[1] + ".yaml");
        ProcessMap map2 = algorithm.getOptimizationMap(map1, factors, x2);
        mapList.add(map2);
        writeDataTempFile(map2, tempPath+ x1 +"_"+x2+"_"+filenames[2] + ".yaml");
        ProcessMap map3 = utils.restoreProcessMap(map2);
        mapList.add(map3);
        writeDataTempFile(map3, tempPath+ x1 +"_"+x2+"_"+filenames[3] + ".yaml");
        ProcessMap map4 = algorithm.OptimizeMap(map3, factors, x2);
        mapList.add(map4);
        writeDataTempFile(map4, tempPath+ x1 +"_"+x2+"_"+filenames[4] + ".yaml");

        // 如果output目录不存在，创建目录
        File outputDir = new File(outputPath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        String path = outputPath + example + "_" + factors[0] + "_" + factors[1] + "_" + factors[2] + "_" + x1 + "_" + x2 + ".yaml";
        writeData(map4, path);
        return map4;
    }

    @Override
    public ConstraintCondition checkMap(ProcessMap oldMap) {
        for(ConstraintCondition cc: oldMap.getConstraintConditions()){
            switch (cc.getConditionType()){
                case CONNECT:  // 衔接关系，两个节点之间必须有路径
                    boolean flag = false;
                    for(ProcessPath path: oldMap.getProcessPaths()){
                        if(path.getStartNodeID().equals(cc.getNodeID1()) && path.getEndNodeID().equals(cc.getNodeID2())){
                            flag = true;
                            break;
                        }
                    }
                    if(!flag)
                        return cc;
                    break;
                case FOLLOW:  // 承接关系，路径可达,A承接B，从A出发可以到达B
                    ArrayList<String> nodes = new ArrayList<>();
                    for(ProcessPath path: oldMap.getProcessPaths()){
                        if(!nodes.contains(path.getStartNodeID()))
                            nodes.add(path.getStartNodeID());
                        if(!nodes.contains(path.getEndNodeID()))
                            nodes.add(path.getEndNodeID());
                    }
                    if(nodes.contains(cc.getNodeID1()) && nodes.contains(cc.getNodeID2()))
                        if(!utils.canReach(oldMap.getProcessPaths(), cc.getNodeID1(), cc.getNodeID2()))
                            return cc;
                    break;
                case SAME:  // 同一关系，不需要检查
                    break;
                case CONTAIN:  // 包含关系，必须为同一装备，A包含B，A和B必须为同一装备
                    if(!utils.isSameEquipment(oldMap.getMultiNodes(), cc.getNodeID1(), cc.getNodeID2()))
                        return cc;
                    break;
                case CALL:  // 调用关系，路径可达,A调用B，从B出发可以到达A
                    ArrayList<String> nodes1 = new ArrayList<>();
                    for(ProcessPath path: oldMap.getProcessPaths()){
                        if(!nodes1.contains(path.getStartNodeID()))
                            nodes1.add(path.getStartNodeID());
                        if(!nodes1.contains(path.getEndNodeID()))
                            nodes1.add(path.getEndNodeID());
                    }
                    if(nodes1.contains(cc.getNodeID1()) && nodes1.contains(cc.getNodeID2()))
                        if(!utils.canReach(oldMap.getProcessPaths(), cc.getNodeID2(), cc.getNodeID1()))
                            return cc;
                    break;
                case NORMAL:  // 普通关系，不需要检查
                    break;
                case PARTICIPATE:  // 参与关系，必须跨装备，A参与B，A和B必须为不同装备
                    if(utils.isSameEquipment(oldMap.getMultiNodes(), cc.getNodeID1(), cc.getNodeID2()))
                        return cc;
                    break;
                default:
                    return cc;
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> mapToOutput(ProcessMap map4, Algorithm1 algorithm) {
        Map<String, Object> res = new HashMap<>();
        LinkedList<ProcessPath> newPaths = new LinkedList<>();
        for (ProcessPath path : map4.getProcessPaths()) {
            ProcessPath temp = new ProcessPath(path.getPathID(), path.getStartNodeID(), path.getEndNodeID());
            newPaths.add(temp);
        }
        Map<String, Object> ans = compareMapPaths(this.oldPaths, newPaths);
        String mapCode = WriteMapCode(map4, ans);
        res.put("mapCode", mapCode);

        ArrayList<Object> NodeList = new ArrayList<>();
        for (MultiNode n : map4.getMultiNodes()) {
            Map<String, Object> node = new HashMap<>();
            node.put("nodeID", n.getNodeID());
            node.put("nodeDescription", n.getNodeDescription());
            node.put("time", n.getTime());
            node.put("precision", n.getPrecision());
            node.put("cost", n.getCost());
            NodeList.add(node);
        }
        ArrayList<Object> ConstraintList = new ArrayList<>();
        for (ConstraintCondition c : map4.getConstraintConditions()) {
            Map<String, Object> cc = new HashMap<>();
            cc.put("conditionID", c.getConditionID());
            cc.put("conditionType", c.getConditionType().toString());
            cc.put("conditionDescription", c.getConditionDescription());
            cc.put("nodeID1", c.getNodeID1());
            cc.put("nodeID2", c.getNodeID2());
            ConstraintList.add(cc);
        }
        ArrayList<Object> PathList = new ArrayList<>();
        for (ProcessPath p : map4.getProcessPaths()) {
            Map<String, Object> path = new HashMap<>();
            path.put("from", p.getStartNodeID());
            path.put("to", p.getEndNodeID());
            PathList.add(path);
        }
        res.put("ProcessNodes", NodeList);
        res.put("ConstraintConditions", ConstraintList);
        res.put("Paths", PathList);

        Map<String, Object> values = algorithm.getValue(map4);
        res.put("values", values);
        return res;
    }

    @Override
    public ProcessMap inputToMap(Map<String, Object> input) {
        ProcessMap processMap = new ProcessMap();
        //  将输入数据转换为流程图信息，并返回流程图信息

        return processMap;
    }

    public String getMapCode(ProcessMap map) {
        StringBuilder mapCode = new StringBuilder("flowchart LR\n");
        for (MultiNode node : map.getMultiNodes()) {
            mapCode.append(node.getNodeID()).append("\n");
        }
        for (ProcessPath path : map.getProcessPaths()) {
            mapCode.append(path.getStartNodeID()).append(" --> ").append(path.getEndNodeID()).append("\n");
        }
        return mapCode.toString();
    }
}
