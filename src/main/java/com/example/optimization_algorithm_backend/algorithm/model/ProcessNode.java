package com.example.optimization_algorithm_backend.algorithm.model;


// ProcessNode 类, 用于存储流程节点信息
public class ProcessNode {
    String nodeID; // 节点
    String nodeDescription;  // 节点描述
    String equipmentName;  // 装备名称
    int time;             // 节点时间
    double precision;        // 节点精度
    int cost;             // 节点成本

    public ProcessNode(String nodeID, String nodeDescription, String equipmentName, int time, double precision, int cost) {
        this.nodeID = nodeID;
        this.nodeDescription = nodeDescription;
        this.equipmentName = equipmentName;
        this.time = time;
        this.precision = precision;
        this.cost = cost;
    }

    public String toString() {
        return "ProcessNode{" +
                "nodeID='" + nodeID + '\'' +
                ", nodeDescription='" + nodeDescription + '\'' +
                ", equipmentName='" + equipmentName + '\'' +
                ", time=" + time +
                ", precision=" + precision +
                ", cost=" + cost +
                '}';
    }

    public String getNodeID() {
        return nodeID;
    }

    public String getNodeDescription() {
        return nodeDescription;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }
}
