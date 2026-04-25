package com.example.optimization_algorithm_backend.algorithm.model;

import java.util.ArrayList;

public class MultiNode {
    String nodeID; // 节点
    ArrayList<MultiNode> nodeList;
    String nodeDescription;  // 节点描述
    String equipmentName;       // 装备
    Constant constant; // 节点关系类型
    int time;             // 节点时间
    double precision;     // 节点精度
    int cost;             // 节点成本

    public MultiNode(String nodeID, Constant constant, ArrayList<MultiNode> nodeList, String equipment) {
        this.nodeID = nodeID;
        this.nodeList = nodeList;
        this.constant = constant;
        this.equipmentName = equipment;
    }
    public MultiNode(String nodeID, String nodeDescription, String equipment,  int time, double precision, int cost) {
        this.nodeID = nodeID;
        this.nodeDescription = nodeDescription;
        this.time = time;
        this.precision = precision;
        this.constant = Constant.NORMAL;
        this.nodeList = new ArrayList<>();
        this.cost = cost;
        this.equipmentName = equipment;
    }

    public String getNodeID() {
        return nodeID;
    }

    public int getTime() {
        return time;
    }
    public double getPrecision() {
        return precision;
    }
    public int getCost() {
        return cost;
    }

    public String getNodeDescription() {
        return nodeDescription;
    }

    public ArrayList<String> getNodeList() {
        ArrayList<String> ans = new ArrayList<>();
        if(this.nodeList == null){
            return ans;
        }
        for(MultiNode node : this.nodeList){
            ans.add(node.getNodeID());
        }
        return ans;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public Constant getConstant() {
        return constant;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }
}
