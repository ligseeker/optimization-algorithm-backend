package com.example.optimization_algorithm_backend.Controller;

import com.example.optimization_algorithm_backend.Service.AlgorithmService;
import com.example.optimization_algorithm_backend.algorithm.algorithm1.Algorithm1;
import com.example.optimization_algorithm_backend.algorithm.model.ProcessMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class AlgorithmController {

    @Autowired
    private AlgorithmService algorithm1Service;
    @Value("${path.historyPath}")
    String historyPath;
    @Value("${path.inputPath}")
    String inputPath;
    @Value("${path.outputPath}")
    String outputPath;
    @Value("${path.tempPath}")
    String tempPath;

    @PostMapping("/optimizeByFile")  // 根据上传的文件获取输入数据，输出优化后的结果
    public Map<String, Object> OptimizeByFile(@RequestParam("file") MultipartFile file){
        Map<String, Object> res = new HashMap<>();
        if (file.isEmpty()) {
            res.put("code", 500);
            res.put("msg", "文件为空");
            return res;
        }
        try {
            String filePath = historyPath + file.getOriginalFilename();
            String path = algorithm1Service.saveFile(file, filePath);
            ProcessMap oldMap = algorithm1Service.pathToMap(path);
            Algorithm1 algorithm = new Algorithm1();
            algorithm.initAlgorithm(oldMap);
//            ConstraintCondition cc = algorithm.IsSatisfyConstraints(oldMap);
//            if(cc != null){
//                res.put("code", 500);
//                res.put("msg", "优化失败，流程图不符合约束");
//                res.put("data", cc);
//                return res;
//            }
            int[] factors = new int[3];
            factors[0] = 1;
            factors[1] = 1;
            factors[2] = 1;
            int x1 = 0;
            int x2 = 0;
            ProcessMap newMap = algorithm1Service.optimizeMap1(oldMap, "test", algorithm, factors, x1, x2);
            Map<String, Object> output = algorithm1Service.mapToOutput(newMap, algorithm);
            res.put("code", 200);
            res.put("msg", "优化成功");
            res.put("data", output);
        } catch (Exception e){
            e.printStackTrace();
            res.put("code", 500);
            res.put("msg", "优化失败");
        }
        return res;
    }

    @PostMapping("/optimizeByInput") // 根据输入数据，输出优化后的结果
    public Map<String, Object> OptimizeByInput(@RequestBody Map<String, Object> re_map){
        Map<String, Object> res = new HashMap<>();
        try{
            ProcessMap oldMap = algorithm1Service.inputToMap(re_map);
            Algorithm1 algorithm = new Algorithm1();
            algorithm.initAlgorithm(oldMap);
            int[] factors = new int[3];
            factors[0] = 1;
            factors[1] = 1;
            factors[2] = 1;
            int x1 = 0;
            int x2 = 0;
            ProcessMap newMap = algorithm1Service.optimizeMap1(oldMap, "test", algorithm, factors, x1, x2);
            Map<String, Object> output = algorithm1Service.mapToOutput(newMap, algorithm);
            res.put("code", 200);
            res.put("msg", "优化成功");
            res.put("data", output);
        } catch (Exception e){
            e.printStackTrace();
            res.put("code", 500);
            res.put("msg", "优化失败");
        }
        return res;
    }

    @PostMapping("/uploadFile")
    public Map<String, Object> UploadFile(@RequestParam("file") MultipartFile file){
        Map<String, Object> res = new HashMap<>();
        if (file.isEmpty()) {
            res.put("code", 500);
            res.put("msg", "文件为空");
            return res;
        }
        try {
            String filePath = historyPath + file.getName();
            String path = algorithm1Service.saveFile(file, filePath);
            ProcessMap oldMap = algorithm1Service.pathToMap(path);
            Algorithm1 algorithm = new Algorithm1();
            algorithm.initAlgorithm(oldMap);
            String mapCode = algorithm1Service.getMapCode(oldMap);
            res.put("value", algorithm.getValue(oldMap).get("oldValue"));
            res.put("code", 200);
            res.put("msg", "上传成功");
            res.put("mapCode", mapCode);
        } catch (Exception e) {
            e.printStackTrace();
            res.put("code", 500);
            res.put("msg", "上传失败");
        }
        return  res;
    }
    @GetMapping("/downloadFile")//没有参数，返回temp文件夹下的output.yaml文件，不能返回map
    public ResponseEntity<Resource> DownloadFile(@RequestParam Map<String, Object> re_map){
        try {
            String type = (String) re_map.get("type");
            String example = (String) re_map.get("example");

            Path filePath;
            switch (type) {
                case "history":
                    filePath = Paths.get(historyPath + example + ".yaml");
                    break;
                case "input":
                    filePath = Paths.get(inputPath + example + ".yaml");
                    break;
                case "output":
                    filePath = Paths.get(outputPath + example + ".yaml");
                    break;
                case "temp":
                    filePath = Paths.get(tempPath + example + ".yaml");
                    break;
                default:
                    throw new RuntimeException("Error downloading the file!");
            }
//            System.out.println(filePath.toString());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_PLAIN)
                        .body(resource);
            } else {
                throw new RuntimeException("Could not find the file!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error downloading the file: " + e.getMessage());
        }
    }

    @RestController //返回数据全部为json格式，@Controller+@ResponseBody
    public static  class HelloController {
        @RequestMapping("/hello") //有@GetMapping、@PostMapping等,@RequestMapping(value="/hello",method=RequestMethod.GET)
        public String Hello(){
            return "Hello, World!!!!!!!!!!!!!";
        }

    }

}
