package com.example.optimization_algorithm_backend.Service;

import com.example.optimization_algorithm_backend.algorithm.algorithm1.Algorithm1;
import com.example.optimization_algorithm_backend.algorithm.algorithm2.Algorithm2;
import com.example.optimization_algorithm_backend.algorithm.algorithm3.Algorithm3;
import com.example.optimization_algorithm_backend.algorithm.model.ConstraintCondition;
import com.example.optimization_algorithm_backend.algorithm.model.ProcessMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface AlgorithmService {
    public String saveFile(MultipartFile file, String filePath);
    public ProcessMap pathToMap(String path);
    public ProcessMap optimizeMap1(ProcessMap map, String example, Algorithm1 algorithm, int[] factors, int x1, int x2);
    public Map<String, Object> mapToOutput(ProcessMap processMap, Algorithm1 algorithm);
    public ProcessMap inputToMap(Map<String, Object> input);
    public String getMapCode(ProcessMap processMap);
    public ProcessMap optimizeMap2(ProcessMap map, String example, Algorithm2 algorithm, int[] factors, int x1, int x2);
    public ProcessMap optimizeMap3(ProcessMap map, String example, Algorithm3 algorithm, int[] factors, int x1, int x2);

    public ConstraintCondition checkMap(ProcessMap oldMap);
}
