package com.example.optimization_algorithm_backend.Controller;


import com.example.optimization_algorithm_backend.Service.AlgorithmService;
import com.example.optimization_algorithm_backend.Service.EquipmentService;
import com.example.optimization_algorithm_backend.algorithm.model.MultiNode;
import com.example.optimization_algorithm_backend.algorithm.model.ProcessMap;
import com.example.optimization_algorithm_backend.algorithm.model.utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class EquipmentController {
    @Autowired
    private EquipmentService equipmentService;

    @Value("${path.historyPath}")
    String historyPath;
    @Value("${path.inputPath}")
    String inputPath;
    @Value("${path.outputPath}")
    String outputPath;
    @Value("${path.equipmentPath}")
    String equipmentPath;
    @Value("${path.tempPath}")
    String tempPath;
    @Autowired
    private AlgorithmService algorithmService;

    //新建装备
    @PostMapping("/addEquipment")
    public Map<String, Object> addEquipment(@RequestParam Map<String, Object> res_map) {
        Map<String, Object> res = new HashMap<>();

        //获取装备信息
        String example = (String) res_map.get("example");
        String equipmentName = (String) res_map.get("name");
        String color = (String) res_map.get("color");
        String description = (String) res_map.get("description");

        String srcPath = historyPath + example + ".yaml";
        File file = new File(srcPath);
        if(!file.exists()){
            res.put("code", 500);
            res.put("msg", "实例不存在");
            return res;
        }

        if (equipmentService.findEquipment(equipmentName, example)) {
            res.put("code", 500);
            res.put("msg", "装备已存在");
        } else {
            equipmentService.addEquipment(equipmentName, color, example,description);
            res.put("code", 200);
            res.put("msg", "添加成功");
        }
        return res;
    }


    //删除装备
    @PostMapping("/deleteEquipment")
    public Map<String, Object> deleteEquipment(@RequestParam Map<String, Object> res_map) {
        Map<String, Object> res = new HashMap<>();

        //获取装备信息
        String example = (String) res_map.get("example");
        String equipmentName = (String) res_map.get("name");

        String srcPath = historyPath + example + ".yaml";
        File file = new File(srcPath);
        if(!file.exists()){
            res.put("code", 500);
            res.put("msg", "实例不存在");
            return res;
        }

        if (!equipmentService.findEquipment(equipmentName, example)) {
            res.put("code", 500);
            res.put("msg", "装备不存在");
        } else {
            equipmentService.deleteEquipment(equipmentName, example);
//            ProcessMap map = algorithmService.pathToMap(srcPath);
//            for(MultiNode node: map.getMultiNodes()){
//                if(node.getEquipmentName().equals(equipmentName)){
//                    node.setEquipmentName("");
//                    break;
//                }
//            }
            res.put("code", 200);
            res.put("msg", "删除成功");
        }
        return res;
    }
    // 上传装备图片
    @PostMapping("/uploadEquipmentImage")
    public Map<String, Object> uploadEquipmentImage(@RequestParam("file") MultipartFile file,
                                                       @RequestParam("equipmentName") String equipmentName,
                                                       @RequestParam("example") String example) {
        Map<String, Object> res = new HashMap<>();
        if (file.isEmpty()) {
            res.put("code", 500);
            res.put("msg", "上传文件为空");
        }
        if(equipmentService.findEquipment(equipmentName, example)){
            res.put("code", 500);
            res.put("msg", "装备不存在");
        }

        // 获取文件的 MIME 类型
        String contentType = file.getContentType();
        // 允许的图片类型
        List<String> allowedTypes = Arrays.asList("image/jpeg", "image/png", "image/gif");

        // 检测文件类型是否符合要求
        if (!allowedTypes.contains(contentType)) {
            res.put("code", 500);
            res.put("msg", "文件类型不支持，只允许上传 jpg, png, gif 格式的图片");
            return res;
        }
        try {
            // 构建新的文件名，这里使用装备名和原始文件名的一部分（防止重名）
            String imageName = getRandomString(10) + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'));
            // 创建文件路径
            Path path = Paths.get(equipmentPath + imageName).toAbsolutePath().normalize();
            // 确保目录存在
            Files.createDirectories(path.getParent());
            // 保存文件
            Files.copy(file.getInputStream(), path);

            // 更新装备信息
            equipmentService.uploadEquipmentImage(equipmentName, example, imageName);

            res.put("code", 200);
            res.put("msg", "上传成功");
            res.put("data", imageName);
        } catch (IOException e) {
            e.printStackTrace();
            res.put("code", 500);
            res.put("msg", "上传失败");
        }
        return res;
    }

    //修改装备颜色
    @PostMapping("/modifyEquipmentColor")
    public Map<String, Object> modifyEquipmentColor(@RequestParam Map<String, Object> res_map) {
        Map<String, Object> res = new HashMap<>();

        //获取装备信息
        String example = (String) res_map.get("example");
        String equipmentName = (String) res_map.get("name");
        String color = (String) res_map.get("color");

        String srcPath = historyPath + example + ".yaml";
        File file = new File(srcPath);
        if(!file.exists()){
            res.put("code", 500);
            res.put("msg", "实例不存在");
            return res;
        }

        if (!equipmentService.findEquipment(equipmentName, example)) {
            res.put("code", 500);
            res.put("msg", "装备不存在");
        } else {
            equipmentService.modifyEquipmentColor(equipmentName, color, example);
            res.put("code", 200);
            res.put("msg", "修改成功");
        }
        return res;
    }
    // 修改装备描述
    @PostMapping("/modifyEquipmentDescription")
    public Map<String, Object> modifyEquipmentDescription(@RequestParam Map<String, Object> res_map) {
        Map<String, Object> res = new HashMap<>();

        //获取装备信息
        String example = (String) res_map.get("example");
        String equipmentName = (String) res_map.get("name");
        String description = (String) res_map.get("description");

        String srcPath = historyPath + example + ".yaml";
        File file = new File(srcPath);
        if(!file.exists()){
            res.put("code", 500);
            res.put("msg", "实例不存在");
            return res;
        }

        if (!equipmentService.findEquipment(equipmentName, example)) {
            res.put("code", 500);
            res.put("msg", "装备不存在");
        } else {
            equipmentService.modifyEquipmentDescription(equipmentName, description, example);
            res.put("code", 200);
            res.put("msg", "修改成功");
        }
        return res;
    }
    //重命名装备
    @PostMapping("/renameEquipment")
    public Map<String, Object> renameEquipment(@RequestParam Map<String, Object> res_map) {
        Map<String, Object> res = new HashMap<>();

        //获取装备信息
        String example = (String) res_map.get("example");
        String equipmentName = (String) res_map.get("name");
        String newName = (String) res_map.get("newName");

        String srcPath = historyPath + example + ".yaml";
        File file = new File(srcPath);
        if(!file.exists()){
            res.put("code", 500);
            res.put("msg", "实例不存在");
            return res;
        }

        if (!equipmentService.findEquipment(equipmentName, example)) {
            res.put("code", 500);
            res.put("msg", "装备不存在");
        } else if (equipmentService.findEquipment(newName, example)) {
            res.put("code", 500);
            res.put("msg", "新装备名已存在");
        } else {
            equipmentService.renameEquipment(equipmentName, newName, example);
            res.put("code", 200);
            res.put("msg", "修改成功");
        }
        return res;
    }

    //获取所有装备信息
    @GetMapping("/getEquipment")
    public Map<String, Object> getEquipment(@RequestParam Map<String, Object> res_map) {
        Map<String, Object> res = new HashMap<>();

        //获取装备信息
        String type = (String) res_map.get("type");
        String example = (String) res_map.get("example");

        String path = "";
        boolean flag_temp = false;
        String algorithmType = "";
        if (Objects.equals(type, "history")) {
            path = historyPath;
        } else if (Objects.equals(type, "input")) {
            path = inputPath;
        } else if (Objects.equals(type, "output")) {
            path = outputPath;
        }else if(Objects.equals(type,"temp")){
            path = tempPath;
            flag_temp = true;
            algorithmType = (String) res_map.get("algorithm");
        }
        if(flag_temp){
            String tempFilename1 = DirectoryController.algorithmChangeIntoNum(algorithmType);
            if(tempFilename1.equals("wrong")){
                res.put("code", 500);
                res.put("msg", "中间流程图获取失败");
            }
            example =  tempFilename1+ "_" + example;
        }
        String srcPath = path + example + ".yaml";
        File file = new File(srcPath);
        if(!file.exists()){
            res.put("code", 500);
            res.put("msg", "实例不存在");
            return res;
        }

        Map<String, Object> data = equipmentService.getAllEquipments(srcPath);

        res.put("code", 200);
        res.put("msg", "获取成功");
        res.put("data", data.get("Equipments"));

        return res;
    }

    public String getRandomString(int n) {
        return utils.getRandomString(n);
    }

    @GetMapping("/images/{fileName:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        try {
            // 构造文件路径（这里假设IMAGE_DIRECTORY是容器内的绝对路径，或者通过环境变量等方式获取）
            File file = new File(equipmentPath + fileName);

            // 检查文件是否存在
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            // 读取文件为Resource对象
            Resource resource = new UrlResource(file.toURI().toURL());

            // 返回图片资源
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // 根据实际情况设置正确的MediaType
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                    .body(resource);
        } catch (IOException e) {
            // 处理异常情况，例如读取文件失败
            return ResponseEntity.internalServerError().build();
        }
    }

}
