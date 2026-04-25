package com.example.optimization_algorithm_backend.algorithm.model;

import java.util.ArrayList;
import java.util.LinkedList;


// ProcessMap 类, 用于存储流程图信息
public class ProcessMap {
    String mapID;  // 流程图 ID
    ArrayList<MultiNode> multiNodes;    // 流程节点
    LinkedList<ProcessPath> processPaths;  // 流程路径
    ArrayList<ConstraintCondition> constraintConditions;  // 约束条件
    ArrayList<Equipment> equipments;  // 装备信息
    int totalTime = 0;  // 总时间
    double totalPrecision = 0;  // 总精度
    int totalCost = 0;  // 总成本

    public ProcessMap() {}
    public ProcessMap(String mapID, ArrayList<MultiNode> multiNodes, LinkedList<ProcessPath> processPaths, ArrayList<ConstraintCondition> constraintConditions, ArrayList<Equipment> equipments) {
        this.mapID = mapID;
        this.multiNodes = multiNodes;
        this.processPaths = processPaths;
        this.constraintConditions = constraintConditions;
        this.equipments = equipments;
    }


    public ArrayList<MultiNode> getMultiNodes() {
        return multiNodes;
    }

    public LinkedList<ProcessPath> getProcessPaths() {
        return processPaths;
    }

    public ArrayList<ConstraintCondition> getConstraintConditions() {
        return constraintConditions;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public double getTotalPrecision() {
        return totalPrecision;
    }

    public void setTotalPrecision(double totalPrecision) {
        this.totalPrecision = totalPrecision;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(int totalCost) {
        this.totalCost = totalCost;
    }

    public void setMultiNodes(ArrayList<MultiNode> multiNodes) {
        this.multiNodes = multiNodes;
    }

    public void setProcessPaths(LinkedList<ProcessPath> processPaths) {
        this.processPaths = processPaths;
    }

    public void setConstraintConditions(ArrayList<ConstraintCondition> constraintConditions) {
        this.constraintConditions = constraintConditions;
    }

    public ArrayList<Equipment> getEquipments() {
        return equipments;
    }

    public void setEquipments(ArrayList<Equipment> equipments) {
        this.equipments = equipments;
    }
}
