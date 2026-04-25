package com.example.optimization_algorithm_backend.algorithm.model;

import java.util.ArrayList;

public class Equipment {
    String name;
    ArrayList<String> nodes;
    String color;
    String description;

    String imagePath;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Equipment() {}
    public Equipment(String name, ArrayList<String> nodes, String color) {
        this.name = name;
        this.nodes = nodes;
        this.color = color;
        this.description = "";
        this.imagePath = "";
    }
    public Equipment(String name, ArrayList<String> nodes, String color,String description) {
        this.name = name;
        this.nodes = nodes;
        this.color = color;
        this.description = description;
        this.imagePath = "";
    }
    public Equipment(String name, ArrayList<String> nodes, String color,String description,String imagePath) {
        this.name = name;
        this.nodes = nodes;
        this.color = color;
        this.description = description;
        this.imagePath = imagePath;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public ArrayList<String> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<String> nodes) {
        this.nodes = nodes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
