package com.example.optimization_algorithm_backend.Controller;

import com.example.optimization_algorithm_backend.Service.ConstraintService;
import com.example.optimization_algorithm_backend.algorithm.model.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


@RestController
@CrossOrigin(origins = "*")
public class ConstraintController {

    @Autowired
    private ConstraintService constraintService;
    @Value("${path.historyPath}")
    String historyPath;


    @PostMapping("/addConstraint")
    public Map<String, Object> addConstraint(@RequestParam Map<String, Object> res_map) {
        Map<String, Object> res = new HashMap<>();
        String example = (String) res_map.get("example");
        String nodeID1 = (String) res_map.get("nodeID1");
        String nodeID2 = (String) res_map.get("nodeID2");
        String type = (String) res_map.get("type");

        String path = historyPath + example + ".yaml";
        File file = new File(path);
        if(!file.exists()){
            res.put("code", 500);
            res.put("msg", "实例不存在");
            return res;
        }

        Constant constraintType = getConstant(type);
        if (constraintType == null) {
            res.put("code", 500);
            res.put("msg", "添加失败，约束类型错误");
            return res;
        }
        if(!constraintService.findNode(nodeID1, example) || !constraintService.findNode(nodeID2, example)){
            res.put("code", 500);
            res.put("msg", "添加失败，节点不存在");
            return res;
        }else if (nodeID1.equals(nodeID2)) {
            res.put("code", 500);
            res.put("msg", "添加失败，两个节点相同");
            return res;
        } else if (constraintService.findConstraint(nodeID1, nodeID2, example)) {
            res.put("code", 500);
            res.put("msg", "添加失败，约束已存在");
            return res;
        } else {
            constraintService.addConstraint(nodeID1, nodeID2, constraintType, example);
            res.put("code", 200);
            res.put("msg", "添加约束成功");
        }
//        String constraintID = nodeID1 + nodeID2;
//        String constraintDescription = "This is a constraint between " + nodeID1 + " and " + nodeID2;
//
//        ProcessMap map = algorithm1Service.pathToMap(path);
//        ArrayList<ConstraintCondition> constraints = map.getConstraintConditions();
//        ConstraintCondition newConstraint = new ConstraintCondition(constraintID, constraintDescription,constraintType, nodeID1, nodeID2);
//        // 检查是否已存在相同的约束
//        for (ConstraintCondition constraint : constraints) {
//            if (constraint.getNodeID1().equals(nodeID1) && constraint.getNodeID2().equals(nodeID2)) {
//                res.put("code", 500);
//                res.put("msg", "添加失败，约束已存在");
//                return res;
//            }
//        }
//
//        constraints.add(newConstraint);
//        map.setConstraintConditions(constraints);
//        writeData(map, path);
//        res.put("code", 200);
//        res.put("msg", "添加约束后成功");
        return res;
    }

    //修改约束条件
    @PostMapping("/modifyConstraint")
    public Map<String, Object> modifyConstraint(@RequestParam Map<String, Object> res_map) {
        Map<String, Object> res = new HashMap<>();
        String example = (String) res_map.get("example");
        String nodeID1 = (String) res_map.get("nodeID1");
        String nodeID2 = (String) res_map.get("nodeID2");
        String type = (String) res_map.get("type");

        String path = historyPath + example + ".yaml";
        File file = new File(path);
        if(!file.exists()){
            res.put("code", 500);
            res.put("msg", "实例不存在");
            return res;
        }

        Constant constraintType = getConstant(type);
        if (constraintType == null) {
            res.put("code", 500);
            res.put("msg", "添加失败，约束类型错误");
            return res;
        }
        if (nodeID1.equals(nodeID2)) {
            res.put("code", 500);
            res.put("msg", "修改失败，两个节点相同");
        } else if (constraintService.findConstraint(nodeID1, nodeID2, example)) {
            constraintService.modifyConstraint(nodeID1, nodeID2, constraintType, example);
            res.put("code", 200);
            res.put("msg", "修改约束成功");
        } else {
            res.put("code", 500);
            res.put("msg", "修改失败，约束不存在");
        }
        return res;
    }

    @PostMapping("/deleteConstraint")
    public Map<String, Object> deleteConstraint(@RequestParam Map<String, Object> constraint) {
        Map<String, Object> res = new HashMap<>();
        String example = (String) constraint.get("example");
        String nodeID1 = (String) constraint.get("nodeID1");
        String nodeID2 = (String) constraint.get("nodeID2");
        String type = (String) constraint.get("type");

        String path = historyPath + example + ".yaml";
        File file = new File(path);
        if(!file.exists()){
            res.put("code", 500);
            res.put("msg", "实例不存在");
            return res;
        }

        Constant constraintType = getConstant(type);
        if (constraintType == null) {
            res.put("code", 500);
            res.put("msg", "添加失败，约束类型错误");
            return res;
        }

        if (nodeID1.equals(nodeID2)) {
            res.put("code", 500);
            res.put("msg", "删除失败，两个节点相同");
        } else if (constraintService.findConstraint(nodeID1, nodeID2, example)) {
            constraintService.deleteConstraint(nodeID1, nodeID2, constraintType, example);
            res.put("code", 200);
            res.put("msg", "删除约束成功");
        } else {
            res.put("code", 500);
            res.put("msg", "删除失败，约束不存在");
        }
        return res;
    }

    public Constant getConstant(String type) {
        switch (type) {
            case "CONNECT":
                return Constant.CONNECT;
            case "SAME":
                return Constant.SAME;
            case "CONTAIN":
                return Constant.CONTAIN;
            case "FOLLOW":
                return Constant.FOLLOW;
            case "CALL":
                return Constant.CALL;
            case "PARTICIPATE":
                return Constant.PARTICIPATE;
            default:
                return null;
        }
    }


}
