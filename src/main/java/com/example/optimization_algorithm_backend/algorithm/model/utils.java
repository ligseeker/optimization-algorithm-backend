package com.example.optimization_algorithm_backend.algorithm.model;

import java.util.*;

import static com.example.optimization_algorithm_backend.algorithm.model.Constant.*;

// 工具类
public class utils {
    // 根据InputInfo初始化ProcessMap
    public static ProcessMap initProcessMap(InputInfo inputInfo) {
        ArrayList<MultiNode> processNodes = new ArrayList<>();
        ArrayList<ConstraintCondition> constraintConditions = inputInfo.constraintConditions;
        LinkedList<ProcessPath> processPaths = inputInfo.processPaths;
        ArrayList<Equipment> equipments = inputInfo.equipments;
        String mapID = getRandomString(10);
        //  将ProcessNode类转换为MultiNode类
        for (ProcessNode p : inputInfo.processNodes) {
            MultiNode multiNode = new MultiNode(p.nodeID, p.nodeDescription, p.equipmentName, p.time, p.precision, p.cost);
            processNodes.add(multiNode);
        }
        // 按照约束合并节点，对流程图进行优化
        // 使用迭代器遍历约束条件，方便删除元素
        Iterator<ConstraintCondition> conditionIterator = constraintConditions.iterator();
        while (conditionIterator.hasNext()) {
            ConstraintCondition c = conditionIterator.next();
            MultiNode multiNode;
            MultiNode node1, node2;
            switch (c.conditionType) {
                case CONNECT:
//                    multiNode = new MultiNode(getRandomString(5), Constant.CONNECT, new ArrayList<>(), "");
                    //查找并返回nodeID1和nodeID2对应的multiNode
                    node1 = processNodes.stream().filter(m -> m.nodeID.equals(c.nodeID1)).findFirst().orElse(null);
                    node2 = processNodes.stream().filter(m -> m.nodeID.equals(c.nodeID2)).findFirst().orElse(null);
                    multiNode = new MultiNode(c.nodeID1 + c.nodeID2, CONNECT, new ArrayList<>(), "");
                    // 计算大节点的时间、精度、成本
                    if (node1 != null && node2 != null) {
                        multiNode.time = node1.time + node2.time;
                        multiNode.precision = node1.precision * node2.precision;
                        multiNode.cost = node1.cost + node2.cost;
                    }
                    // 将节点添加到multiNode的nodeList中
                    if (node1 != null && node1.nodeList.size() != 0) {
                        multiNode.nodeList.addAll(node1.nodeList);
                        //  如果node1是合并节点的中间节点，需要删除
                        processNodes.remove(node1);
                    } else
                        multiNode.nodeList.add(node1);
                    if (node2 != null && node2.nodeList.size() != 0) {
                        multiNode.nodeList.addAll(node2.nodeList);
                        //  如果node1是合并节点的中间节点，需要删除
                        processNodes.remove(node2);
                    } else
                        multiNode.nodeList.add(node2);
                    processNodes.add(multiNode);
                    // 移除已处理的约束条件
                    conditionIterator.remove();
                    // 约束转移到大节点
                    for (ConstraintCondition cc : constraintConditions) {
                        if (cc.nodeID1.equals(c.nodeID1) || cc.nodeID1.equals(c.nodeID2))
                            cc.nodeID1 = multiNode.nodeID;
                        if (cc.nodeID2.equals(c.nodeID1) || cc.nodeID2.equals(c.nodeID2))
                            cc.nodeID2 = multiNode.nodeID;
                    }
                    // 修改路径，将原路径中的nodeID1和nodeID2替换为multiNode.nodeID
                    Iterator<ProcessPath> iterator = processPaths.iterator();
                    while (iterator.hasNext()) {
                        ProcessPath p = iterator.next();
                        if (p.startNodeID.equals(c.nodeID1) && p.endNodeID.equals(c.nodeID2)) {
                            iterator.remove(); // 安全地删除元素
                        } else if (p.endNodeID.equals(c.nodeID1) || p.endNodeID.equals(c.nodeID2)) {
                            for (ProcessPath p1 : processPaths) {
                                if (p1.startNodeID.equals(p.startNodeID) && p1.endNodeID.equals(multiNode.nodeID)) {
                                    iterator.remove();
                                    break;
                                }
                            }
                            p.endNodeID = multiNode.nodeID;
                        } else if (p.startNodeID.equals(c.nodeID2) || p.startNodeID.equals(c.nodeID1)) {
                            for (ProcessPath p1 : processPaths) {
                                if (p1.startNodeID.equals(multiNode.nodeID) && p1.endNodeID.equals(p.endNodeID)) {
                                    iterator.remove();
                                    break;
                                }
                            }
                            p.startNodeID = multiNode.nodeID;
                        }
                    }
//                    // 使用增强型for循环删除元素会报错
//                    for (ProcessPath p : processPaths) {
//                        if (p.startNodeID.equals(c.nodeID1) && p.endNodeID.equals(c.nodeID2)) {
//                            processPaths.remove(p);
//                        } else if (p.endNodeID.equals(c.nodeID1)) {
//                            p.endNodeID = multiNode.nodeID;
//                        } else if (p.startNodeID.equals(c.nodeID2)) {
//                            p.startNodeID = multiNode.nodeID;
//                        }
//                    }
                    break;
                case SAME:
                    // 同一关系不合并`
                    break;
                case CONTAIN:
//                    // 包含关系需要将小流程的约束关系转移至大流程上，作为合并节点的约束关系，
//                    // 被包含节点不会直接出现在流程图中，而是通过条件约束出现，用于链路优化。
////                    multiNode = new MultiNode(getRandomString(5), Constant.CONTAIN, new ArrayList<>(), "");
//                    multiNode = new MultiNode(c.nodeID1+c.nodeID2, Constant.CONTAIN, new ArrayList<>(), "");
//                    // 查找并返回nodeID1和nodeID2对应的multiNode
//                    node1 = processNodes.stream().filter(m -> m.nodeID.equals(c.nodeID1)).findFirst().orElse(null);
//                    node2 = processNodes.stream().filter(m -> m.nodeID.equals(c.nodeID2)).findFirst().orElse(null);
//                    // 合并节点时，时间使用大流程的时间（即小流程时间小于大流程时间）精度需要相乘，成本直接相加
//                    if (node1 != null && node2 != null) {
//                        multiNode.time = node1.time;
//                        multiNode.precision = node1.precision * node2.precision;
//                        multiNode.cost = node1.cost + node2.cost;
//                    }
//                    // 添加到processNodes
//                    multiNode.nodeList.add(node1);
//                    multiNode.nodeList.add(node2);
//                    processNodes.add(multiNode);
//                    // 移除已处理的约束条件
//                    conditionIterator.remove();
//                    // 修改约束条件中的nodeID
//                    for (ConstraintCondition cc : constraintConditions) {
//                        if (cc.nodeID1.equals(c.nodeID1) || cc.nodeID1.equals(c.nodeID2)) {
//                            cc.nodeID1 = multiNode.nodeID;
//                        }
//                        if (cc.nodeID2.equals(c.nodeID1) || cc.nodeID2.equals(c.nodeID2)) {
//                            cc.nodeID2 = multiNode.nodeID;
//                        }
//                    }
//                    // 修改流程图中大流程的节点为合并后的大节点
//                    for (ProcessPath p : processPaths) {
//                        if (p.startNodeID.equals(c.nodeID1)) {
//                            p.startNodeID = multiNode.nodeID;
//                        }
//                        if (p.endNodeID.equals(c.nodeID1)) {
//                            p.endNodeID = multiNode.nodeID;
//                        }
//                    }
                    break;
                case FOLLOW:
                    // 承接关系不合并
                    break;
                case CALL:
//                    // 调用关系需要将被调用流程的约束关系转移至主流程上，作为合并节点的约束关系，
//                    // 被调用节点不会直接出现在流程图中，而是通过条件约束出现，用于链路优化。
//                    // 合并节点时，时间使用主流程的时间加被调用流程时间，精度需要相乘，成本直接相加
////                    multiNode = new MultiNode(getRandomString(5), Constant.CALL, new ArrayList<>(), "");
//                    multiNode = new MultiNode(c.nodeID1+c.nodeID2, CALL, new ArrayList<>(), "");
//                    //查找并返回nodeID1和nodeID2对应的multiNode
//                    node1 = processNodes.stream().filter(m -> m.nodeID.equals(c.nodeID1)).findFirst().orElse(null);
//                    node2 = processNodes.stream().filter(m -> m.nodeID.equals(c.nodeID2)).findFirst().orElse(null);
//                    // 计算大节点的时间、精度、成本
//                    if (node1 != null && node2 != null) {
//                        multiNode.time = node1.time + node2.time;
//                        multiNode.precision = node1.precision * node2.precision;
//                        multiNode.cost = node1.cost + node2.cost;
//                    }
//                    // 添加到processNodes
//                    multiNode.nodeList.add(node1);
//                    multiNode.nodeList.add(node2);
//                    processNodes.add(multiNode);
//                    // 移除已处理的约束条件
//                    conditionIterator.remove();
//                    // 修改约束条件中的nodeID
//                    for (ConstraintCondition cc : constraintConditions) {
//                        if (cc.nodeID1.equals(c.nodeID1) || cc.nodeID1.equals(c.nodeID2)) {
//                            cc.nodeID1 = multiNode.nodeID;
//                        }
//                        if (cc.nodeID2.equals(c.nodeID1) || cc.nodeID2.equals(c.nodeID2)) {
//                            cc.nodeID2 = multiNode.nodeID;
//                        }
//                    }
//                    // 修改流程图中调用流程的节点为合并后的大节点
//                    // 删除A到B的路径，B到A的路径，修改A的节点为合并后的大节点
//                    Iterator<ProcessPath> iterator_call = processPaths.iterator();
//                    while (iterator_call.hasNext()) {
//                        ProcessPath p = iterator_call.next();
//                        if (p.startNodeID.equals(c.nodeID1) && p.endNodeID.equals(c.nodeID2)) {
//                            iterator_call.remove();
//                        }
//                        if (p.startNodeID.equals(c.nodeID2) && p.endNodeID.equals(c.nodeID1)) {
//                            iterator_call.remove();
//                        }
//                        if (p.startNodeID.equals(c.nodeID1)) {
//                            p.startNodeID = multiNode.nodeID;
//                        }
//                        if (p.endNodeID.equals(c.nodeID1)) {
//                            p.endNodeID = multiNode.nodeID;
//                        }
//                    }
                    break;
                case PARTICIPATE:
                    // 参与关系不合并
                    break;

                default:
                    System.out.println("未知约束类型: " + c.conditionType);
                    break;
            }
        }
        return new ProcessMap(mapID, processNodes, processPaths, constraintConditions, equipments);
    }

