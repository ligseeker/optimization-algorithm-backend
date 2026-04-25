package com.example.optimization_algorithm_backend.Service.Impl;

import com.example.optimization_algorithm_backend.Service.AlgorithmService;
import com.example.optimization_algorithm_backend.Service.NodeService;
import com.example.optimization_algorithm_backend.algorithm.model.ConstraintCondition;
import com.example.optimization_algorithm_backend.algorithm.model.MultiNode;
import com.example.optimization_algorithm_backend.algorithm.model.ProcessMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static com.example.optimization_algorithm_backend.algorithm.Main.writeData;

@Service
public class NodeServiceImpl implements NodeService {
    @Value("${path.historyPath}")
    String historyPath;
    @Value("${path.inputPath}")
    String inputPath;
    @Value("${path.outputPath}")
    String outputPath;
    @Autowired
    private AlgorithmService algorithmService;

    @Override
    public void addNodeInMap(String nodeID, String nodeDescription, int time, double precision, int cost, String example){
        String path =  historyPath + example + ".yaml";
        ProcessMap map = algorithmService.pathToMap(path);
        map.getMultiNodes().add(new MultiNode(nodeID, nodeDescription, "", time, precision, cost));
        writeData(map, path);
    }

    @Override
    public void deleteNodeInMap(String nodeID, String example){
        String path = historyPath + example + ".yaml";
        ProcessMap map = algorithmService.pathToMap(path);
        map.getMultiNodes().removeIf(p -> p.getNodeID().equals(nodeID));
        map.getProcessPaths().removeIf(p -> p.getStartNodeID().equals(nodeID) || p.getEndNodeID().equals(nodeID));
        map.getConstraintConditions().removeIf(p -> p.getNodeID1().equals(nodeID) || p.getNodeID2().equals(nodeID));
        writeData(map, path);
    }

    @Override
    public void updateNodeInMap(String nodeID, String nodeDescription, int time, double precision, int cost, String example){
        String path = historyPath + example + ".yaml";
        ProcessMap map = algorithmService.pathToMap(path);
        map.getMultiNodes().removeIf(p -> p.getNodeID().equals(nodeID));
        map.getMultiNodes().add(new MultiNode(nodeID, nodeDescription, "", time, precision, cost));
        writeData(map, path);
    }

    @Override
    public MultiNode findNodeInMap(String nodeID, String type, String example){
        MultiNode res;
        String path;
        switch (type) {
            case "history":
                path = historyPath + example + ".yaml";
                break;
            case "input":
                path = inputPath + example + ".yaml";
                break;
            case "output":
                path = outputPath + example + ".yaml";
                break;
            default:
                return null;
        }
        ProcessMap map = algorithmService.pathToMap(path);
        for(MultiNode node: map.getMultiNodes()){
            if(node.getNodeID().equals(nodeID)){
                res = node;
                return res;
            }
        }
        return null;
    }
    @Override
    public ArrayList<ConstraintCondition> getCCbyNodeID(String nodeID, String type, String example){
        ArrayList<ConstraintCondition> res = new ArrayList<>();
        String path;
        switch (type) {
            case "history":
                path = historyPath + example + ".yaml";
                break;
            case "input":
                path = inputPath + example + ".yaml";
                break;
            case "output":
                path = outputPath + example + ".yaml";
                break;
            default:
                return null;
        }
        ProcessMap map = algorithmService.pathToMap(path);
        for(ConstraintCondition cc: map.getConstraintConditions()){
            if(cc.getNodeID1().equals(nodeID) || cc.getNodeID2().equals(nodeID)){
                res.add(cc);
            }
        }
        return res;
    }
}
