package com.example.optimization_algorithm_backend.Service;

import java.util.ArrayList;

public interface PathService {
    void addPathInMap(String nodeId1, String nodeId2, String example);
    void deletePathInMap(String nodeId1, String nodeId2, String example);
    boolean findPathInMap(String nodeId1, String nodeId2, String example);

    ArrayList<String> findPathNodeList(String node,String example);

}
