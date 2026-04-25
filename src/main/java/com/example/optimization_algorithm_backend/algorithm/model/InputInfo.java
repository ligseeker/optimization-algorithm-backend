package com.example.optimization_algorithm_backend.algorithm.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

// InputInfo 类, 用于存储输入信息
public class InputInfo {
    ArrayList<ProcessNode> processNodes;  //流程节点
    ArrayList<ConstraintCondition> constraintConditions;  //约束条件
    LinkedList<ProcessPath> processPaths;  //流程路径
    ArrayList<Equipment> equipments;  //装备信息

    int[] CalculationFactor;  // 计算因子，表示计算优化目标时各个指标的权重
    public InputInfo(ArrayList<ProcessNode> processNodes, ArrayList<ConstraintCondition> constraintConditions, LinkedList<ProcessPath> processPaths,ArrayList<Equipment> equipments, int[] calculationFactor) {
        this.processNodes = processNodes;
        this.constraintConditions = constraintConditions;
        this.processPaths = processPaths;
        this.equipments = equipments;
        this.CalculationFactor = calculationFactor;
    }

    public String toString() {
        return "InputInfo{" +
                "processNodes=" + processNodes +
                ", constraintConditions=" + constraintConditions +
                ", processPaths=" + processPaths +
                ", equipments=" + equipments +
                ", CalculationFactor=" + Arrays.toString(CalculationFactor) +
                '}';
    }

    public int[] getCalculationFactor() {
        return CalculationFactor;
    }

    public ArrayList<ProcessNode> getProcessNodes() {
        return processNodes;
    }

    public ArrayList<ConstraintCondition> getConstraintConditions() {
        return constraintConditions;
    }

    public LinkedList<ProcessPath> getProcessPaths() {
        return processPaths;
    }

    public ArrayList<Equipment> getEquipments() {
        return equipments;
    }

    public void setEquipments(ArrayList<Equipment> equipments) {
        this.equipments = equipments;
    }
}
