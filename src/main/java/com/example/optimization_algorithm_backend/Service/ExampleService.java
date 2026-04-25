package com.example.optimization_algorithm_backend.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

public interface ExampleService {
    Map<String,Object> processZipFile(String zipFilePath, String destDir) throws IOException;

    Map<String, Object> multiProcessZipFile(String zipFilePath, String destDir) throws IOException;

    void packageZipFile(List<String> filePaths, List<String> zipEntries, OutputStream outputStream) throws IOException;

    String parseConfigurationFromFileName(String fileName) throws IOException;
    String createTemporaryConfigFile(String configContent) throws IOException;
}
