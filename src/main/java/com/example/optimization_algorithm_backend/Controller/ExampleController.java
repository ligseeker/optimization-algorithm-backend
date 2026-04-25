package com.example.optimization_algorithm_backend.Controller;


import com.example.optimization_algorithm_backend.Service.ExampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@CrossOrigin(origins = "*")
public class ExampleController {

    @Value("${upload_dir.temp}")
    String tempDir;

    @Value("${path.historyPath}")
    String historyPath;

    @Autowired
    private ExampleService exampleService;

    //上传实例(ZIP压缩包)
    @PostMapping("/uploadExample")
    public Map<String, Object> uploadExample(@RequestParam("file") MultipartFile file) {
        Map<String, Object> res = new HashMap<>();
        if (file.isEmpty()) {
            res.put("code", 500);
            res.put("msg", "文件上传失败，文件为空");
        }
        try {
            File dir = new File(tempDir);
            if (!dir.exists()) dir.mkdirs();
            // 保存上传的ZIP文件
            Path tempZipPath = Files.createTempFile(Paths.get(tempDir), "uploaded-", ".zip");
            Files.write(tempZipPath, file.getBytes());

            // 解压ZIP文件
            res = exampleService.multiProcessZipFile(tempZipPath.toString(), tempDir);

            // 删除上传的ZIP文件
            Files.delete(tempZipPath);

        } catch (IOException e) {
            e.printStackTrace();
            res.put("code", 500);
            res.put("msg", "文件上传失败");
        }
        return res;
    }

    @PostMapping("/exportExample")
    public void downloadZipFile(@RequestParam String inputFileName,
                                @RequestParam List<String> outputFileName,
                                HttpServletResponse response) throws IOException {
        // 设置响应头，告知浏览器返回的是 ZIP 文件
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\"example.zip\"");

        String path = tempDir + "output/";

        // 构建 ZIP 文件结构
        List<String> filePaths = new ArrayList<>();
        List<String> zipEntries = new ArrayList<>();

        // 文件路径列表，指定要打包的文件
        filePaths.add(tempDir + "input/" + inputFileName + ".yaml");
        zipEntries.add("input/" + inputFileName + ".yaml");

        // 解析 outputFileName，生成对应的 output 文件夹和 configuration.yaml
        for (int i = 0; i < outputFileName.size(); i++) {
            String outputFile = outputFileName.get(i);
            System.out.println(outputFile);
            String configurationContent = exampleService.parseConfigurationFromFileName(outputFile);

            // 创建 ZIP 中的 output 文件夹
            String outputFolder = "output" + (i + 1);
            zipEntries.add(outputFolder + "/" + outputFile + ".yaml");
            zipEntries.add(outputFolder + "/configuration.yaml");

            // 添加文件路径
            filePaths.add(path + outputFile + ".yaml");  // 假设 output.yaml 的路径
            filePaths.add(exampleService.createTemporaryConfigFile(configurationContent));  // 创建临时的 configuration.yaml 文件
        }

