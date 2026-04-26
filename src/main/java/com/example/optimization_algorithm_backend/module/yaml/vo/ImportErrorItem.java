package com.example.optimization_algorithm_backend.module.yaml.vo;

public class ImportErrorItem {

    private String code;
    private String location;
    private String message;

    public ImportErrorItem() {
    }

    public ImportErrorItem(String code, String location, String message) {
        this.code = code;
        this.location = location;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
