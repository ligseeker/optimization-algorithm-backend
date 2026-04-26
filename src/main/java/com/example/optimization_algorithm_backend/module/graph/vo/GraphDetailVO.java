package com.example.optimization_algorithm_backend.module.graph.vo;

import com.example.optimization_algorithm_backend.module.constraint.vo.ConstraintVO;
import com.example.optimization_algorithm_backend.module.equipment.vo.EquipmentVO;
import com.example.optimization_algorithm_backend.module.node.vo.NodeVO;
import com.example.optimization_algorithm_backend.module.path.vo.PathVO;

import java.util.List;

public class GraphDetailVO {

    private GraphVO graph;
    private List<NodeVO> nodes;
    private List<PathVO> paths;
    private List<EquipmentVO> equipments;
    private List<ConstraintVO> constraints;

    public GraphVO getGraph() {
        return graph;
    }

    public void setGraph(GraphVO graph) {
        this.graph = graph;
    }

    public List<NodeVO> getNodes() {
        return nodes;
    }

    public void setNodes(List<NodeVO> nodes) {
        this.nodes = nodes;
    }

    public List<PathVO> getPaths() {
        return paths;
    }

    public void setPaths(List<PathVO> paths) {
        this.paths = paths;
    }

    public List<EquipmentVO> getEquipments() {
        return equipments;
    }

    public void setEquipments(List<EquipmentVO> equipments) {
        this.equipments = equipments;
    }

    public List<ConstraintVO> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<ConstraintVO> constraints) {
        this.constraints = constraints;
    }
}
