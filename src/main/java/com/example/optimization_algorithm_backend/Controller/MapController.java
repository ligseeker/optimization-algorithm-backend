package com.example.optimization_algorithm_backend.Controller;

import com.example.optimization_algorithm_backend.Service.AlgorithmService;
import com.example.optimization_algorithm_backend.algorithm.algorithm1.Algorithm1;
import com.example.optimization_algorithm_backend.algorithm.algorithm2.Algorithm2;
import com.example.optimization_algorithm_backend.algorithm.algorithm3.Algorithm3;
import com.example.optimization_algorithm_backend.algorithm.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


import static com.example.optimization_algorithm_backend.algorithm.Main.*;
import static com.example.optimization_algorithm_backend.algorithm.model.utils.getRandomString;

@RestController
@CrossOrigin(origins = "*")
public class MapController {

    @Autowired
    private AlgorithmService algorithmService;
    @Value("${path.historyPath}")
    String historyPath;
    @Value("${path.inputPath}")
    String inputPath;
    @Value("${path.outputPath}")
    String outputPath;

    @PostMapping("/getRandomMap")
    public Map<String, Object> getRandomMap(@RequestParam Map<String, Object> re_map) {
        Map<String, Object> res = new HashMap<>();
        String example = (String) re_map.get("example");
//        boolean randomFollowConstraint = re_map.get("follow").equals("true");

        try {
            InputInfo inputInfo1 = readData("src/main/resources/template2.yaml");
            ProcessMap mapTest = initMapTest(inputInfo1);

            int min, max, randomInt;
            Random random = new Random();

            // 随机生成各个节点的时间、成本、精度
//            for(MultiNode node: mapTest.getMultiNodes()){
//                min = 10;
//                max = 30;
//                randomInt = random.nextInt(max - min + 1) + min;
//                node.setTime(randomInt);
//                min = 1;
//                max = 5;
//                randomInt = random.nextInt(max - min + 1) + min;
//                node.setCost(randomInt);
//                node.setPrecision(1.0);
//            }

            // 随机生成承接约束条件
//            if(randomFollowConstraint){
//                for(ConstraintCondition cc: mapTest.getConstraintConditions()){
//                    if(cc.getConditionType()==Constant.FOLLOW){
//                        String start = GetRandomValidNode(mapTest.getProcessPaths(), "Aa", cc.getNodeID2());
//                        cc.setNodeID1(start);
//                        cc.setConditionID(start+cc.getNodeID2());
//                    }
//                }
//            }

            // 随机生成衔接约束条件
//            ArrayList<String> tempNodeList = new ArrayList<>();
//            for(MultiNode node: mapTest.getMultiNodes()){
//                if (node.getNodeID().charAt(1)!= 'h' && node.getNodeID().charAt(1)!= 'i'){
//                    tempNodeList.add(node.getNodeID());
//                }
//            }
//            max = tempNodeList.size()-1;
//            min = 0;
//            for(MultiNode node: mapTest.getMultiNodes()) {
//                if (node.getNodeID().charAt(1) == 'i') {
//                    randomInt = random.nextInt(max - min + 1) + min;
//                    String node1 = tempNodeList.get(randomInt);
//                    String node2 = node.getNodeID();
//                    ConstraintCondition cc = new ConstraintCondition(node1+node2, "This is a constraint", Constant.CONNECT, node1, node2);
//
//                    for(ConstraintCondition cc1: mapTest.getConstraintConditions()){
//                        if(cc1.getNodeID1().equals(node1) && cc1.getConditionType()==Constant.FOLLOW){
//                            cc1.setConditionID(node2+cc1.getNodeID2());
//                            cc1.setNodeID1(node2);
//                        }
//                    }
//                    mapTest.getConstraintConditions().add(cc);
//
//                    for(ProcessPath path: mapTest.getProcessPaths()){
//                        if(path.getStartNodeID().equals(node1)){
//                            path.setStartNodeID(node2);
//                        }
//                    }
//                    mapTest.getProcessPaths().add(new ProcessPath(utils.getRandomString(5), node1, node2));
//                    tempNodeList.remove(node1);
//                    max = tempNodeList.size()-1;
//                }
//            }

            // 随机生成同一约束条件的数值
            for (ConstraintCondition cc : mapTest.getConstraintConditions()) {
                if (cc.getConditionType() == Constant.SAME) {
                    for (MultiNode node1 : mapTest.getMultiNodes()) {
                        if (node1.getNodeID().equals(cc.getNodeID1())) {
                            min = 95;
                            max = 99;
                            randomInt = random.nextInt(max - min + 1) + min;
                            node1.setPrecision((double) randomInt / 100);
                            break;
                        }
                    }
                    for (MultiNode node1 : mapTest.getMultiNodes()) {
                        if (node1.getNodeID().equals(cc.getNodeID2())) {
                            min = 95;
                            max = 99;
                            randomInt = random.nextInt(max - min + 1) + min;
                            node1.setPrecision((double) randomInt / 100);
                            break;
                        }
                    }
                }
            }
            String path = historyPath + example + ".yaml";
            writeData(mapTest, path);
            res.put("code", 200);
            res.put("msg", "生成成功");
        } catch (Exception e) {
            res.put("code", 500);
            res.put("msg", "生成失败");
        }
        return res;
    }

