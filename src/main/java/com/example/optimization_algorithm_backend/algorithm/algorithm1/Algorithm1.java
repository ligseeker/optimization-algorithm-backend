package com.example.optimization_algorithm_backend.algorithm.algorithm1;

import com.example.optimization_algorithm_backend.algorithm.model.*;

import java.util.*;

public class Algorithm1 {
    Map<String, ArrayList<String>> pathList;
    ArrayList<String> sortNodes;
    double[] oldValues = new double[3];

    public double[] getOldValues() {
        return oldValues;
    }

    public void setOldValues(double[] oldValues) {
        this.oldValues = oldValues;
    }

    public void initAlgorithm(ProcessMap map){
        this.pathList = createMap(map);
        this.sortNodes = getSortNodes(this.pathList);
        this.oldValues[0] = getTotalTime(map.getMultiNodes(), map.getProcessPaths(), this.sortNodes);
        this.oldValues[1] = getTotalPrecision(map.getMultiNodes(), this.sortNodes);
        this.oldValues[2] = getTotalCost(map.getMultiNodes(), this.sortNodes);
    }
    public ProcessMap getOptimizationMap(ProcessMap map, int x1){  // 根据算法设计，调整链路结构，优化承接关系的条件约束
        String newMapID = utils.getRandomString(10);
        ArrayList<MultiNode> newMultiNodes = map.getMultiNodes();
        LinkedList<ProcessPath> newProcessPaths = map.getProcessPaths();
        ArrayList<ConstraintCondition> newConstraintConditions = map.getConstraintConditions();
        ArrayList<Equipment> newEquipments = map.getEquipments();
        int totalTime = map.getTotalTime();
        double totalPrecision = map.getTotalPrecision();
        int totalCost = map.getTotalCost();
        ArrayList<String> nodesWithConstraint;
        this.sortNodes = adjustSortNodes(this.sortNodes, this.pathList, x1);
        for(String end : this.sortNodes){
//            System.out.println("now node: " + end);
            nodesWithConstraint = getNodeWithConstraint(end, newConstraintConditions);
//            System.out.println(nodesWithConstraint);
            if(!nodesWithConstraint.isEmpty()){
                for(String start : nodesWithConstraint){
                    ArrayList<String> lastTwoNodes = getLastTwoNode(start, end, this.pathList);
//                    System.out.println(start + "->" + end + ":last two nodes is "+ lastTwoNodes);
                    if (lastTwoNodes.isEmpty()){
//                        System.out.println(start + " -> " + end +" no path");
                        if(!this.pathList.get(start).contains(end)){
                            this.pathList.get(start).add(end);
                            newProcessPaths.removeIf(p -> p.getStartNodeID().equals(start) && p.getEndNodeID().equals(end));
                            newProcessPaths.add(new ProcessPath(utils.getRandomString(5), start, end));
//                            System.out.println("add path: "+ start + " -> " + end );
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
//                            System.out.println(start+ "->" + node +": " + flag);
                            if(!start.equals(node) && !this.pathList.get(start).contains(end)){
                                this.pathList.get(start).add(end);
                                newProcessPaths.removeIf(p -> p.getStartNodeID().equals(start) && p.getEndNodeID().equals(end));
                                newProcessPaths.add(new ProcessPath(utils.getRandomString(5), start, end));
//                                System.out.println("add path: "+ start + " -> " + end );
                            }
                            if(!start.equals(node) && !flag){
                                this.pathList.get(node).remove(end);
                                newProcessPaths.removeIf(p -> p.getStartNodeID().equals(node) && p.getEndNodeID().equals(end));
//                                System.out.println("remove path: "+ node + " -> " + end );
                                for(String n : this.pathList.get(end)){
                                    if(!this.pathList.get(node).contains(n)){
                                        this.pathList.get(node).add(n);
                                        newProcessPaths.removeIf(p -> p.getStartNodeID().equals(node) && p.getEndNodeID().equals(n));
                                        newProcessPaths.add(new ProcessPath(utils.getRandomString(5), node, n));
//                                        System.out.println("add path: "+ node + " -> " + n);
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
//        ArrayList<Integer> pathList = new ArrayList<>();
//        for (int i = 0; i < newProcessPaths.size(); i++) {
//            ProcessPath path = newProcessPaths.get(i);
//            boolean flag = false;
//            String start = path.getStartNodeID(), end = path.getEndNodeID();
//            for (ConstraintCondition cc : newConstraintConditions) {
//                if (cc.getNodeID1().equals(start) && cc.getNodeID2().equals(end)) {
//                    flag = true;
//                    break;
//                }
//            }
//            if (!flag) {
//                pathList.add(i);
////                this.pathList.get(start).remove(end);
////                newProcessPaths.remove(path);
////                System.out.println("remove path: " + start + " -> " + end);
//            }
//        }
//        for(Integer i: pathList){
//            ProcessPath path = newProcessPaths.get(i);
//            this.pathList.get(path.getStartNodeID()).remove(path.getEndNodeID());
//            newProcessPaths.remove(path);
////            System.out.println("remove path: " + path.getStartNodeID() + " -> " + path.getEndNodeID());
//        }
        ProcessMap newMap =  new ProcessMap(newMapID, newMultiNodes, newProcessPaths, newConstraintConditions, newEquipments);
        newMap.setTotalTime(totalTime);
        newMap.setTotalPrecision(totalPrecision);
        newMap.setTotalCost(totalCost);
        return newMap;
    }
    public ProcessMap OptimizeMap(ProcessMap map, int[] factor, int x2){  // 根据同一关系的特点，遍历同一关系的节点能否在流程图中完成替换
        ArrayList<MultiNode> multiNodes = map.getMultiNodes();
        LinkedList<ProcessPath> processPaths = map.getProcessPaths();
        ArrayList<ConstraintCondition> constraintConditions = map.getConstraintConditions();
        ArrayList<Equipment> equipments = map.getEquipments();
        if(x2 == 1){
            Collections.reverse(constraintConditions);
        } else if(x2 == 2){
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
//        System.out.println("\nold data:\n time:"+ oldTime + "\n precision:"+oldPrecision+"\n cost:"+oldCost);
        double optimizationRatio = 0.0;
//        System.out.println("old ratio:" + optimizationRatio);
        double temp;
        int sum = factor[0] + factor[1] + factor[2];
        double factor1 = (double) factor[0] / sum;
        double factor2 = (double) factor[1] / sum;
        double factor3 = (double) factor[2] / sum;
        for(ConstraintCondition cc : constraintConditions){
            if(cc.getConditionType() == Constant.SAME) {
                if(this.sortNodes.contains(cc.getNodeID1())){
                    LinkedList<ProcessPath> paths = updateProcessPaths(processPaths, cc.getNodeID1(), cc.getNodeID2());
//                    System.out.println(this.sortNodes);
                    ArrayList<String> sortNodes1 = updateSortNodes(this.sortNodes, cc.getNodeID1(), cc.getNodeID2());
//                    System.out.println(this.sortNodes);
                    int newTime = getTotalTime(multiNodes, paths, sortNodes1);
                    double newPrecision = getTotalPrecision(multiNodes, sortNodes1);
                    int newCost = getTotalCost(multiNodes,sortNodes1);
//                    System.out.println("compare "+ cc.getNodeID1() + " and "+cc.getNodeID2());
//                    System.out.println("new data:\n time:"+ newTime + "\n precision:"+newPrecision+"\n cost:"+newCost);
                    temp = (double) (oldTime - newTime) / oldTime * factor1 + (newPrecision - oldPrecision) * factor2 + (double) (oldCost - newCost) / oldCost * factor3;
//                    System.out.println("new ratio:"+ temp);
                    if(temp > optimizationRatio){
//                        System.out.println("use node: "+ cc.getNodeID2());
                        newProcessPaths = paths;
                        this.sortNodes = sortNodes1;
                        optimizationRatio = temp;
                    }
                }
                else if(this.sortNodes.contains(cc.getNodeID2())){
                    LinkedList<ProcessPath> paths = updateProcessPaths(processPaths, cc.getNodeID2(), cc.getNodeID1());
//                    System.out.println(this.sortNodes);
                    ArrayList<String> sortNodes1 = updateSortNodes(this.sortNodes, cc.getNodeID2(), cc.getNodeID1());
//                    System.out.println(this.sortNodes);
                    int newTime = getTotalTime(multiNodes, paths, sortNodes1);
                    double newPrecision = getTotalPrecision(multiNodes, sortNodes1);
                    int newCost = getTotalCost(multiNodes,sortNodes1);
//                    System.out.println("compare "+ cc.getNodeID1() + " and "+cc.getNodeID2());
//                    System.out.println("new data:\n time:"+ newTime + "\n precision:"+newPrecision+"\n cost:"+newCost);
                    temp = (double) (oldTime - newTime) / oldTime * factor1 + (newPrecision - oldPrecision) * factor2 + (double) (oldCost - newCost) / oldCost * factor3;
                    if(temp > optimizationRatio){
//                        System.out.println("use node: "+ cc.getNodeID1());
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
//        double ratio = (double) (oldTime - time) / oldTime * factor1 + (precision - oldPrecision) * factor2 + (double) (oldCost - cost) / oldCost * factor3;
//        System.out.println("old data:\n time:"+ oldTime + "\n precision:"+String.format("%.2f",oldPrecision) +"\n cost:"+oldCost);
//        System.out.println("new data:\n time:"+ time + "\n precision:"+String.format("%.2f",precision) +"\n cost:"+cost);
//        System.out.println("optimization ratio: " + String.format("%.2f",ratio));
        newMap.setTotalTime(time);
        newMap.setTotalPrecision(precision);
        newMap.setTotalCost(cost);
        return newMap;
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
//        System.out.println(res);
        return res;
    }
    public ArrayList<String> getNodeWithConstraint(String to, ArrayList<ConstraintCondition> list){  // 根据节点的nodeID,获取关联的约束
        ArrayList<String> res = new ArrayList<>();
        for(ConstraintCondition c : list){
            if(c.getNodeID2().equals(to) && c.getConditionType()==Constant.FOLLOW && this.sortNodes.contains(c.getNodeID1())){
                res.add(c.getNodeID1());
            }
        }
        res.removeIf(node -> !this.sortNodes.contains(node));
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
//            System.out.println(node);
//            System.out.println(map.get(node));
            for(String n : map.get(node)){
                inDegree.put(n, inDegree.get(n) - 1);
            }
        }
        return res;
    }
    public ArrayList<String> getLastTwoNode(String from, String to, Map<String, ArrayList<String>> map){ // 深度优先遍历，返回from到to的所有路径的倒数第二个节点
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
    public ArrayList<String> adjustSortNodes(ArrayList<String> nodes, Map<String, ArrayList<String>> paths, int x1) {
        ArrayList<String> res = new ArrayList<>();
        if(x1 == 0) {
            res = nodes;
        } else if(x1 == 1) {
            for(String node: paths.keySet()){
                ArrayList<String> list = paths.get(node);
                Collections.reverse(list);
                paths.put(node, list);
            }
            res = getSortNodes(paths);
        }
        else if(x1 == 2) {
            for(String node: paths.keySet()){
                ArrayList<String> list = paths.get(node);
                Collections.shuffle(list);
                paths.put(node, list);
            }
            res = getSortNodes(paths);
        }
        return res;
    }
}