    // 将优化后的合并流程图恢复成原始流程图
    public static ProcessMap restoreProcessMap(ProcessMap processMap) {
        ArrayList<MultiNode> processNodes;
        ArrayList<ConstraintCondition> constraintConditions;
        LinkedList<ProcessPath> processPaths;
        ArrayList<Equipment> equipments;

        processNodes = processMap.multiNodes;
        constraintConditions = processMap.constraintConditions;
        processPaths = processMap.processPaths;
        equipments = processMap.equipments;
        Iterator<MultiNode> multiNodeIterator = processNodes.iterator();
        while (multiNodeIterator.hasNext()) {
            MultiNode m = multiNodeIterator.next();
            int listSize = m.nodeList.size();
            switch(m.constant){
                case CONNECT:
                    // 修改约束条件中的nodeID
                    for (ConstraintCondition cc : constraintConditions) {
                        if (cc.nodeID1.equals(m.nodeID)) {
                            cc.nodeID1 = m.nodeList.get(listSize - 1).nodeID;
                        }
                        if (cc.nodeID2.equals(m.nodeID)) {
                            cc.nodeID2 = m.nodeList.get(0).nodeID;
                        }
                    }
                    // 恢复衔接关系流程图
                    if (listSize == 2) {
                        MultiNode node1 = m.nodeList.get(0);
                        MultiNode node2 = m.nodeList.get(1);
                        // 恢复流程图
                        for (ProcessPath p : processPaths) {
                            if (p.startNodeID.equals(m.nodeID)) {
                                p.startNodeID = node2.nodeID;
                            }
                            if (p.endNodeID.equals(m.nodeID)) {
                                p.endNodeID = node1.nodeID;
                            }
                        }
                        ProcessPath path = new ProcessPath(getRandomString(5), node1.nodeID, node2.nodeID);
                        ConstraintCondition ccc = new ConstraintCondition(getRandomString(5), "", CONNECT, node1.nodeID, node2.nodeID);
                        processPaths.add(path);
                        constraintConditions.add(ccc);

                    } else if (listSize > 2) {
                        MultiNode node1 = m.nodeList.get(0);
                        MultiNode node2 = m.nodeList.get(listSize - 1);
                        for (ProcessPath p : processPaths) {
                            if (p.startNodeID.equals(m.nodeID)) {
                                p.startNodeID = node2.nodeID;
                            }
                            if (p.endNodeID.equals(m.nodeID)) {
                                p.endNodeID = node1.nodeID;
                            }
                        }
                        // 恢复剩余的节点之间的衔接关系
                        for (int i = 0; i < listSize - 1; i++) {
                            MultiNode node = m.nodeList.get(i);
                            MultiNode nextNode = m.nodeList.get(i + 1);
                            ProcessPath path = new ProcessPath(getRandomString(5), node.nodeID, nextNode.nodeID);
                            ConstraintCondition ccc = new ConstraintCondition(getRandomString(5), "", CONNECT, node.nodeID, nextNode.nodeID);
                            processPaths.add(path);
                            constraintConditions.add(ccc);
                        }
                    }
                    // 移除合并节点
                    multiNodeIterator.remove();
                    break;
                case SAME:
                    break;
                case CONTAIN:
                    break;
                case FOLLOW:
                    break;
                case CALL:
//                    // 修改约束条件中的nodeID
//                    for (ConstraintCondition cc : constraintConditions) {
//                        if (cc.nodeID1.equals(m.nodeID)) {
//                            cc.nodeID1 = m.nodeList.get(0).nodeID;
//                        }
//                        if (cc.nodeID2.equals(m.nodeID)) {
//                            cc.nodeID2 = m.nodeList.get(0).nodeID;
//                        }
//                    }
//                    // 恢复调用关系流程图
//                    if (listSize == 2) {
//                        MultiNode node1 = m.nodeList.get(0);
//                        MultiNode node2 = m.nodeList.get(1);
//                        // 恢复流程图
//                        for (ProcessPath p : processPaths) {
//                            if (p.startNodeID.equals(m.nodeID)) {
//                                p.startNodeID = node1.nodeID;
//                            }
//                            if (p.endNodeID.equals(m.nodeID)) {
//                                p.endNodeID = node1.nodeID;
//                            }
//                        }
//                        ProcessPath path1 = new ProcessPath(getRandomString(5), node1.nodeID, node2.nodeID);
//                        ProcessPath path2 = new ProcessPath(getRandomString(5), node2.nodeID, node1.nodeID);
//                        ConstraintCondition ccc = new ConstraintCondition(getRandomString(5), "", CALL, node1.nodeID, node2.nodeID);
//                        processPaths.add(path1);
//                        constraintConditions.add(ccc);
//                    } else {
//                        System.out.println("调用关系节点数不为2");
//                    }
                    break;
                case PARTICIPATE:
                    break;
                case NORMAL:
                    break;
                default:
                    System.out.println("未知约束类型: " + m.constant);
                    break;
            }
        }

        return new ProcessMap(getRandomString(10), processNodes, processPaths, constraintConditions, equipments);
    }