    @PostMapping("/upload")
    public Map<String, Object> Upload(@RequestParam("file") MultipartFile file) {
        Map<String, Object> res = new HashMap<>();
        if (file.isEmpty()) {
            res.put("code", 500);
            res.put("msg", "文件为空");
            return res;
        }
        try {
            // 如果history目录不存在，创建目录
            File historyDir = new File(historyPath);
            if (!historyDir.exists()) {
                historyDir.mkdirs();
            }
            String filePath = historyPath + file.getOriginalFilename();
            String path = algorithmService.saveFile(file, filePath);
            System.out.println(path);
            ProcessMap oldMap = algorithmService.pathToMap(path);
            String mapCode = algorithmService.getMapCode(oldMap);

            Map<String, Object> value = new HashMap<>();
            res.put("value", value);
            res.put("code", 200);
            res.put("msg", "上传成功");
            res.put("mapCode", mapCode);
        } catch (Exception e) {
            e.printStackTrace();
            res.put("code", 500);
            res.put("msg", "上传失败");
        }
        return res;
    }

    @PostMapping("/initialize")
    public Map<String, Object> Initialize(@RequestParam Map<String, Object> re_map) {
        Map<String, Object> res = new HashMap<>();
        String example = (String) re_map.get("example");

        try {
            // 如果history目录不存在，创建目录
            File historyDir = new File(historyPath);
            if (!historyDir.exists()) {
                historyDir.mkdirs();
            }
            ArrayList<MultiNode> nodes = new ArrayList<>();
            LinkedList<ProcessPath> paths = new LinkedList<>();
            ArrayList<ConstraintCondition> ccs = new ArrayList<>();
            ArrayList<Equipment> equipments = new ArrayList<>();
            ProcessMap map = new ProcessMap(getRandomString(10), nodes, paths, ccs, equipments);
            String path = historyPath + example + ".yaml";
            writeData(map, path);
            Map<String, Object> value = new HashMap<>();
            value.put("time", 0);
            value.put("precision", 1.00);
            value.put("cost", 0);
            res.put("values", value);
            res.put("code", 200);
            res.put("msg", "初始化成功");
            res.put("mapCode", "");
        } catch (Exception e) {
            e.printStackTrace();
            res.put("code", 500);
            res.put("msg", "初始化失败");
        }
        return res;
    }

