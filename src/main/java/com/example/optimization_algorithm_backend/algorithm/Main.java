package com.example.optimization_algorithm_backend.algorithm;

import com.example.optimization_algorithm_backend.algorithm.algorithm1.Algorithm1;
import com.example.optimization_algorithm_backend.algorithm.algorithm2.Algorithm2;
import com.example.optimization_algorithm_backend.algorithm.algorithm3.Algorithm3;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions;

import java.io.*;
import java.util.*;
import java.util.Random;

import com.example.optimization_algorithm_backend.algorithm.model.*;
import org.json.*;


public class Main {
    public static void main(String[] args) {
        ArrayList<ProcessMap> mapList = new ArrayList<>();

//        String jsonStr = "";
//        try{
//            FileReader fileReader = new FileReader("src/main/java/com/example/optimization_algorithm_backend/algorithm/test.json");
//            BufferedReader bufferedReader = new BufferedReader(fileReader);
//            String line = null;
//            while((line = bufferedReader.readLine()) != null){
//                jsonStr += line;
//            }
//            } catch (IOException e) {
//            e.printStackTrace();
//        }
//        InputInfo inputInfo = readData1(jsonStr);
//        ProcessMap map0 = initMapTest(inputInfo);


//        InputInfo inputInfo1 = readData("src/main/java/com/example/optimization_algorithm_backend/algorithm/input.yaml");
//        ProcessMap mapTest = initMapTest(inputInfo1);
//        // 将mapTest转换成json格式
//        JSONStringer jsonStringer = new JSONStringer();
//        try {
//            jsonStringer.object();
//            jsonStringer.key("ProcessNodes").array();
//            for(MultiNode node: mapTest.getMultiNodes()){
//                jsonStringer.object();
//                jsonStringer.key("nodeID").value(node.getNodeID());
//                jsonStringer.key("nodeDescription").value(node.getNodeDescription());
//                jsonStringer.key("equipmentName").value(node.getEquipmentName());
//                jsonStringer.key("time").value(node.getTime());
//                jsonStringer.key("precision").value(node.getPrecision());
//                jsonStringer.key("cost").value(node.getCost());
//                jsonStringer.endObject();
//            }
//            jsonStringer.endArray();
//            jsonStringer.key("Paths").array();
//            for(ProcessPath path: mapTest.getProcessPaths()){
//                jsonStringer.object();
//                jsonStringer.key("from").value(path.getStartNodeID());
//                jsonStringer.key("to").value(path.getEndNodeID());
//                jsonStringer.endObject();
//            }
//            jsonStringer.endArray();
//            jsonStringer.key("ConstraintConditions").array();
//            for(ConstraintCondition cc: mapTest.getConstraintConditions()){
//                jsonStringer.object();
//                jsonStringer.key("conditionID").value(cc.getConditionID());
//                jsonStringer.key("conditionDescription").value(cc.getConditionDescription());
//                jsonStringer.key("conditionType").value(cc.getConditionType().toString());
//                jsonStringer.key("nodeID1").value(cc.getNodeID1());
//                jsonStringer.key("nodeID2").value(cc.getNodeID2());
//                jsonStringer.endObject();
//            }
//            jsonStringer.endArray();
//            jsonStringer.key("Equipments").array();
//            for(Equipment e: mapTest.getEquipments()){
//                jsonStringer.object();
//                jsonStringer.key("name").value(e.getName());
//                jsonStringer.key("nodes").array();
//                for(String node: e.getNodes()){
//                    jsonStringer.value(node);
//                }
//                jsonStringer.endArray();
//                jsonStringer.key("color").value(e.getColor());
//                jsonStringer.key("description").value(e.getDescription());
//                jsonStringer.key("imagePath").value(e.getImagePath());
//                jsonStringer.endObject();
//            }
//            jsonStringer.endArray();
//            jsonStringer.endObject();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        String json = jsonStringer.toString();
//
//        // 将json写入到test.json文件中
//        try {
//            FileWriter fileWriter = new FileWriter("src/main/java/com/example/optimization_algorithm_backend/algorithm/test.json");
//            fileWriter.write(json);
//            fileWriter.flush();
//            fileWriter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        int x1 = 1, x2 = 2;
        ProcessMap result;

        InputInfo inputInfo = readData("src/main/java/com/example/optimization_algorithm_backend/algorithm/input-test.yaml");
        ProcessMap map0 = initMapTest(inputInfo);
        mapList.add(map0);
        writeData(map0, "src/main/java/com/example/optimization_algorithm_backend/algorithm/map0.yaml");  // map0表示节点合并前的链路结构
        LinkedList<ProcessPath> oldPaths = new LinkedList<>();
        for(ProcessPath path: map0.getProcessPaths()){
            ProcessPath temp = new ProcessPath(path.getPathID(), path.getStartNodeID(), path.getEndNodeID());
            oldPaths.add(temp);
        }
        ProcessMap map1 = utils.initProcessMap(inputInfo);  // 合并
        mapList.add(map1);

        if(x1 ==0){
            Algorithm1 algorithm1 = new Algorithm1();
            algorithm1.initAlgorithm(map1);
            writeData(map1, "src/main/java/com/example/optimization_algorithm_backend/algorithm/map1.yaml");
            ProcessMap map2 = algorithm1.getOptimizationMap(map1, x2);
            mapList.add(map2);
            writeData(map2, "src/main/java/com/example/optimization_algorithm_backend/algorithm/map2.yaml");  // map2表示链路结构优化后的链路结构
            ProcessMap map3 = utils.restoreProcessMap(map2);
            mapList.add(map3);
            writeData(map3, "src/main/java/com/example/optimization_algorithm_backend/algorithm/map3.yaml"); // map3表示节点拆分恢复后的链路结构
            ProcessMap map4 = algorithm1.OptimizeMap(map3, inputInfo.getCalculationFactor(), x2);
            mapList.add(map4);
            writeData(map4, "src/main/java/com/example/optimization_algorithm_backend/algorithm/map4.yaml");  // map4表示替换同一节点后的链路结构
            result = map4;
        } else if(x1 ==1){
            Algorithm2 algorithm2 = new Algorithm2();
            algorithm2.initAlgorithm(map1);
            writeData(map1, "src/main/java/com/example/optimization_algorithm_backend/algorithm/map1.yaml");
            ProcessMap map2 = algorithm2.getOptimizationMap(map1, inputInfo.getCalculationFactor(), x2);
            mapList.add(map2);
            writeData(map2, "src/main/java/com/example/optimization_algorithm_backend/algorithm/map2.yaml");  // map2表示链路结构优化后的链路结构
            ProcessMap map3 = utils.restoreProcessMap(map2);
            mapList.add(map3);
            writeData(map3, "src/main/java/com/example/optimization_algorithm_backend/algorithm/map3.yaml"); // map3表示节点拆分恢复后的链路结构
            ProcessMap map4 = algorithm2.OptimizeMap(map3, inputInfo.getCalculationFactor(), x2);
            mapList.add(map4);
            writeData(map4, "src/main/java/com/example/optimization_algorithm_backend/algorithm/map4.yaml");  // map4表示替换同一节点后的链路结构
            result = map4;
        } else if(x1 ==2){
            Algorithm3 algorithm3 = new Algorithm3();
            algorithm3.initAlgorithm(map1);
            writeData(map1, "src/main/java/com/example/optimization_algorithm_backend/algorithm/map1.yaml");
            ProcessMap map2 = algorithm3.getOptimizationMap(map1, inputInfo.getCalculationFactor(), x2);
            mapList.add(map2);
            writeData(map2, "src/main/java/com/example/optimization_algorithm_backend/algorithm/map2.yaml");  // map2表示链路结构优化后的链路结构
            ProcessMap map3 = utils.restoreProcessMap(map2);
            mapList.add(map3);
            writeData(map3, "src/main/java/com/example/optimization_algorithm_backend/algorithm/map3.yaml"); // map3表示节点拆分恢复后的链路结构
            ProcessMap map4 = algorithm3.OptimizeMap(map3, inputInfo.getCalculationFactor(), x2);
            mapList.add(map4);
            writeData(map4, "src/main/java/com/example/optimization_algorithm_backend/algorithm/map4.yaml");  // map4表示替换同一节点后的链路结构
            result = map4;
        } else{
            System.out.println("x1 error");
            return;
        }

//
//
//        Set<String> nodeSet = new HashSet<>();
//        for(ProcessPath path: map4.getProcessPaths()){
//            nodeSet.add(path.getStartNodeID());
//            nodeSet.add(path.getEndNodeID());
//        }
//        ArrayList<String> nodeHasNoPath = new ArrayList<>();
//        for(String node: nodeSet){
//            boolean flag = false;
//            for(ProcessPath path: map4.getProcessPaths()){
//                if(path.getStartNodeID().equals(node)){
//                    flag = true;
//                    break;
//                }
//            }
//            if(!flag){
//                nodeHasNoPath.add(node);
//            }
//        }

        LinkedList<ProcessPath> newPaths = new LinkedList<>();
        for(ProcessPath path: result.getProcessPaths()){
            ProcessPath temp = new ProcessPath(path.getPathID(), path.getStartNodeID(), path.getEndNodeID());
            newPaths.add(temp);
        }
        Map<String, Object> res = compareMapPaths(oldPaths, newPaths);
        String mapCode = WriteMapCode(result, res);
        writeData(result, "src/main/java/com/example/optimization_algorithm_backend/algorithm/output2.yaml");
        System.out.println("\n"+mapCode);
    }

