package com.example.optimization_algorithm_backend.Service;

import com.example.optimization_algorithm_backend.algorithm.model.ConstraintCondition;
import com.example.optimization_algorithm_backend.algorithm.model.MultiNode;

import java.util.ArrayList;

public interface NodeService {
    void addNodeInMap(String nodeID, String nodeDescription, int time, double precision, int cost, String example);
    void deleteNodeInMap(String nodeID, String example);
    void updateNodeInMap(String nodeID, String nodeDescription, int time, double precision, int cost, String example);
    MultiNode findNodeInMap(String nodeID, String type, String example);
    ArrayList<ConstraintCondition> getCCbyNodeID(String nodeID, String type, String example);

}