    @PostMapping("/save")
    public Map<String, Object> Save(@RequestParam Map<String, Object> re_map) {
        Map<String, Object> res = new HashMap<>();
        String example = (String) re_map.get("example");
        String from = historyPath + example + ".yaml";

        String path = historyPath + example + ".yaml";
        File file = new File(path);
        if (!file.exists()) {
            res.put("code", 500);
            res.put("msg", "实例不存在");
            return res;
        }

        // 如果input目录不存在，创建目录
        File inputDir = new File(inputPath);
        if (!inputDir.exists()) {
            inputDir.mkdirs();
        }

//        String str = utils.getRandomString(4);
        String str;
        // 查找input目录下含有多少个example-str.yaml文件
        File[] files = new File(inputPath).listFiles();
        int num = 1;
        if (files != null) {
            for (File f : files) {
                if (f.getName().contains(example + "-")) {
                    num++;
                }
            }
        }
        str = String.valueOf(num);
        String to = inputPath + example + "-" + str + ".yaml";
        // 判断to文件是否存在, 如果存在，num+1, 直到to文件不存在
        while (Paths.get(to).toFile().exists()) {
            num++;
            str = String.valueOf(num);
            to = inputPath + example + "-" + str + ".yaml";
        }

        // 将from文件复制到to文件
        try {
            ProcessMap map = algorithmService.pathToMap(from);
            writeData(map, to);
            res.put("code", 200);
            res.put("msg", "保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            res.put("code", 500);
            res.put("msg", "保存失败");
        }
        return res;
    }

    @PostMapping("/optimize")
    public Map<String, Object> Optimize(@RequestParam Map<String, Object> re_map) {
        Map<String, Object> res = new HashMap<>();
        String example = (String) re_map.get("example");
        int timeFactor = Integer.parseInt((String) re_map.get("timeFactor"));
        int precisionFactor = Integer.parseInt((String) re_map.get("precisionFactor"));
        int costFactor = Integer.parseInt((String) re_map.get("costFactor"));
        int algorithmType1 = Integer.parseInt((String) re_map.get("algorithmType1"));
        int algorithmType2 = Integer.parseInt((String) re_map.get("algorithmType2"));
        try {
            String path = inputPath + example + ".yaml";
            if (!Paths.get(path).toFile().exists()) {
                res.put("code", 500);
                res.put("msg", "实例不存在");
                return res;
            }
            ProcessMap oldMap = algorithmService.pathToMap(path);

            // 检查流程图是否符合约束
            ConstraintCondition cc = algorithmService.checkMap(oldMap);
            if (cc != null) {
                res.put("code", 500);
                res.put("cc", cc);
                res.put("msg", "优化失败，约束条件不满足");
                return res;
            }
            int[] factors = new int[3];
            factors[0] = timeFactor;
            factors[1] = precisionFactor;
            factors[2] = costFactor;
            ProcessMap newMap;
            Map<String, Object> values;
            if (algorithmType1 == 0 && (algorithmType2 >= 0 && algorithmType2 <= 2)) {
                Algorithm1 algorithm = new Algorithm1();
                algorithm.initAlgorithm(oldMap);
                newMap = algorithmService.optimizeMap1(oldMap, example, algorithm, factors, algorithmType1, algorithmType2);
                values = algorithm.getValue(newMap);
            } else if (algorithmType1 == 1 && (algorithmType2 >= 0 && algorithmType2 <= 2)) {
                Algorithm2 algorithm = new Algorithm2();
                algorithm.initAlgorithm(oldMap);
                newMap = algorithmService.optimizeMap2(oldMap, example, algorithm, factors, algorithmType1, algorithmType2);
                values = algorithm.getValue(newMap);
            } else if (algorithmType1 == 2 && (algorithmType2 >= 0 && algorithmType2 <= 2)) {
                Algorithm3 algorithm = new Algorithm3();
                algorithm.initAlgorithm(oldMap);
                newMap = algorithmService.optimizeMap3(oldMap, example, algorithm, factors, algorithmType1, algorithmType2);
                values = algorithm.getValue(newMap);
            } else {
                res.put("code", 500);
                res.put("msg", "优化失败，算法不存在");
                return res;
            }
            Map<String, Object> ans = compareMapPaths(oldMap.getProcessPaths(), newMap.getProcessPaths());
            String mapCode = WriteMapCode(newMap, ans);
            int oldTime = (int) ((Map<String, Object>) values.get("oldValue")).get("time");
            double oldPrecision = (double) ((Map<String, Object>) values.get("oldValue")).get("precision");
            int oldCost = (int) ((Map<String, Object>) values.get("oldValue")).get("cost");
            int newTime = (int) ((Map<String, Object>) values.get("newValue")).get("time");
            double newPrecision = (double) ((Map<String, Object>) values.get("newValue")).get("precision");
            int newCost = (int) ((Map<String, Object>) values.get("newValue")).get("cost");
            double factor1 = (double) timeFactor / (timeFactor + precisionFactor + costFactor);
            double factor2 = (double) precisionFactor / (timeFactor + precisionFactor + costFactor);
            double factor3 = (double) costFactor / (timeFactor + precisionFactor + costFactor);
            double temp = (double) (oldTime - newTime) / oldTime * factor1 + (newPrecision - oldPrecision) * factor2 + (double) (oldCost - newCost) / oldCost * factor3;
            res.put("ratio", temp);
            res.put("values", values);
            res.put("code", 200);
            res.put("msg", "优化成功");
            res.put("mapCode", mapCode);
        } catch (Exception e) {
            e.printStackTrace();
            res.put("code", 500);
            res.put("msg", "优化失败");
        }
        return res;
    }

    @GetMapping("/download")//没有参数，返回temp文件夹下的output.yaml文件，不能返回map
    public ResponseEntity<Resource> Download(@RequestParam Map<String, Object> re_map) {
        try {
            String example = (String) re_map.get("example");
            String path = outputPath + example + ".yaml";
            Path filePath = Paths.get(path);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_PLAIN)
                        .body(resource);
            } else {
                throw new RuntimeException("Could not find the file!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error downloading the file: " + e.getMessage());
        }
    }

    // 获取模板文件
    @GetMapping("/getTemplate")
    public ResponseEntity<Resource> GetTemplate() {
        try {
            ClassPathResource resource = new ClassPathResource("templateFile.yaml");
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_PLAIN)
                        .body(resource);
            } else {
                throw new RuntimeException("Could not find the file!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error downloading the file: " + e.getMessage());
        }
    }
}

