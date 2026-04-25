package com.example.optimization_algorithm_backend.algorithm.model;


// ProcessPath 类, 用于存储流程路径信息
public class ProcessPath {
    String pathID;     // 路径 ID
    String startNodeID;      // 起始节点 ID
    String endNodeID;        // 终止节点 ID

    public ProcessPath(String pathID, String startNodeID, String endNodeID) {
        this.pathID = pathID;
        this.startNodeID = startNodeID;
        this.endNodeID = endNodeID;
    }

    public String toString() {
        return "ProcessPath{" +
                "pathID='" + pathID + '\'' +
                ", startNodeID='" + startNodeID + '\'' +
                ", endNodeID='" + endNodeID + '\'' +
                '}';
    }

    public String getStartNodeID() {
        return startNodeID;
    }

    public String getEndNodeID() {
        return endNodeID;
    }
    public void setStartNodeID(String startNodeID) {
        this.startNodeID = startNodeID;
    }
    public void setEndNodeID(String endNodeID) {
        this.endNodeID = endNodeID;
    }

    public String getPathID() {
        return pathID;
    }
}
