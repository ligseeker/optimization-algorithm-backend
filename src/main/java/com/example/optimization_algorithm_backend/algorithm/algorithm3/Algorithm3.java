package com.example.optimization_algorithm_backend.algorithm.algorithm3;

import com.example.optimization_algorithm_backend.algorithm.model.*;

import java.util.*;

import static com.example.optimization_algorithm_backend.algorithm.Main.GetAllNodes;

public class Algorithm3 {  // 迭代优化算法
    Map<String, ArrayList<String>> pathList;
    ArrayList<String> sortNodes;
    double[] oldValues = new double[3];
    public void initAlgorithm(ProcessMap map){
        this.pathList = createMap(map);
        this.sortNodes = getSortNodes(this.pathList);
        this.oldValues[0] = getTotalTime(map.getMultiNodes(), map.getProcessPaths(), this.sortNodes);
        this.oldValues[1] = getTotalPrecision(map.getMultiNodes(), this.sortNodes);
        this.oldValues[2] = getTotalCost(map.getMultiNodes(), this.sortNodes);
    }
    public ProcessMap getOptimizationMap(ProcessMap oldMap, int[] factor, int x1){
        ProcessMap newMap = oldMap;
        if(x1 == 1 || x1 == 2){
            newMap = RandomCreateMap(oldMap);
        }
        this.pathList = createMap(oldMap);
        this.sortNodes = getSortNodes(this.pathList);
        double oldRatio, newRatio;
        int epoch = 10;
        for(int i = 0; i < epoch; i++){
            ProcessMap tempMap = IterativeOptimize(newMap, x1);  // 优化链式结构
            oldRatio = CalculateRatio(newMap, factor);
            newRatio = CalculateRatio(tempMap, factor);
            if(newRatio > oldRatio){
                newMap = tempMap;
            }
        }
        return newMap;
    }
    public ProcessMap IterativeOptimize(ProcessMap oldMap, int x1){
        String newMapID = utils.getRandomString(10);
        ArrayList<MultiNode> newMultiNodes = oldMap.getMultiNodes();
        LinkedList<ProcessPath> newProcessPaths = oldMap.getProcessPaths();
        ArrayList<ConstraintCondition> newConstraintConditions = oldMap.getConstraintConditions();
        ArrayList<Equipment> newEquipments = oldMap.getEquipments();
        ArrayList<String> nodesWithConstraint;

        ArrayList<String> nodeList = new ArrayList<>();
        for(ProcessPath path: oldMap.getProcessPaths()){
            if(!nodeList.contains(path.getStartNodeID())) nodeList.add(path.getStartNodeID());
            if(!nodeList.contains(path.getEndNodeID())) nodeList.add(path.getEndNodeID());
        }
        if(x1 == 2)  Collections.shuffle(nodeList);
        for(String end: nodeList){
            nodesWithConstraint = getNodeWithConstraint(end, newConstraintConditions, nodeList);
            if(!nodesWithConstraint.isEmpty()){
                for(String start : nodesWithConstraint){
//                    writeData(oldMap, "src/main/java/com/example/optimization_algorithm_backend/algorithm/temp.yaml");
                    ArrayList<String> lastTwoNodes = getLastTwoNode(start, end, this.pathList);
                    if (lastTwoNodes.isEmpty()){
                        if(!this.pathList.get(start).contains(end)){
                            this.pathList.get(start).add(end);
                            newProcessPaths.removeIf(p -> p.getStartNodeID().equals(start) && p.getEndNodeID().equals(end));
                            newProcessPaths.add(new ProcessPath(utils.getRandomString(5), start, end));
                        }
                    } else {
                        for(String node: lastTwoNodes){
                            boolean flag = false;
                            for(ConstraintCondition cc: newConstraintConditions){
                                if(cc.getNodeID1().equals(node) && cc.getNodeID2().equals(end)) {
                                    flag = true;
                                    break;
                                }
                            }

                            if(!start.equals(node) && !this.pathList.get(start).contains(end)){
                                this.pathList.get(start).add(end);
                                newProcessPaths.removeIf(p -> p.getStartNodeID().equals(start) && p.getEndNodeID().equals(end));
                                newProcessPaths.add(new ProcessPath(utils.getRandomString(5), start, end));
                            }
                            if(!start.equals(node) && !flag){
                                this.pathList.get(node).remove(end);
                                newProcessPaths.removeIf(p -> p.getStartNodeID().equals(node) && p.getEndNodeID().equals(end));
                                for(String n : this.pathList.get(end)){
                                    if(!this.pathList.get(node).contains(n)){
                                        this.pathList.get(node).add(n);
                                        newProcessPaths.removeIf(p -> p.getStartNodeID().equals(node) && p.getEndNodeID().equals(n));
                                        newProcessPaths.add(new ProcessPath(utils.getRandomString(5), node, n));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        ProcessMap newMap =  new ProcessMap(newMapID, newMultiNodes, newProcessPaths, newConstraintConditions, newEquipments);
        this.pathList = createMap(newMap);
        this.sortNodes = getSortNodes(this.pathList);
        int newTime = getTotalTime(newMap.getMultiNodes(), newMap.getProcessPaths(), this.sortNodes);
        double newPrecision = getTotalPrecision(newMap.getMultiNodes(), this.sortNodes);
        int newCost = getTotalCost(newMap.getMultiNodes(), this.sortNodes);
        newMap.setTotalTime(newTime);
        newMap.setTotalPrecision(newPrecision);
        newMap.setTotalCost(newCost);
        return newMap;
    }

    public ProcessMap OptimizeMap(ProcessMap map, int[] factor, int x2){  // 根据同一关系的特点，遍历同一关系的节点能否在流程图中完成替换
        ArrayList<MultiNode> multiNodes = map.getMultiNodes();
        LinkedList<ProcessPath> processPaths = map.getProcessPaths();
        ArrayList<ConstraintCondition> constraintConditions = map.getConstraintConditions();
        ArrayList<Equipment> equipments = map.getEquipments();
        if(x2 == 2){
            Collections.shuffle(constraintConditions);
        }
        String newMapID = utils.getRandomString(10);
        ArrayList<MultiNode> newMultiNodes = multiNodes;
        LinkedList<ProcessPath> newProcessPaths = processPaths;
        ArrayList<ConstraintCondition> newConstraintConditions = constraintConditions;
        this.pathList = createMap(map);
        this.sortNodes = getSortNodes(this.pathList);
        int oldTime = (int) this.oldValues[0];
        double oldPrecision = this.oldValues[1];
        int oldCost = (int) this.oldValues[2];
        double optimizationRatio = 0.0;
        double temp;
        int sum = factor[0] + factor[1] + factor[2];
        double factor1 = (double) factor[0] / sum;
        double factor2 = (double) factor[1] / sum;
        double factor3 = (double) factor[2] / sum;
        for(ConstraintCondition cc : constraintConditions){
            if(cc.getConditionType() == Constant.SAME) {
                if(this.sortNodes.contains(cc.getNodeID1())){
                    LinkedList<ProcessPath> paths = updateProcessPaths(processPaths, cc.getNodeID1(), cc.getNodeID2());
                    ArrayList<String> sortNodes1 = updateSortNodes(this.sortNodes, cc.getNodeID1(), cc.getNodeID2());
                    int newTime = getTotalTime(multiNodes, paths, sortNodes1);
                    double newPrecision = getTotalPrecision(multiNodes, sortNodes1);
                    int newCost = getTotalCost(multiNodes,sortNodes1);
                    temp = (double) (oldTime - newTime) / oldTime * factor1 + (newPrecision - oldPrecision) * factor2 + (double) (oldCost - newCost) / oldCost * factor3;
                    if(temp > optimizationRatio){
                        newProcessPaths = paths;
                        this.sortNodes = sortNodes1;
                        optimizationRatio = temp;
                    }
                }
                else if(this.sortNodes.contains(cc.getNodeID2())){
                    LinkedList<ProcessPath> paths = updateProcessPaths(processPaths, cc.getNodeID2(), cc.getNodeID1());
                    ArrayList<String> sortNodes1 = updateSortNodes(this.sortNodes, cc.getNodeID2(), cc.getNodeID1());
                    int newTime = getTotalTime(multiNodes, paths, sortNodes1);
                    double newPrecision = getTotalPrecision(multiNodes, sortNodes1);
                    int newCost = getTotalCost(multiNodes,sortNodes1);
                    temp = (double) (oldTime - newTime) / oldTime * factor1 + (newPrecision - oldPrecision) * factor2 + (double) (oldCost - newCost) / oldCost * factor3;
                    if(temp > optimizationRatio){
                        newProcessPaths = paths;
                        this.sortNodes = sortNodes1;
                        optimizationRatio = temp;
                    }
                }
            }
        }
        ProcessMap newMap = new ProcessMap(newMapID, newMultiNodes, newProcessPaths, newConstraintConditions, equipments);
        int time = getTotalTime(newMultiNodes, newProcessPaths, this.sortNodes);
        double precision = getTotalPrecision(newMultiNodes, this.sortNodes);
        int cost = getTotalCost(newMultiNodes, this.sortNodes);
        newMap.setTotalTime(time);
        newMap.setTotalPrecision(precision);
        newMap.setTotalCost(cost);
        return newMap;
    }

    public ArrayList<String> getLastTwoNode(String from, String to, Map<String, ArrayList<String>> map) {
        ArrayList<String> res = new ArrayList<>();
        if(map.get(from).contains(to)){
            res.add(from);
            return res;
        }
        for(String node: map.get(from)){
            ArrayList<String> nodes = getLastTwoNode(node, to, map);
            for(String n : nodes){
                if(!res.contains(n)) res.add(n);
            }
        }
        return res;
    }

    public ProcessMap RandomCreateMap(ProcessMap oldMap){
        ArrayList<String> startNodes = new ArrayList<>();
        ArrayList<String> endNodes = new ArrayList<>();
        ArrayList<String> tempNodes = new ArrayList<>();
        for(ProcessPath path: oldMap.getProcessPaths()){
            String start = path.getStartNodeID();
            String end = path.getEndNodeID();
            ArrayList<String> nodes = GetAllNodes(oldMap.getProcessPaths(), start, end);
            int max = nodes.size()-1;
            int min = 0;
            Random random = new Random();
            int randomInt = random.nextInt(max - min + 1) + min;
            String temp = nodes.get(randomInt);
            if(temp.equals(start) || temp.equals(end)) continue;
            startNodes.add(start);
            endNodes.add(end);
            tempNodes.add(temp);
        }
        if(startNodes.isEmpty()) return oldMap;
        for(int i=0;i<startNodes.size();i++){
            int j =i;
            oldMap.getProcessPaths().removeIf(p -> p.getStartNodeID().equals(startNodes.get(j)) && p.getEndNodeID().equals(endNodes.get(j)));
            oldMap.getProcessPaths().removeIf(p -> p.getStartNodeID().equals(tempNodes.get(j)) && p.getEndNodeID().equals(endNodes.get(j)));
            oldMap.getProcessPaths().add(new ProcessPath(utils.getRandomString(5), tempNodes.get(j), endNodes.get(j)));
        }
        return new ProcessMap(utils.getRandomString(10), oldMap.getMultiNodes(), oldMap.getProcessPaths(), oldMap.getConstraintConditions(), oldMap.getEquipments());
    }

    public ArrayList<String> getNodeWithConstraint(String to, ArrayList<ConstraintCondition> list, ArrayList<String> nodes){
        ArrayList<String> res = new ArrayList<>();
        for(ConstraintCondition c : list){
            if(c.getNodeID2().equals(to) && c.getConditionType()==Constant.FOLLOW && nodes.contains(c.getNodeID1())){
                res.add(c.getNodeID1());
            }
        }
        return res;
    }
    public Map<String, ArrayList<String>> createMap(ProcessMap map) {  // 创建节点之间的映射, 用于查找路径
        Map<String, ArrayList<String>> res = new HashMap<>();
        for (ProcessPath path : map.getProcessPaths()) {
            if(!res.containsKey(path.getStartNodeID())) res.put(path.getStartNodeID(), new ArrayList<>());
            res.get(path.getStartNodeID()).add(path.getEndNodeID());
        }
        ArrayList<String> list = new ArrayList<>();
        for(ArrayList<String> l: res.values()){
            for(String node: l){
                if(!res.containsKey(node)) list.add(node);
            }
        }
        for(String node: list){
            res.put(node, new ArrayList<>());
        }
        return res;
    }
    public ArrayList<String> getSortNodes(Map<String, ArrayList<String>> map){  // 按照拓扑排序的顺序返回节点列表
        ArrayList<String> res = new ArrayList<>();
        Map<String, Integer> inDegree = new HashMap<>();
        for(String key : map.keySet()){
            inDegree.put(key, 0);
        }
        for(String key : map.keySet()){
            for(String node : map.get(key)){
                inDegree.put(node, inDegree.get(node) + 1);
            }
        }
        while(!inDegree.isEmpty()){
            String node = "";
            for(String key : inDegree.keySet()){
                if(inDegree.get(key) == 0){
                    node = key;
                    break;
                }
            }
            res.add(node);
            inDegree.remove(node);
            for(String n : map.get(node)){
                inDegree.put(n, inDegree.get(n) - 1);
            }
        }
        return res;
    }
    public int getTotalTime(ArrayList<MultiNode> multiNodes, LinkedList<ProcessPath> processPaths, ArrayList<String> sortNodes){  // 计算获取时间
        int res;
        Map<String, Integer> timeMap = new HashMap<>();
        for (String sortNode : sortNodes) {
            timeMap.put(sortNode, 0);
        }
        for (String node : sortNodes) {
            int time = 0;
            for (MultiNode multiNode : multiNodes) {
                if (multiNode.getNodeID().equals(node)) {
                    time = multiNode.getTime();
                    break;
                }
            }
            timeMap.put(node, time);
            for (ProcessPath path : processPaths) {
                if (path.getEndNodeID().equals(node)) {
                    timeMap.put(node, Math.max(timeMap.get(node), timeMap.get(path.getStartNodeID()) + time));
                }
            }
        }
//        System.out.println(timeMap);
        res = timeMap.get(sortNodes.get(sortNodes.size() - 1));
        return res;
    }
    public double getTotalPrecision(ArrayList<MultiNode> multiNodes, ArrayList<String> sortNodes){  // 计算获取精度
        double res = 1;
        double precision = 1;
        for(String node : sortNodes){
            for(MultiNode multiNode : multiNodes){
                if(multiNode.getNodeID().equals(node)){
                    precision = multiNode.getPrecision();
                    break;
                }
            }
            res *= precision;
        }
        return res;
    }
    public int getTotalCost(ArrayList<MultiNode> multiNodes, ArrayList<String> sortNodes){  // 计算获取成本
        int res = 0;
        int cost = 0;
        for(String node : sortNodes){
            for(MultiNode multiNode : multiNodes){
                if(multiNode.getNodeID().equals(node)){
                    cost = multiNode.getCost();
                    break;
                }
            }
            res += cost;
        }
        return res;
    }
    public LinkedList<ProcessPath> updateProcessPaths(LinkedList<ProcessPath> processPaths, String oldNodeID, String newNodeID){  // 更新调整路径列表
        LinkedList<ProcessPath> res = processPaths;
        for(ProcessPath path : processPaths){
            if(path.getStartNodeID().equals(oldNodeID)){
                path.setStartNodeID(newNodeID);
            }
            else if(path.getEndNodeID().equals(oldNodeID)){
                path.setEndNodeID(newNodeID);
            }
        }
        return res;
    }
    public ArrayList<String> updateSortNodes(ArrayList<String> sortNodes, String oldNodeID, String newNodeID){  // 更新调整遍历顺序
        ArrayList<String> res = sortNodes;
        int i = res.indexOf(oldNodeID);
        res.set(i, newNodeID);
        return res;
    }
    // 计算优化比例
    public double CalculateRatio(ProcessMap map, int[] factor){
        int sum = factor[0] + factor[1] + factor[2];
        double factor1 = (double) factor[0] / sum;
        double factor2 = (double) factor[1] / sum;
        double factor3 = (double) factor[2] / sum;
        double oldTime = this.oldValues[0];
        double oldPrecision = this.oldValues[1];
        double oldCost = this.oldValues[2];
        double newTime = map.getTotalTime();
        double newPrecision = map.getTotalPrecision();
        double newCost = map.getTotalCost();
        return (oldTime - newTime) / oldTime * factor1 + (newPrecision - oldPrecision) * factor2 + (oldCost - newCost) / oldCost * factor3;
    }
    public Map<String,Object> getValue(ProcessMap map){
        Map<String, Object> res = new HashMap<>();
        Map<String, Object> oldValue = new HashMap<>();
        oldValue.put("time", (int) this.oldValues[0]);
        oldValue.put("precision", this.oldValues[1]);
        oldValue.put("cost", (int) this.oldValues[2]);
        res.put("oldValue", oldValue);
        Map<String, Object> newValue = new HashMap<>();
        newValue.put("time", map.getTotalTime());
        newValue.put("precision", map.getTotalPrecision());
        newValue.put("cost", map.getTotalCost());
        res.put("newValue", newValue);
        return res;
    }
}
