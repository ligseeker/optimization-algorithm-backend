package com.example.optimization_algorithm_backend.Controller;

import com.example.optimization_algorithm_backend.Service.AlgorithmService;
import com.example.optimization_algorithm_backend.algorithm.algorithm1.Algorithm1;
import com.example.optimization_algorithm_backend.algorithm.model.ProcessMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.optimization_algorithm_backend.algorithm.Main.*;

@RestController
@CrossOrigin(origins = "*")
public class DirectoryController {  // 目录控制器，用于处理目录相关的请求

    @Autowired
    private AlgorithmService algorithmService;
    @Value("${path.historyPath}")
    String historyPath;
    @Value("${path.inputPath}")
    String inputPath;
    @Value("${path.outputPath}")
    String outputPath;
    @Value("${path.tempPath}")
    String tempPath;

    @GetMapping("/getDirectory")  // 获取目录
    public Map<String, Object> getDirectory() {
        Map<String, Object> res = new HashMap<>();
        ArrayList<String> historyFiles = new ArrayList<>();
        ArrayList<String> inputFiles = new ArrayList<>();
        ArrayList<String> outputFiles = new ArrayList<>();
        Map<String,ArrayList<String>> tempFiles = new HashMap<>();
        try {
            File historyDir = new File(historyPath);
            if (!historyDir.exists()) {
                historyDir.mkdirs();
            }
            File[] historyList = historyDir.listFiles();
            if (historyList != null) {
                for (File file : historyList) {
                    if (file.isFile() && file.getName().endsWith(".yaml")) {
                        historyFiles.add(file.getName().substring(0, file.getName().length() - 5));
                    }
                }
            }
            File inputDir = new File(inputPath);
            if (!inputDir.exists()) {
                inputDir.mkdirs();
            }
            File[] inputList = inputDir.listFiles();
            if (inputList != null) {
                for (File file : inputList) {
                    if (file.isFile() && file.getName().endsWith(".yaml")) {
                        inputFiles.add(file.getName().substring(0, file.getName().length() - 5));
                    }
                }
            }
            File outputDir = new File(outputPath);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            File[] outputList = outputDir.listFiles();
            if (outputList != null) {
                for (File file : outputList) {
                    if (file.isFile() && file.getName().endsWith(".yaml")) {
                        outputFiles.add(file.getName().substring(0, file.getName().length() - 5));
                    }
                }
            }
            File tempDir = new File(tempPath);
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }
            File[] tempList = tempDir.listFiles();
            String tempFilename1="";
            String tempFilename2="";
            String algorithmType="";
            if (tempList != null) {
                for (File file : tempList) {
                    if (file.isFile() && file.getName().endsWith(".yaml")) {
                        // 中间流程图名称 示例 "0_0_0初始链路图"
                        tempFilename1 = file.getName().substring(0, file.getName().length() - 5);
                        // 算法类型 示例 "0_0"
                        algorithmType = numChangeIntoAlgorithm(tempFilename1.substring(0,3));
                        if(algorithmType.equals("wrong")){
                            res.put("code", 500);
                            res.put("msg", "中间流程图目录获取失败");
                        }
                        // 中间流程图名称 示例 "0初始链路图"
                        tempFilename2 = tempFilename1.substring(4);
                        if (tempFiles.containsKey(algorithmType)) {
                            tempFiles.get(algorithmType).add(tempFilename2);
                        } else {
                            ArrayList<String> temp = new ArrayList<>();
                            temp.add(tempFilename2);
                            tempFiles.put(algorithmType, temp);
                        }
                    }
                }
            }
            res.put("historyFiles", historyFiles);
            res.put("inputFiles", inputFiles);
            res.put("outputFiles", outputFiles);
            res.put("tempFiles", tempFiles);
            res.put("code", 200);
            res.put("msg", "获取成功");
        } catch (Exception e) {
            res.put("code", 500);
            res.put("msg", "获取失败");
        }
        return res;
    }

    @GetMapping("filterByExample")
    public Map<String, Object> filterByExample(@RequestParam Map<String, Object> re_map) {
        Map<String, Object> res = new HashMap<>();
        ArrayList<String> outputFiles = new ArrayList<>();
        String example = (String) re_map.get("example");
        if (example == null || example.isEmpty()) {
            res.put("code", 500);
            res.put("msg", "example为空");
            return res;
        }
        File outputDir = new File(outputPath);
        File[] outputList = outputDir.listFiles();
        if (outputList == null) {
            res.put("code", 500);
            res.put("msg", "无输出文件");
            return res;
        }
        for (File file : outputList) {
            if (file.isFile() && file.getName().endsWith(".yaml") && file.getName().startsWith(example + "_")) {
                outputFiles.add(file.getName().substring(0, file.getName().length() - 5));
            }
        }
        if (outputFiles.isEmpty()) {
            res.put("code", 200);
            res.put("msg", "输出文件为空，请先进行优化操作");
        } else {
            res.put("outputFiles", outputFiles);
            res.put("code", 200);
            res.put("msg", "获取成功");
        }
        return res;
    }

    @PostMapping("/getMapCode")
    public Map<String, Object> getMapCode(@RequestParam Map<String, Object> re_map) {
        Map<String, Object> res = new HashMap<>();
        String example = (String) re_map.get("example");
        String type = (String) re_map.get("type");

        String path = "";
        boolean flag_output = false;
        boolean flag_temp = false;
        String algorithmType = "";
        if (Objects.equals(type, "history")) {
            path = historyPath;
        } else if (Objects.equals(type, "input")) {
            path = inputPath;
        } else if (Objects.equals(type, "output")) {
            flag_output = true;
            path = outputPath;
        } else if (Objects.equals(type, "temp")) {
            flag_temp = true;
            path = tempPath;
            algorithmType = (String) re_map.get("algorithm");
        }
        if (example == null || example.isEmpty()) {
            res.put("code", 500);
            res.put("msg", "文件名为空");
            return res;
        }
        // 中间流程图 示例 "0_0_0初始链路图"
        if(flag_temp){
            String tempFilename1 = algorithmChangeIntoNum(algorithmType);
            if(tempFilename1.equals("wrong")){
                res.put("code", 500);
                res.put("msg", "中间流程图获取失败");
            }
            example =  tempFilename1+ "_" + example;
        }
        try {
            path = path + example + ".yaml";
            if (!Paths.get(path).toFile().exists()) {
                res.put("code", 500);
                res.put("msg", "实例不存在");
                return res;
            }
            ProcessMap map = algorithmService.pathToMap(path);
            if (Objects.equals(type, "history")) {
                Map<String, Object> values = new HashMap<>();
                res.put("values", values);
            } else {
                Algorithm1 algorithm = new Algorithm1();
                algorithm.initAlgorithm(map);
                Map<String, Object> values = new HashMap<>();
                if (Objects.equals(type, "input") || Objects.equals(type, "output")) {
                    values.put("time", (int) algorithm.getOldValues()[0]);
                    values.put("precision", algorithm.getOldValues()[1]);
                    values.put("cost", (int) algorithm.getOldValues()[2]);
                }
                res.put("values", values);
            }
            String mapCode;
//            if (Objects.equals(type, "output")) {
//                String input = example.substring(0, example.indexOf("_"));
//                ProcessMap oldMap = algorithmService.pathToMap(inputPath + input + ".yaml");
//                Map<String, Object> ans = compareMapPaths(oldMap.getProcessPaths(), map.getProcessPaths());
//                mapCode = WriteMapCode(map, ans);
//            } else mapCode = algorithmService.getMapCode(map);
            mapCode = algorithmService.getMapCode(map);
//            System.out.println(mapCode);
            res.put("code", 200);
            res.put("msg", "获取成功");
            res.put("mapCode", mapCode);

            if (flag_output) {
//                String mapCode1 = algorithmService.getMapCode(map);
                String input = example.substring(0, example.indexOf("_"));
                ProcessMap oldMap = algorithmService.pathToMap(inputPath + input + ".yaml");
                Map<String, Object> ans = compareMapPaths(oldMap.getProcessPaths(), map.getProcessPaths());
                String mapCode1 = WriteMapCode(map, ans);
                res.put("mapCode1", mapCode1);

                Pattern pattern = Pattern.compile(".*_(\\d+)_(\\d+)_(\\d+)_(\\d+)_(\\d+)");
                Matcher matcher = pattern.matcher(example);
                if (matcher.matches()) {
                    int time = Integer.parseInt(matcher.group(1));
                    int precision = Integer.parseInt(matcher.group(2));
                    int cost = Integer.parseInt(matcher.group(3));
                    int fileType = Integer.parseInt(matcher.group(4));
                    int number = Integer.parseInt(matcher.group(5));
                    Map<String, Object> configuration = new HashMap<>();
                    configuration.put("time", time);
                    configuration.put("precision", precision);
                    configuration.put("cost", cost);
                    configuration.put("algorithmType1", fileType);//
                    configuration.put("algorithmType2", number);
                    res.put("data", configuration);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.put("code", 500);
            res.put("msg", "获取失败");
        }
        return res;
    }
    @GetMapping("/getValues")
    public Map<String, Object> GetValues(@RequestParam Map<String, Object> re_map){
        String example = (String) re_map.get("example");

        String input = inputPath + example.substring(0, example.indexOf("_")) + ".yaml";
        String output = outputPath + example + ".yaml";
        Map<String, Object> res = new HashMap<>();

        ProcessMap map = algorithmService.pathToMap(input);
        Algorithm1 algorithm = new Algorithm1();
        algorithm.initAlgorithm(map);
        int oldTime = (int) algorithm.getOldValues()[0];
        double oldPrecision = algorithm.getOldValues()[1];
        int oldCost = (int) algorithm.getOldValues()[2];
        ProcessMap map1 = algorithmService.pathToMap(output);
        algorithm.initAlgorithm(map1);
        int newTime = (int) algorithm.getOldValues()[0];
        double newPrecision = algorithm.getOldValues()[1];
        int newCost = (int) algorithm.getOldValues()[2];
        Pattern pattern = Pattern.compile(".*_(\\d+)_(\\d+)_(\\d+)_(\\d+)_(\\d+)");
        Matcher matcher = pattern.matcher(example);
        if (matcher.matches()) {
            int timeFactor = Integer.parseInt(matcher.group(1));
            int precisionFactor = Integer.parseInt(matcher.group(2));
            int costFactor = Integer.parseInt(matcher.group(3));
            double factor1 = (double) timeFactor / (timeFactor + precisionFactor + costFactor);
            double factor2 = (double) precisionFactor / (timeFactor + precisionFactor + costFactor);
            double factor3 = (double) costFactor / (timeFactor + precisionFactor + costFactor);
            double temp = (double) (oldTime - newTime) / oldTime * factor1 + (newPrecision - oldPrecision) * factor2 + (double) (oldCost - newCost) / oldCost * factor3;
            res.put("ratio", temp);
            res.put("code", 200);
            res.put("msg", "获取成功");
        } else {
            res.put("code", 500);
            res.put("msg", "获取失败");
        }
        return res;
    }

    // 获取实例目录
    @GetMapping("/getExampleDirectory")
    public Map<String, Object> getExampleDirectory() {
        Map<String, Object> res = new HashMap<>();
        ArrayList<Map<String, Object>> exampleFiles = new ArrayList<>();

        File historyDir = new File(historyPath);
        if (!historyDir.exists()) {
            historyDir.mkdirs();
        }
        File inputDir = new File(inputPath);
        if (!inputDir.exists()) {
            inputDir.mkdirs();
        }
        File outputDir = new File(outputPath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        File[] inputList = new File(inputPath).listFiles();
        File[] outputList = new File(outputPath).listFiles();

        if (inputList != null) {
            for (File file : inputList) {
                Map<String, Object> temp = new HashMap<>();
                if (file.isFile() && file.getName().endsWith(".yaml")) {
                    String name = file.getName().substring(0, file.getName().length() - 5);

                    ArrayList<String> outputFiles = new ArrayList<>();
                    if (outputList != null) {
                        for (File output : outputList) {
                            if (output.isFile() && output.getName().endsWith(".yaml") && output.getName().startsWith(name + "_")) {
                                outputFiles.add(output.getName().substring(0, output.getName().length() - 5));
                            }
                        }
                    }
                    temp.put("exampleName", name);
                    temp.put("inputFile", name);
                    temp.put("outputFiles", outputFiles);
                    exampleFiles.add(temp);
                }
            }
        }
        res.put("examples", exampleFiles);
        res.put("msg", "获取成功");
        res.put("code", 200);
        return res;
    }

    public static String algorithmChangeIntoNum(String algorithm) {
        String res = "";
        String[] numbers = new String[]{
                "0_0", "0_1", "0_2",
                "1_0", "1_1", "1_2",
                "2_0", "2_1", "2_2"};
        switch (algorithm) {
            case "潜力导向-顺序优化":
                res = numbers[0];
                break;
            case "潜力导向-逆序优化":
                res = numbers[1];
                break;
            case "潜力导向-随机优化":
                res = numbers[2];
                break;
            case "约束强制满足-全域探索":
                res = numbers[3];
                break;
            case "约束强制满足-固定区域探索":
                res = numbers[4];
                break;
            case "约束强制满足-适配区域探索":
                res = numbers[5];
                break;
            case "层次化禁忌搜索-确定型搜索":
                res = numbers[6];
                break;
            case "层次化禁忌搜索-双模式搜索":
                res = numbers[7];
                break;
            case "层次化禁忌搜索-随机搜索":
                res = numbers[8];
                break;
            default:
                res = "wrong";
        }

        return res;
    }
    public String numChangeIntoAlgorithm(String num) {
        String res;
        String[] filenames = new String[]{
                "潜力导向-顺序优化", "潜力导向-逆序优化", "潜力导向-随机优化",
                "约束强制满足-全域探索", "约束强制满足-固定区域探索", "约束强制满足-适配区域探索",
                "层次化禁忌搜索-确定型搜索", "层次化禁忌搜索-双模式搜索", "层次化禁忌搜索-随机搜索"};
        switch (num) {
            case "0_0":
                res = filenames[0];
                break;
            case "0_1":
                res = filenames[1];
                break;
            case "0_2":
                res = filenames[2];
                break;
            case "1_0":
                res = filenames[3];
                break;
            case "1_1":
                res = filenames[4];
                break;
            case "1_2":
                res = filenames[5];
                break;
            case "2_0":
                res = filenames[6];
                break;
            case "2_1":
                res = filenames[7];
                break;
            case "2_2":
                res = filenames[8];
                break;
            default:
                res = "wrong";
                break;
        }
        return res;
    }


}