    public static InputInfo readData1(String jsonStr){  // 从json文件中读取内容，解析成InputInfo
        ArrayList<ProcessNode> processNodes = new ArrayList<>();
        ArrayList<ConstraintCondition> constraintConditions = new ArrayList<>();
        LinkedList<ProcessPath> processPaths = new LinkedList<>();
        ArrayList<Equipment> equipments = new ArrayList<>();
        int[] calculationFactor = new int[3];
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray processNodesArray = jsonObject.getJSONArray("ProcessNodes");
            for(int i = 0; i < processNodesArray.length(); i++){
                JSONObject node = processNodesArray.getJSONObject(i);
                String nodeID = node.getString("nodeID");
                String nodeDescription = node.getString("nodeDescription");
                String equipmentName = node.getString("equipmentName");
                int time = node.getInt("time");
                double precision = node.getDouble("precision");
                int cost = node.getInt("cost");
                ProcessNode processNode = new ProcessNode(nodeID, nodeDescription, equipmentName, time, precision, cost);
                processNodes.add(processNode);
            }
            JSONArray constraintConditionsArray = jsonObject.getJSONArray("ConstraintConditions");
            for(int i = 0; i < constraintConditionsArray.length(); i++){
                JSONObject constraintCondition = constraintConditionsArray.getJSONObject(i);
                String conditionID = constraintCondition.getString("conditionID");
                String conditionDescription = constraintCondition.getString("conditionDescription");
                String conditionType = constraintCondition.getString("conditionType");
                String nodeID1 = constraintCondition.getString("nodeID1");
                String nodeID2 = constraintCondition.getString("nodeID2");
                ConstraintCondition constraintCondition1 = new ConstraintCondition(conditionID, conditionDescription, Constant.valueOf(conditionType), nodeID1, nodeID2);
                constraintConditions.add(constraintCondition1);
            }
            JSONArray processPathsArray = jsonObject.getJSONArray("Paths");
            for(int i = 0; i < processPathsArray.length(); i++){
                JSONObject processPath = processPathsArray.getJSONObject(i);
                String from = processPath.getString("from");
                String to = processPath.getString("to");
                ProcessPath processPath1 = new ProcessPath(utils.getRandomString(5), from, to);
                processPaths.add(processPath1);
            }
            JSONArray equipmentsArray = jsonObject.getJSONArray("Equipments");
            for(int i = 0; i < equipmentsArray.length(); i++){
                JSONObject equipment = equipmentsArray.getJSONObject(i);
                String name = equipment.getString("name");
                JSONArray nodes = equipment.getJSONArray("nodes");
                ArrayList<String> nodesList = new ArrayList<>();
                for(int j = 0; j < nodes.length(); j++){
                    nodesList.add(nodes.getString(j));
                }
                String color = equipment.getString("color");
                String description = equipment.getString("description");
                String imagePath = equipment.getString("imagePath");
                Equipment equipment1 = new Equipment(name, nodesList, color, description, imagePath);
                equipments.add(equipment1);
            }
            if(jsonObject.has("TimeFactor"))  calculationFactor[0] = jsonObject.getInt("TimeFactor");
            else calculationFactor[0] = 1;
            if(jsonObject.has("PrecisionFactor"))  calculationFactor[1] = jsonObject.getInt("PrecisionFactor");
            else calculationFactor[1] = 1;
            if(jsonObject.has("CostFactor"))  calculationFactor[2] = jsonObject.getInt("CostFactor");
            else calculationFactor[2] = 1;
            return new InputInfo(processNodes, constraintConditions, processPaths, equipments, calculationFactor);
        } catch (Exception e) {
            throw new RuntimeException(e);

        }
    }
    public static InputInfo readData(String fileName) {
        Yaml yaml = new Yaml();
        ArrayList<ProcessNode> processNodes = new ArrayList<>();
        ArrayList<ConstraintCondition> constraintConditions = new ArrayList<>();
        LinkedList<ProcessPath> processPaths = new LinkedList<>();
        ArrayList<Equipment> equipments = new ArrayList<>();
        int[] calculationFactor = new int[3];
        try {
            Map<String, Object> data = yaml.load(new FileReader(fileName));
            Object NodeList = data.get("ProcessNodes");
            for(int i = 0; i < ((ArrayList<?>) NodeList).size(); i++){
                Object node = ((ArrayList<?>) NodeList).get(i);
                String nodeID = (String) ((Map<?, ?>) node).get("nodeID");
                String nodeDescription = (String) ((Map<?, ?>) node).get("nodeDescription");
                String equipmentName;
                if(((Map<?, ?>) node).get("equipmentName") == null) equipmentName = "";
                else equipmentName = (String) ((Map<?, ?>) node).get("equipmentName");
                Object time = ((Map<?, ?>) node).get("time");
                Object precision = ((Map<?, ?>) node).get("precision");
                Object cost = ((Map<?, ?>) node).get("cost");
                ProcessNode processNode = new ProcessNode(nodeID, nodeDescription, equipmentName, (int) time, (double) precision, (int) cost);
                processNodes.add(processNode);
            }
            Object ConstraintList = data.get("ConstraintConditions");
            for(int i = 0; i < ((ArrayList<?>) ConstraintList).size(); i++){
                Object constraintCondition = ((ArrayList<?>) ConstraintList).get(i);
                String conditionID = (String) ((Map<?, ?>) constraintCondition).get("conditionID");
                String conditionDescription = (String) ((Map<?, ?>) constraintCondition).get("conditionDescription");
                String conditionType = (String) ((Map<?, ?>) constraintCondition).get("conditionType");
                String nodeID1 = (String) ((Map<?, ?>) constraintCondition).get("nodeID1");
                String nodeID2 = (String) ((Map<?, ?>) constraintCondition).get("nodeID2");
                ConstraintCondition constraintCondition1 = new ConstraintCondition(conditionID, conditionDescription, Constant.valueOf(conditionType), nodeID1, nodeID2);
                constraintConditions.add(constraintCondition1);
            }
            Object PathList = data.get("Paths");
            for(int i = 0; i < ((ArrayList<?>) PathList).size(); i++){
                String from = (String) ((Map<?, ?>) ((ArrayList<?>) PathList).get(i)).get("from");
                String to = (String) ((Map<?, ?>) ((ArrayList<?>) PathList).get(i)).get("to");
                ProcessPath processPath = new ProcessPath(utils.getRandomString(5), from, to);
                processPaths.add(processPath);
            }
            Object EquipmentList = data.get("Equipments");
            for(int i = 0; i < ((ArrayList<?>) EquipmentList).size(); i++){
                Object equipment = ((ArrayList<?>) EquipmentList).get(i);
                String name = (String) ((Map<?, ?>) equipment).get("name");
                ArrayList<String> nodes = (ArrayList<String>) ((Map<?, ?>) equipment).get("nodes");
                String color = (String) ((Map<?, ?>) equipment).get("color");
                String description = (String) ((Map<?, ?>) equipment).get("description");
                String imagePath = (String) ((Map<?, ?>) equipment).get("imagePath");
                Equipment equipment1 = new Equipment(name, nodes, color, description, imagePath);
                equipments.add(equipment1);
            }
            if(data.get("TimeFactor") == null)  calculationFactor[0] = 1;
            else calculationFactor[0] = (int) data.get("TimeFactor");
            if(data.get("PrecisionFactor") == null)  calculationFactor[1] = 1;
            else calculationFactor[1] = (int) data.get("PrecisionFactor");
            if(data.get("CostFactor") == null)  calculationFactor[2] = 1;
            else calculationFactor[2] = (int) data.get("CostFactor");
            return new InputInfo(processNodes, constraintConditions, processPaths, equipments, calculationFactor);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeData(ProcessMap map, String fileName){
        ArrayList<Object> NodeList = new ArrayList<>();
        for(MultiNode n : map.getMultiNodes()){
            Map<String, Object> node = new HashMap<>();
            node.put("nodeID", n.getNodeID());
            node.put("nodeDescription", n.getNodeDescription());
            node.put("equipmentName", n.getEquipmentName());
            node.put("time", n.getTime());
            node.put("precision", n.getPrecision());
            node.put("cost", n.getCost());
            NodeList.add(node);
        }
        ArrayList<Object> ConstraintList = new ArrayList<>();
        for(ConstraintCondition c : map.getConstraintConditions()){
            Map<String, Object> cc = new HashMap<>();
            cc.put("conditionID", c.getConditionID());
            cc.put("conditionType", c.getConditionType().toString());
            cc.put("conditionDescription", c.getConditionDescription());
            cc.put("nodeID1", c.getNodeID1());
            cc.put("nodeID2", c.getNodeID2());
            ConstraintList.add(cc);
        }
        ArrayList<Object> PathList = new ArrayList<>();
        for(ProcessPath p : map.getProcessPaths()){
            Map<String, Object> path = new HashMap<>();
            path.put("from", p.getStartNodeID());
            path.put("to", p.getEndNodeID());
            PathList.add(path);
        }
        ArrayList<Object> EquipmentList = new ArrayList<>();
        for(Equipment e : map.getEquipments()){
            Map<String, Object> equipment = new HashMap<>();
            equipment.put("name", e.getName());
            equipment.put("nodes", e.getNodes());
            equipment.put("color", e.getColor());
            equipment.put("description", e.getDescription());
            equipment.put("imagePath", e.getImagePath());
            EquipmentList.add(equipment);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("ProcessNodes", NodeList);
        data.put("ConstraintConditions", ConstraintList);
        data.put("Paths", PathList);
        data.put("Equipments", EquipmentList);

//        StringBuilder mapCode = new StringBuilder("flowchart LR\n");
//        for(ProcessPath path: map.getProcessPaths()){
//            mapCode.append(path.getStartNodeID()).append(" --> ").append(path.getEndNodeID()).append("\n");
//        }
//        data.put("mapCode", mapCode.toString());

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(false);

        Yaml yaml = new Yaml(options);
        try {
            yaml.dump(data, new FileWriter(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeDataTempFile(ProcessMap map, String fileName){
        ArrayList<String> mergedNodeList = new ArrayList<>();
        for (MultiNode n : map.getMultiNodes()) {
            if(!n.getNodeList().isEmpty()){
                mergedNodeList.addAll(n.getNodeList());
            }
        }

        ArrayList<Object> NodeList = new ArrayList<>();
        for(MultiNode n : map.getMultiNodes()){
            if(mergedNodeList.contains(n.getNodeID()))
                break;
            Map<String, Object> node = new HashMap<>();
            node.put("nodeID", n.getNodeID());
            node.put("nodeDescription", n.getNodeDescription());
            node.put("equipmentName", n.getEquipmentName());
            node.put("time", n.getTime());
            node.put("precision", n.getPrecision());
            node.put("cost", n.getCost());
            NodeList.add(node);
        }
        ArrayList<Object> ConstraintList = new ArrayList<>();
        for(ConstraintCondition c : map.getConstraintConditions()){
            Map<String, Object> cc = new HashMap<>();
            cc.put("conditionID", c.getConditionID());
            cc.put("conditionType", c.getConditionType().toString());
            cc.put("conditionDescription", c.getConditionDescription());
            cc.put("nodeID1", c.getNodeID1());
            cc.put("nodeID2", c.getNodeID2());
            ConstraintList.add(cc);
        }
        ArrayList<Object> PathList = new ArrayList<>();
        for(ProcessPath p : map.getProcessPaths()){
            Map<String, Object> path = new HashMap<>();
            path.put("from", p.getStartNodeID());
            path.put("to", p.getEndNodeID());
            PathList.add(path);
        }
        ArrayList<Object> EquipmentList = new ArrayList<>();
        for(Equipment e : map.getEquipments()){
            Map<String, Object> equipment = new HashMap<>();
            equipment.put("name", e.getName());
            equipment.put("nodes", e.getNodes());
            equipment.put("color", e.getColor());
            equipment.put("description", e.getDescription());
            equipment.put("imagePath", e.getImagePath());
            EquipmentList.add(equipment);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("ProcessNodes", NodeList);
        data.put("ConstraintConditions", ConstraintList);
        data.put("Paths", PathList);
        data.put("Equipments", EquipmentList);

//        StringBuilder mapCode = new StringBuilder("flowchart LR\n");
//        for(ProcessPath path: map.getProcessPaths()){
//            mapCode.append(path.getStartNodeID()).append(" --> ").append(path.getEndNodeID()).append("\n");
//        }
//        data.put("mapCode", mapCode.toString());

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(false);

        Yaml yaml = new Yaml(options);
        try {
            yaml.dump(data, new FileWriter(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ProcessMap initMapTest(InputInfo inputInfo){
        ArrayList<ProcessNode> processNodes = inputInfo.getProcessNodes();
        ArrayList<MultiNode> convertNodes = new ArrayList<>();
        ArrayList<ConstraintCondition> constraintConditions = inputInfo.getConstraintConditions();
        LinkedList<ProcessPath> processPaths = inputInfo.getProcessPaths();
        ArrayList<Equipment> equipments = inputInfo.getEquipments();
        String mapID = utils.getRandomString(10);
        //将ProcessNode转换为MultiNode
        for (ProcessNode p : processNodes) {
            MultiNode multiNode = new MultiNode(p.getNodeID(), p.getNodeDescription(), p.getEquipmentName(), p.getTime(), p.getPrecision(), p.getCost());
            convertNodes.add(multiNode);
        }
        return new ProcessMap(mapID, convertNodes, processPaths, constraintConditions, equipments);
    }
    public static Map<String, Object> compareMapPaths(LinkedList<ProcessPath> oldPaths, LinkedList<ProcessPath> newPaths){
        Map<String, Object> res = new HashMap<>();
        res.put("addPath", new ArrayList<>());
        res.put("removePath", new ArrayList<>());
        for(ProcessPath path1: oldPaths){
            boolean flag = false;
            for(ProcessPath path2: newPaths){
                if(path1.getStartNodeID().equals(path2.getStartNodeID()) && path1.getEndNodeID().equals(path2.getEndNodeID())){
                    flag = true;
                    break;
                }
            }
            if(!flag){
                Map<String, String> pathTemp = new HashMap<>();
                pathTemp.put("start", path1.getStartNodeID());
                pathTemp.put("end", path1.getEndNodeID());
                ((ArrayList<Map>) res.get("removePath")).add(pathTemp);
            }
        }
        for(ProcessPath path1: newPaths){
            boolean flag = false;
            for(ProcessPath path2: oldPaths){
                if(path1.getStartNodeID().equals(path2.getStartNodeID()) && path1.getEndNodeID().equals(path2.getEndNodeID())){
                    flag = true;
                    break;
                }
            }
            if(!flag){
                Map<String, String> pathTemp = new HashMap<>();
                pathTemp.put("start", path1.getStartNodeID());
                pathTemp.put("end", path1.getEndNodeID());
                ((ArrayList<Map>) res.get("addPath")).add(pathTemp);
            }
        }
        return  res;
    }
    public static String WriteMapCode(ProcessMap map, Map<String, Object> data){
        StringBuilder mapCode = new StringBuilder("flowchart LR\n");
                if(data == null) {
            for(ProcessPath path: map.getProcessPaths()){
                mapCode.append(path.getStartNodeID()).append(" --> ").append(path.getEndNodeID()).append("\n");
            }
            return mapCode.toString();
        }
        for(ConstraintCondition cc: map.getConstraintConditions()){
            if(cc.getConditionType() == Constant.SAME){
                mapCode.append(cc.getNodeID1()).append("\n").append(cc.getNodeID2()).append("\n");
            }
        }
        for(ProcessPath path: map.getProcessPaths()){
            boolean flag = false;
            for(Map<String, String> item: (ArrayList<Map>) data.get("addPath")){
                if(path.getStartNodeID().equals(item.get("start")) && path.getEndNodeID().equals(item.get("end"))){
                    flag = true;
                    break;
                }
            }
            if(flag) {
                mapCode.append(path.getStartNodeID()).append(" == add ==> ").append(path.getEndNodeID()).append("\n");
            }
            else mapCode.append(path.getStartNodeID()).append(" --> ").append(path.getEndNodeID()).append("\n");
        }
        for(Map<String, String> item: (ArrayList<Map>) data.get("removePath")){
            mapCode.append(item.get("start")).append(" -. remove .-> ").append(item.get("end")).append("\n");
        }
        Map<String, List<String>> nodeMap = new HashMap<>();
        for(ConstraintCondition cc: map.getConstraintConditions()){
            if(cc.getConditionType() == Constant.CONTAIN){
                if(nodeMap.containsKey(cc.getNodeID1())){
                    nodeMap.get(cc.getNodeID1()).add(cc.getNodeID2());
                } else{
                    List<String> temp = new ArrayList<>();
                    temp.add(cc.getNodeID2());
                    nodeMap.put(cc.getNodeID1(), temp);
                }
            }
        }
        if(!nodeMap.isEmpty()){
            for(String key: nodeMap.keySet()){
                mapCode.append("subgraph ").append(key).append("-CONTAIN\n");
                for(String value: nodeMap.get(key)){
                    mapCode.append(value).append("\n");
                    mapCode.append(key).append("\n");
                }
                mapCode.append("end\n");
            }
            mapCode.append("classDef sel1 fill:#aaeeee,stroke:green,stroke-width:2px,stroke-dasharray:5,5;\n");
            mapCode.append("class ");
            for(String key: nodeMap.keySet()){
                mapCode.append(key).append("-CONTAIN");
            }
            mapCode.append(" sel1\n");
        }
        nodeMap.clear();
        for(ConstraintCondition cc: map.getConstraintConditions()){
            if(cc.getConditionType() == Constant.PARTICIPATE){
                if(nodeMap.containsKey(cc.getNodeID2())){
                    nodeMap.get(cc.getNodeID2()).add(cc.getNodeID1());
                } else{
                    List<String> temp = new ArrayList<>();
                    temp.add(cc.getNodeID1());
                    nodeMap.put(cc.getNodeID2(), temp);
                }
            }
        }
        if(!nodeMap.isEmpty()){
            for(String key: nodeMap.keySet()){
                mapCode.append("subgraph ").append(key).append("-PARTICIPATE\n");
                for(String value: nodeMap.get(key)){
                    mapCode.append(value).append("\n");
                    mapCode.append(key).append("\n");
                }
                mapCode.append("end\n");
            }
            mapCode.append("classDef sel2 fill:#dddddd,stroke:green,stroke-width:2px,stroke-dasharray:5,5\n");
            mapCode.append("class ");
            for(String key: nodeMap.keySet()){
                mapCode.append(key).append("-PARTICIPATE,");
            }
            mapCode.append(" sel2\n");
        }
        return mapCode.toString();
    }
    public static String GetRandomValidNode(LinkedList<ProcessPath> paths, String start, String end){
        // 在路径列表中找到一条从start到end的完整路径，从这些路径的中间节点中随机选择一个节点，并返回
        Random random = new Random();
        String node;

        ArrayList<ArrayList<String>> allPaths = GetAllPaths(paths, start, end);
        ArrayList<String> nodes = new ArrayList<>();
        for(ArrayList<String> path: allPaths){
            for(int i = 1; i < path.size()-1; i++){
                if (!nodes.contains(path.get(i))) nodes.add(path.get(i));
            }
        }
        if(nodes.isEmpty()) return "Ab";
        int max = nodes.size()-1;
        int min = 0;
        int randomInt = random.nextInt(max - min + 1) + min;
        node = nodes.get(randomInt);

        return node;
    }
    // 深度优先搜索，获取到从start到end的所有完整路径
    public static void DFSPath(LinkedList<ProcessPath> paths, String start, String end, ArrayList<String> pathList, ArrayList<ArrayList<String>> res){
        if(start.equals(end)){
            res.add(new ArrayList<>(pathList));
//            System.out.println(pathList);
            return;
        }
        for(ProcessPath path: paths){
            if(path.getStartNodeID().equals(start)){
                pathList.add(start);
                DFSPath(paths, path.getEndNodeID(), end, pathList, res);
                pathList.remove(pathList.size()-1);
            }
        }
    }
    public static ArrayList<ArrayList<String>> GetAllPaths(LinkedList<ProcessPath> paths, String start, String end){
        ArrayList<ArrayList<String>> res = new ArrayList<>();  // 存储所有从start到end的完整路径
        ArrayList<String> pathList = new ArrayList<>();  // 存储当前路径
//        System.out.println("start:"+start+" end:"+end);
        DFSPath(paths, start, end, pathList, res);
        return res;
    }

    public static ArrayList<String> GetAllNodes(LinkedList<ProcessPath> paths, String start, String end){
        ArrayList<String> res = new ArrayList<>();  // 存储所有从start到end的所有路径上的所有节点

        // 广度优先搜索，获取到从start到end的所有节点,并且实现去重，使用栈结构模拟递归调用
        Queue<List<String>> queue = new LinkedList<>();
        Map<String, ArrayList<String>> adjList = new HashMap<>();  // 邻接表

        // 构造
        for (ProcessPath path : paths) {
            adjList.computeIfAbsent(path.getStartNodeID(), k -> new ArrayList<>()).add(path.getEndNodeID());
        }

        // Initialize BFS
        queue.add(Arrays.asList(start));

        while (!queue.isEmpty()) {
            List<String> path = queue.poll();
            String lastNode = path.get(path.size() - 1);

            if (lastNode.equals(end)) {
                for(String node: path){
                    if(!res.contains(node)) res.add(node);
                }
            } else {
                List<String> neighbors = adjList.get(lastNode);
                if (neighbors != null) {
                    for (String neighbor : neighbors) {
                        if (!path.contains(neighbor)) {  // Avoid cycles
                            List<String> newPath = new ArrayList<>(path);
                            newPath.add(neighbor);
                            queue.add(newPath);
                        }
                    }
                }
            }
        }

        return res;
    }
}