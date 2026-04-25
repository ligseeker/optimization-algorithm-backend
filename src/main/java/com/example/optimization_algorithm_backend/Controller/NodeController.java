package com.example.optimization_algorithm_backend.Controller;

import com.example.optimization_algorithm_backend.Service.AlgorithmService;
import com.example.optimization_algorithm_backend.Service.EquipmentService;
import com.example.optimization_algorithm_backend.Service.NodeService;
import com.example.optimization_algorithm_backend.algorithm.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.*;

import static com.example.optimization_algorithm_backend.algorithm.Main.writeData;

@RestController
@CrossOrigin(origins = "*")
public class NodeController {

    @Autowired
    private AlgorithmService algorithmService;
    @Autowired
    private NodeService nodeService;
    @Autowired
    private EquipmentService equipmentService;
    @Value("${path.historyPath}")
    String historyPath;
    @Value("${path.inputPath}")
    String inputPath;
    @Value("${path.outputPath}")
    String outputPath;

    @PostMapping("/addNode")
    public Map<String, Object> AddNode(@RequestParam Map<String, Object> re_map) {
        Map<String, Object> res = new HashMap<>();
        String example = (String) re_map.get("example");
        String nodeID = (String) re_map.get("nodeID");
        String nodeDescription = (String) re_map.get("nodeDescription");
        int time = Integer.parseInt((String) re_map.get("time"));
        double precision = Double.parseDouble((String) re_map.get("precision"));
        int cost = Integer.parseInt((String) re_map.get("cost"));

        String path = historyPath + example + ".yaml";
        File file = new File(path);
        if(!file.exists()){
            res.put("code", 500);
            res.put("msg", "实例不存在");
            return res;
        }

        MultiNode node = nodeService.findNodeInMap(nodeID, "history", example);
        if(node != null){
            res.put("code", 500);
            res.put("msg", "添加失败，nodeID已存在");
            return res;
        }
        nodeService.addNodeInMap(nodeID, nodeDescription, time, precision, cost, example);

        ProcessMap map = algorithmService.pathToMap(path);
        String mapCode = algorithmService.getMapCode(map);
        res.put("mapCode", mapCode);
        res.put("code", 200);
        res.put("msg", "添加节点成功");
        return res;
    }
    @PostMapping("/updateNode")
    public Map<String, Object> UpdateNode(@RequestParam Map<String, Object> re_map) {
        Map<String, Object> res = new HashMap<>();
        String example = (String) re_map.get("example");
        String nodeID = (String) re_map.get("nodeID");
        String nodeDescription = (String) re_map.get("nodeDescription");
        int time = Integer.parseInt((String) re_map.get("time"));
        double precision = Double.parseDouble((String) re_map.get("precision"));
        int cost = Integer.parseInt((String) re_map.get("cost"));

        String path = historyPath + example + ".yaml";
        File file = new File(path);
        if(!file.exists()){
            res.put("code", 500);
            res.put("msg", "实例不存在");
            return res;
        }

        // 根据nodeID更新节点信息
        MultiNode node = nodeService.findNodeInMap(nodeID, "history", example);
        if(node == null){
            res.put("code", 500);
            res.put("msg", "修改节点失败，nodeID不存在");
            return res;
        }
        nodeService.updateNodeInMap(nodeID, nodeDescription, time, precision, cost, example);

        ProcessMap map = algorithmService.pathToMap(path);
        String mapCode = algorithmService.getMapCode(map);
        res.put("mapCode", mapCode);
        res.put("code", 200);
        res.put("msg", "修改节点成功");
        return res;
    }

    @PostMapping("/deleteNode")
    public Map<String, Object> DeleteNode(@RequestParam Map<String, Object> re_map) {
        Map<String, Object> res = new HashMap<>();
        String example = (String) re_map.get("example");
        String nodeID = (String) re_map.get("nodeID");

        String path = historyPath + example + ".yaml";
        File file = new File(path);
        if(!file.exists()){
            res.put("code", 500);
            res.put("msg", "实例不存在");
            return res;
        }

        // 根据nodeID删除节点信息，自动删除关联的条件约束和路径
        MultiNode node = nodeService.findNodeInMap(nodeID, "history", example);
        if(node == null){
            res.put("code", 500);
            res.put("msg", "删除节点失败，nodeID不存在");
            return res;
        }
        nodeService.deleteNodeInMap(nodeID, example);
        ProcessMap map = algorithmService.pathToMap(path);
        String mapCode = algorithmService.getMapCode(map);
        res.put("mapCode", mapCode);
        res.put("code", 200);
        res.put("msg", "删除节点成功");
        return res;
    }

