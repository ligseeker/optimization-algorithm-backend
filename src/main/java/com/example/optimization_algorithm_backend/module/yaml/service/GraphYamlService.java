package com.example.optimization_algorithm_backend.module.yaml.service;

import com.example.optimization_algorithm_backend.module.yaml.vo.GraphImportResponse;
import com.example.optimization_algorithm_backend.module.yaml.vo.GraphYamlExportResponse;
import org.springframework.web.multipart.MultipartFile;

public interface GraphYamlService {

    GraphImportResponse importGraph(Long workspaceId, String graphName, MultipartFile file);

    GraphYamlExportResponse exportGraphYaml(Long graphId);
}