    // 生成长度为n的随机字符串
    public static String getRandomString(int n) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            int index = (int) (Math.random() * str.length());
            sb.append(str.charAt(index));
        }
        return sb.toString();
    }

    public static String printProcessMap(ProcessMap processMap) {
        ArrayList<MultiNode> processNodes;
        ArrayList<ConstraintCondition> constraintConditions;
        LinkedList<ProcessPath> processPaths;

        processNodes = processMap.multiNodes;
        constraintConditions = processMap.constraintConditions;
        processPaths = processMap.processPaths;
        StringBuilder sb = new StringBuilder();
        for (ProcessPath map : processPaths) {
            sb.append(map.getStartNodeID()).append(" --> ").append(map.getEndNodeID()).append("\n");
        }
        return sb.toString();
    }


    // 判断该流程图中start节点是否能到达end节点
    public static boolean canReach(LinkedList<ProcessPath> paths, String start, String end) {
        Map<String, ArrayList<String>> graph = new HashMap<>();
        for (ProcessPath path : paths) {
            String from = path.startNodeID;
            String to = path.endNodeID;
            graph.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
        }
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.offer(start);
        visited.add(start);
        while (!queue.isEmpty()) {
            String node = queue.poll();
            if (node.equals(end)) {
                return true;
            }
            if (graph.containsKey(node)) {
                for (String next : graph.get(node)) {
                    if (!visited.contains(next)) {
                        queue.offer(next);
                        visited.add(next);
                    }
                }
            }
        }
        return false;
    }

    public static boolean isSameEquipment(ArrayList<MultiNode> multiNodes, String nodeID1, String nodeID2) {
        String equipmentName1 = "", equipmentName2 = "";
        for(MultiNode multiNode : multiNodes) {
            if (multiNode.nodeID.equals(nodeID1)) {
                equipmentName1 = multiNode.getEquipmentName();
                break;
            }
        }
        for(MultiNode multiNode : multiNodes) {
            if (multiNode.nodeID.equals(nodeID2)) {
                equipmentName2 = multiNode.getEquipmentName();
                break;
            }
        }
        return equipmentName1.equals(equipmentName2);
    }
}