    @GetMapping("getNodeInfo")
    public Map<String, Object> GetNodeInfo(@RequestParam Map<String, Object> re_map) {
        Map<String, Object> res = new HashMap<>();
        String example = (String) re_map.get("example");
        String type = (String) re_map.get("type");
        String path;

        if(Objects.equals(type, "history")){
            path = historyPath + example + ".yaml";
        }else if(Objects.equals(type, "input")) {
            path = inputPath + example + ".yaml";
        } else if (Objects.equals(type, "output")){
            path = outputPath + example + ".yaml";
        }else {
            res.put("code", 500);
            res.put("msg", "目录错误");
            return res;
        }
        ProcessMap map;

        File file = new File(path);
        if(!file.exists()){
            res.put("code", 500);
            res.put("msg", "实例不存在");
            return res;
        }

        String nodeID = (String) re_map.get("nodeID");

        // 根据nodeID查找节点信息
        MultiNode node = nodeService.findNodeInMap(nodeID, type, example);
        if(node == null){
            res.put("code", 500);
            res.put("msg", "查找节点失败，nodeID不存在");
            return res;
        }
        ArrayList<ConstraintCondition> ccList = nodeService.getCCbyNodeID(nodeID, type, example);
        res.put("nodeID", node.getNodeID());
        res.put("nodeDescription", node.getNodeDescription());
        res.put("time", node.getTime());
        res.put("precision", node.getPrecision());
        res.put("cost", node.getCost());
        res.put("ccList", ccList);
        res.put("equipmentName", node.getEquipmentName());
        res.put("code", 200);
        res.put("msg", "获取节点成功");
        return res;
    }

    // 修改节点所属装备
    @PostMapping("/modifyNodeEquipment")
    public Map<String, Object> ModifyNodeEquipment(@RequestParam Map<String, Object> re_map) {
        Map<String, Object> res = new HashMap<>();
        String example = (String) re_map.get("example");
        String nodeID = (String) re_map.get("nodeID");
        String equipmentName = (String) re_map.get("equipmentName");

        if(equipmentName == null || equipmentName.isEmpty()){
            equipmentName = "";
        }

        String path = historyPath + example + ".yaml";
        File file = new File(path);
        if(!file.exists()){
            res.put("code", 500);
            res.put("msg", "实例不存在");
            return res;
        }
        if(!equipmentName.isEmpty()){
            if(!equipmentService.findEquipment(equipmentName, example)){
                res.put("code", 500);
                res.put("msg", "修改节点所属装备失败，装备不存在");
                return res;
            }
        }
        ProcessMap map = algorithmService.pathToMap(path);
        ArrayList<MultiNode> nodes = map.getMultiNodes();

        // 根据nodeID修改节点所属装备
        int i = -1;
        for(MultiNode node: nodes){
            if(nodeID.equals(node.getNodeID())){
                i = nodes.indexOf(node);
                break;
            }
        }
        if(i == -1){
            res.put("code", 500);
            res.put("msg", "修改节点所属装备失败，nodeID不存在");
            return res;
        }
        MultiNode node = nodes.get(i);
        String oldEquipmentName = node.getEquipmentName();
        node.setEquipmentName(equipmentName);
        nodes.set(i, node);
        map.setMultiNodes(nodes);

        if(!oldEquipmentName.isEmpty()){
            // 修改节点所属装备后,在原装备中删除节点
            ArrayList<Equipment> equipments = map.getEquipments();
            for(Equipment equipment: equipments){
                if(equipment.getName().equals(oldEquipmentName)){
                    equipment.getNodes().remove(nodeID);
                }
            }
            map.setEquipments(equipments);
        }
        //  2021/5/26 修改节点所属装备后,在装备中添加节点
        if(!equipmentName.isEmpty()){
            // 修改节点所属装备后,在装备中添加节点，并删除原装备里的节点
            ArrayList<Equipment> equipments = map.getEquipments();
            for(Equipment equipment: equipments){
                if(equipment.getName().equals(equipmentName)){
                    if(!equipment.getNodes().contains(nodeID)){
                        equipment.getNodes().add(nodeID);
                    }
                }
            }
            map.setEquipments(equipments);
        }
        writeData(map, path);
        String mapCode = algorithmService.getMapCode(map);
        res.put("mapCode", mapCode);
        res.put("code", 200);
        res.put("msg", "修改节点所属装备成功");
        return res;
    }

    // 获取未分配的节点列表
    @GetMapping("/getUnassignedNodes")
    public Map<String, Object> GetUnassignedNodes(@RequestParam Map<String, Object> re_map) {
        Map<String, Object> res = new HashMap<>();
        String example = (String) re_map.get("example");
        String path = historyPath + example + ".yaml";
        ProcessMap map = algorithmService.pathToMap(path);
        ArrayList<MultiNode> nodes = map.getMultiNodes();
        ArrayList<String> unassignedNodes = new ArrayList<>();

        File file = new File(path);
        if(!file.exists()){
            res.put("code", 500);
            res.put("msg", "实例不存在");
            return res;
        }

        for(MultiNode node: nodes){
            if(node.getEquipmentName().isEmpty()){
                unassignedNodes.add(node.getNodeID());
            }
        }
        res.put("unassignedNodes", unassignedNodes);
        res.put("code", 200);
        res.put("msg", "获取未分配节点成功");
        return res;
    }

}
