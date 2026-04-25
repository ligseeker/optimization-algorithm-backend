package com.example.optimization_algorithm_backend.Service.Impl;

import com.example.optimization_algorithm_backend.Service.ExampleService;
import com.example.optimization_algorithm_backend.algorithm.model.utils;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
public class ExampleServiceImpl implements ExampleService {

    @Override
    public Map<String, Object> processZipFile(String zipFilePath, String destDir) throws IOException {
        Map<String, Object> res = new HashMap<>();

        int time, precision, cost, type, number;
        String inputFileName = null;
        String temp_name;


        // 先解压并处理 input/ 下的文件
        try (ZipInputStream zipIn = new ZipInputStream(Files.newInputStream(Paths.get(zipFilePath)))) {
            ZipEntry entry = zipIn.getNextEntry();

            while (entry != null) {
                String fileName = entry.getName();
                String outputFilePath;

                if (fileName.startsWith("input/") && fileName.endsWith(".yaml")) {
                    String originalInputFileName = new File(fileName).getName();
                    inputFileName = originalInputFileName.replace(".yaml", "") + "-" + getRandomString(4) + ".yaml";
                    outputFilePath = destDir + "input/" + inputFileName;
                    saveFile(zipIn, outputFilePath);
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }

        // 再解压并处理 configuration.yaml 文件
        try (ZipInputStream zipIn = new ZipInputStream(Files.newInputStream(Paths.get(zipFilePath)))) {
            ZipEntry entry = zipIn.getNextEntry();

            while (entry != null) {
                String fileName = entry.getName();
                String outputFilePath;

                if (fileName.equals("configuration.yaml")) {
                    outputFilePath = destDir + fileName;
                    saveFile(zipIn, outputFilePath);
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
            // 读取 configuration.yaml 文件
            String configurationFilePath = destDir + "configuration.yaml";
            Map<String, Integer> configuration = checkConfiguration(configurationFilePath);
            if (configuration.get("code") == 0) {
                res.put("code", 500);
                res.put("msg", "configuration.yaml 文件不合法");
                return res;
            } else {
                time = configuration.get("time");
                precision = configuration.get("precision");
                cost = configuration.get("cost");
                type = configuration.get("type");
                number = configuration.get("number");
                // 存储配置信息
                temp_name = "_" + time + "_" + precision + "_" + cost + "_" + type + "_" + number;
            }
        }
        Files.deleteIfExists(Paths.get(destDir + "configuration.yaml"));

        // 最后解压并处理 output/ 下的文件
        try (ZipInputStream zipIn = new ZipInputStream(Files.newInputStream(Paths.get(zipFilePath)))) {
            ZipEntry entry = zipIn.getNextEntry();

            while (entry != null) {
                String fileName = entry.getName();
                String outputFilePath;

                if (fileName.startsWith("output/") && fileName.endsWith(".yaml") && inputFileName != null) {
                    String outputDir = destDir + "output/" + inputFileName.replace(".yaml", "") + "/";
                    Files.createDirectories(Paths.get(outputDir));
                    outputFilePath = outputDir + inputFileName.replace(".yaml", "") + temp_name + ".yaml";
                    saveFile(zipIn, outputFilePath);
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
        res.put("code", 200);
        res.put("msg", "文件上传成功");
        return res;
    }


    @Override
    public Map<String, Object> multiProcessZipFile(String zipFilePath, String destDir) throws IOException {
        Map<String, Object> res = new HashMap<>();

        List<String> outputFolders = new ArrayList<>();
        int time, precision, cost, type, number;
        String inputFileName = null;
        String temp_name;
        String outputDir = null;


        // 先解压并处理 input/ 下的文件
        try (ZipInputStream zipIn = new ZipInputStream(Files.newInputStream(Paths.get(zipFilePath)))) {
            ZipEntry entry = zipIn.getNextEntry();

            while (entry != null) {
                String fileName = entry.getName();
                String outputFilePath;

                if (fileName.startsWith("input/") && fileName.endsWith(".yaml")) {
                    String originalInputFileName = new File(fileName).getName();
                    inputFileName = originalInputFileName.replace(".yaml", "") + "-" + getRandomString(4) + ".yaml";
                    outputFilePath = destDir + "input/" + inputFileName;
                    saveFile(zipIn, outputFilePath);
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
        if (inputFileName != null) {
//            outputDir = destDir + "output/" + inputFileName.replace(".yaml", "") + "/";
            outputDir = destDir + "output/" ;
        }
        // 第一次遍历：获取所有的 outputX 文件夹的名称
        try (ZipInputStream zipIn = new ZipInputStream(Files.newInputStream(Paths.get(zipFilePath)))) {
            ZipEntry entry = zipIn.getNextEntry();

            while (entry != null) {
                String entryName = entry.getName();

                // 检查条目是否属于output文件夹
                if (entryName.startsWith("output")) {
                    // 获取 outputX 文件夹名称
                    String folderName = entryName.split("/")[0];
                    if (!outputFolders.contains(folderName)) {
                        outputFolders.add(folderName);
                    }
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
        System.out.println("finish exact folder");

        for (String folder : outputFolders) {
            System.out.println(folder);
            // 先解压并处理 configuration.yaml 文件
            try (ZipInputStream zipIn = new ZipInputStream(Files.newInputStream(Paths.get(zipFilePath)))) {
                ZipEntry entry = zipIn.getNextEntry();

                while (entry != null) {
                    String fileName = entry.getName();
                    String outputFilePath;

                    if (fileName.startsWith(folder) && fileName.endsWith("configuration.yaml")) {
                        outputFilePath = destDir + "configuration.yaml";
                        saveFile(zipIn, outputFilePath);
                    }
                    zipIn.closeEntry();
                    entry = zipIn.getNextEntry();
                }
                // 读取 configuration.yaml 文件
                String configurationFilePath = destDir + "configuration.yaml";
                Map<String, Integer> configuration = checkConfiguration(configurationFilePath);
                if (configuration.get("code") == 0) {
                    res.put("code", 500);
                    res.put("msg", "configuration.yaml 文件不合法");
                    return res;
                } else {
                    time = configuration.get("time");
                    precision = configuration.get("precision");
                    cost = configuration.get("cost");
                    type = configuration.get("type");
                    number = configuration.get("number");
                    // 存储配置信息
                    temp_name = "_" + time + "_" + precision + "_" + cost + "_" + type + "_" + number;
                }
            }
            Files.deleteIfExists(Paths.get(destDir + "configuration.yaml"));

            // 最后解压并处理 output/ 下的文件
            try (ZipInputStream zipIn = new ZipInputStream(Files.newInputStream(Paths.get(zipFilePath)))) {
                ZipEntry entry = zipIn.getNextEntry();

                while (entry != null) {
                    String fileName = entry.getName();
                    String outputFilePath;

                    if (fileName.startsWith(folder) && fileName.endsWith(".yaml") && !fileName.endsWith("configuration.yaml") && inputFileName != null) {
//                        if(Files.notExists(Paths.get(outputDir))){
//                            Files.createDirectories(Paths.get(outputDir));
//                        }
                        Files.createDirectories(Paths.get(outputDir));
                        outputFilePath = outputDir + inputFileName.replace(".yaml", "") + temp_name + ".yaml";
                        saveFile(zipIn, outputFilePath);
                    }
                    zipIn.closeEntry();
                    entry = zipIn.getNextEntry();
                }
            }
        }
        res.put("code", 200);
        res.put("msg", "文件上传成功");
        return res;
    }



    @Override
    public void packageZipFile(List<String> filePaths, List<String> zipEntries, OutputStream outputStream) throws IOException {
        if (filePaths.size() != zipEntries.size()) {
            throw new IllegalArgumentException("File paths and zip entries must be the same size");
        }

        try (ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {
            for (int i = 0; i < filePaths.size(); i++) {
                File fileToZip = new File(filePaths.get(i));
                if (fileToZip.exists() && fileToZip.isFile()) {
                    try (FileInputStream fis = new FileInputStream(fileToZip)) {
                        ZipEntry zipEntry = new ZipEntry(zipEntries.get(i));
                        zipOut.putNextEntry(zipEntry);

                        byte[] bytes = new byte[1024];
                        int length;
                        while ((length = fis.read(bytes)) >= 0) {
                            zipOut.write(bytes, 0, length);
                        }
                    }
                } else {
                    throw new FileNotFoundException("File not found: " + fileToZip.getAbsolutePath());
                }
            }
        }
    }

    @Override
    public String parseConfigurationFromFileName(String fileName) {
        // 使用正则表达式从文件名中提取参数
        Pattern pattern = Pattern.compile(".*_(\\d+)_(\\d+)_(\\d+)_(\\d+)_(\\d+)");

        Matcher matcher = pattern.matcher(fileName);

        if (matcher.matches()) {
            String time = matcher.group(1);
            String precision = matcher.group(2);
            String cost = matcher.group(3);
            String type = matcher.group(4);
            String number = matcher.group(5);

            // 生成 configuration.yaml 的内容
            return "params:\n" +
                    "  time: " + time + "\n" +
                    "  precision: " + precision + "\n" +
                    "  cost: " + cost + "\n" +
                    "algorithm:\n" +
                    "  type: " + type + "\n" +
                    "  number: " + number + "\n";
        }
        throw new IllegalArgumentException("Invalid output file name format");
    }

    @Override
    public String createTemporaryConfigFile(String configContent) throws IOException {
        // 生成临时的 configuration.yaml 文件，并返回其路径
        File tempConfigFile = File.createTempFile("configuration", ".yaml");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempConfigFile))) {
            writer.write(configContent);
        }
        return tempConfigFile.getAbsolutePath();
    }

    public void saveFile(ZipInputStream zipIn, String filePath) throws IOException {
        // 如果存在则覆盖，不存在则创建
        Path file = Paths.get(filePath);
        if (Files.exists(file)) {
            Files.delete(file);
        }
        Files.createDirectories(file.getParent());
        Files.copy(zipIn, file);
    }
//    @Override
//    public void saveFile(ZipInputStream zipIn, String filePath) throws IOException {
//        Path file = Paths.get(filePath);
//
//        // 创建父目录
//        Files.createDirectories(file.getParent());
//
//        // 使用 REPLACE_EXISTING 选项，直接替换已存在的文件
//        Files.copy(zipIn, file, StandardCopyOption.REPLACE_EXISTING);
//    }

    public String getRandomString(int n) {
        return utils.getRandomString(n);
    }

    //检查configuraion.yaml文件是否合法
    public Map<String, Integer> checkConfiguration(String configurationFilePath) {
        Map<String, Integer> res = new HashMap<>();
        Map<String, Object> configuration = readData(configurationFilePath);
        if (configuration == null) {
            res.put("code", 0);
            return res;
        } else {
            Map<String, Object> params = (Map<String, Object>) configuration.get("params");
            Map<String, Object> algorithm = (Map<String, Object>) configuration.get("algorithm");
            if (params == null || algorithm == null) {
                res.put("code", 0);
                return res;
            }
            //检查参数是否合法，是否缺失，是否为正整数
            //检查算法的type和number是否都为1 2 3 之中的数字
            if (params.get("time") == null || params.get("precision") == null || params.get("cost") == null) {
                res.put("code", 0);
                return res;
            }
            if (algorithm.get("type") == null || algorithm.get("number") == null) {
                res.put("code", 0);
                return res;
            }
            int time = (int) params.get("time");
            int precision = (int) params.get("precision");
            int cost = (int) params.get("cost");
            int type = (int) algorithm.get("type");
            int number = (int) algorithm.get("number");
            if (time <= 0 || precision <= 0 || cost <= 0 || type < 0 || type >= 3 || number < 0 || number >= 3) {
                res.put("code", 0);
                return res;
            }
            res.put("code", 1);
            res.put("time", time);
            res.put("precision", precision);
            res.put("cost", cost);
            res.put("type", type);
            res.put("number", number);
        }
        return res;
    }

    public Map<String, Object> readData(String path) {
        Map<String, Object> res = new HashMap<>();
        try {
            InputStream input = new FileInputStream(path);
            Yaml yaml = new Yaml();
            res = yaml.load(input);
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }
}