        // 调用打包方法
        exampleService.packageZipFile(filePaths, zipEntries, response.getOutputStream());
    }

    // 删除一个或多个实例
    @PostMapping("/deleteOutputExample")
    public Map<String, Object> deleteExample(@RequestParam List<String> fileNames) {
        Map<String, Object> res = new HashMap<>();
        for (String fileName : fileNames) {
            try {
                File file = new File(tempDir + "output/" + fileName + ".yaml");
                if (file.exists()) {
                    file.delete();
                } else {
                    res.put("code", 500);
                    res.put("msg", fileName + "文件不存在");
                    return res;
                }
            } catch (Exception e) {
                res.put("code", 500);
                res.put("msg", fileName + "文件删除失败");
                return res;
            }
        }
        res.put("code", 200);
        res.put("msg", "删除成功");
        return res;
    }

    // 删除input里的某张图，对应的output里文件名包含这张图名字的输出图也要删除
    @PostMapping("/deleteInputFile")
    public Map<String, Object> deleteInputExample(@RequestParam String fileName) {
        Map<String, Object> res = new HashMap<>();
        try {
            File file = new File(tempDir + "input/" + fileName + ".yaml");
            if (file.exists()) {
                file.delete();
            } else {
                res.put("code", 500);
                res.put("msg", fileName + "文件不存在");
                return res;
            }
            File outputDir = new File(tempDir + "output/");
            File[] files = outputDir.listFiles();
            for (File f : files) {
                String fileName1 = f.getName();
                String regex = "^(.*)_(\\d+_\\d+_\\d+_\\d+_\\d+)\\.yaml$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(fileName1);
                if (matcher.find()) {
                    String prefix = matcher.group(1);
                    if (prefix.equals(fileName)) {
                        f.delete();
                    }
                }
            }
        } catch (Exception e) {
            res.put("code", 500);
            res.put("msg", fileName + "文件删除失败");
            return res;
        }
        res.put("code", 200);
        res.put("msg", "删除成功");
        return res;
    }

    // 对input里的某张图进行重命名，相应的output里包含原来名字的文件，也需要更改
    @PostMapping("/renameInputFile")
    public Map<String, Object> renameInputExample(@RequestParam String oldFileName, @RequestParam String newFileName) {
        Map<String, Object> res = new HashMap<>();
        try {
            File old_file = new File(tempDir + "input/" + oldFileName + ".yaml");
            if (oldFileName.equals(newFileName)) {
                res.put("code", 500);
                res.put("msg", "新文件名不能与原文件名相同");
                return res;
            }
            if (old_file.exists()) {
                File new_file = new File(tempDir + "input/" + newFileName + ".yaml");
                if (new_file.exists()) {
                    res.put("code", 500);
                    res.put("msg", newFileName + "文件已存在");
                    return res;
                } else {
                    old_file.renameTo(new File(tempDir + "input/" + newFileName + ".yaml"));
                }
            } else {
                res.put("code", 500);
                res.put("msg", oldFileName + "文件不存在");
                return res;
            }
            File outputDir = new File(tempDir + "output/");
            File[] files = outputDir.listFiles();
            for (File f : files) {
                String fileName = f.getName();
                String regex = "^(.*)_(\\d+_\\d+_\\d+_\\d+_\\d+)\\.yaml$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(fileName);
                if (matcher.find()) {
                    String prefix = matcher.group(1);
                    String suffix = matcher.group(2);
                    if (prefix.equals(oldFileName)) {
                        f.renameTo(new File(tempDir + "output/" + newFileName + "_" + suffix + ".yaml"));
                    }
                }
//                if (f.getName().contains(oldFileName)) {
//                    f.renameTo(new File(tempDir + "output/" + f.getName().replace(oldFileName, newFileName)));
//                }
            }
        } catch (Exception e) {
            res.put("code", 500);
            res.put("msg", oldFileName + "文件重命名失败");
            return res;
        }
        res.put("code", 200);
        res.put("msg", "重命名成功");
        return res;
    }

    // 对history里的某张图进行重命名
    @PostMapping("/renameHistoryFile")
    public Map<String, Object> renameExampleFile(@RequestParam String oldFileName, @RequestParam String newFileName) {
        Map<String, Object> res = new HashMap<>();
        try {
            if (oldFileName.equals(newFileName)) {
                res.put("code", 500);
                res.put("msg", "新文件名不能与原文件名相同");
                return res;
            }
            File old_file = new File(historyPath + oldFileName + ".yaml");
            if (old_file.exists()) {
                File new_file = new File(historyPath + newFileName + ".yaml");
                if (new_file.exists()) {
                    res.put("code", 500);
                    res.put("msg", newFileName + "文件已存在");
                    return res;
                } else {
                    old_file.renameTo(new File(historyPath + newFileName + ".yaml"));
                }
            } else {
                res.put("code", 500);
                res.put("msg", oldFileName + "文件不存在");
                return res;
            }
        } catch (Exception e) {
            res.put("code", 500);
            res.put("msg", oldFileName + "文件重命名失败");
            return res;
        }
        res.put("code", 200);
        res.put("msg", "重命名成功");
        return res;
    }

    // 删除history里的某张图
    @PostMapping("/deleteHistoryFile")
    public Map<String, Object> deleteExampleFile(@RequestParam String fileName) {
        Map<String, Object> res = new HashMap<>();
        try {
            File file = new File(historyPath + fileName + ".yaml");
            if (file.exists()) {
                file.delete();
            } else {
                res.put("code", 500);
                res.put("msg", fileName + "文件不存在");
                return res;
            }
        } catch (Exception e) {
            res.put("code", 500);
            res.put("msg", fileName + "文件删除失败");
            return res;
        }
        res.put("code", 200);
        res.put("msg", "删除成功");
        return res;
    }

}
