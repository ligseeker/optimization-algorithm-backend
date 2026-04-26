package com.example.optimization_algorithm_backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI platformOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Optimization Algorithm Backend API")
                .description("统一返回结构 Result<T>，错误码示例：400001参数错误，401001未登录，403001无权限，404001资源不存在，409001冲突，500001系统异常，600001任务执行失败，700001文件解析失败")
                .version("v1"));
    }
}
