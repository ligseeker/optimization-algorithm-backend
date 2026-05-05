package com.example.optimization_algorithm_backend.module.auth.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.optimization_algorithm_backend.common.response.Result;
import com.example.optimization_algorithm_backend.module.auth.dto.LoginRequest;
import com.example.optimization_algorithm_backend.module.auth.service.AuthService;
import com.example.optimization_algorithm_backend.module.auth.vo.CurrentUserVO;
import com.example.optimization_algorithm_backend.module.auth.vo.LoginResponseVO;
import com.example.optimization_algorithm_backend.module.log.annotation.OperationLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Validated// 作用：启用Spring的验证机制，允许在控制器方法中使用@Valid注解对请求参数进行验证。当请求参数不符合定义的约束条件时，Spring会自动抛出异常并返回相应的错误响应，提升接口的健壮性和安全性。
@RestController // 作用：标识这是一个RESTful风格的控制器，Spring会自动将方法的返回值转换为JSON格式并写入HTTP响应体中，简化了开发过程。
@RequestMapping("/api/auth") // 作用：定义这个控制器处理以/api/auth开头的HTTP请求，所有与认证相关的接口都应该放在这个控制器中，保持代码的组织性和清晰性。
@Tag(name = "Auth", description = "登录认证接口") // 作用：为Swagger文档生成提供接口分类和描述信息，name表示分类名称，这里是Auth，description提供了这个分类的简要描述，这些信息会在生成的API文档中显示，帮助前端开发者理解接口的用途和分类。
public class AuthController {
    // 作用：定义一个RESTful风格的控制器，处理与认证相关的HTTP请求。@RequestMapping("/api/auth")表示这个控制器处理以/api/auth开头的请求，@Tag注解用于生成API文档，提供接口的分类和描述信息。
    private final AuthService authService;
    // 作用：注入AuthService服务，控制器通过调用AuthService的方法来处理认证相关的业务逻辑，如登录、退出登录和获取当前用户信息。通过构造函数注入的方式，确保了依赖关系的清晰和代码的可测试性。

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login") // 作用：定义一个处理HTTP POST请求的接口，路径为/api/auth/login，这个接口用于用户登录。前端发送登录请求时，应该使用POST方法，并将登录信息放在请求体中。
    @Operation(summary = "登录")  // 作用：为Swagger文档生成提供接口的简要描述，这里描述了这个接口的功能是登录。这个信息会在生成的API文档中显示，帮助前端开发者理解接口的用途。
    @OperationLog(operationType = "LOGIN", objectType = "AUTH")
    // 作用：记录登录操作的日志，operationType表示操作类型，这里是登录，objectType表示操作对象，这里是认证相关的操作。通过这个注解，可以在系统中自动记录用户的登录行为，方便后续的审计和分析。
    public Result<LoginResponseVO> login(@Valid @RequestBody LoginRequest request) {
        // @Valid 作用：启用对LoginRequest对象的验证，确保请求体中的数据符合定义的约束条件，如用户名和密码不能为空且长度限制等。如果验证失败，Spring会自动返回错误响应。
        // @RequestBody 作用：将HTTP请求体中的JSON数据转换为LoginRequest对象，方便在控制器方法中直接使用。
        return Result.success(authService.login(request));
    }

    @SaCheckLogin
    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public Result<Boolean> logout() {
        return Result.success("退出成功", authService.logout());
    }

    @SaCheckLogin
    // 只有登录用户才能访问这个接口，确保只有经过认证的用户才能获取当前用户的信息，提升系统的安全性。
    @GetMapping("/me")
    // 作用：描述接口的功能，方便前端开发者理解接口的用途，同时也会在生成的API文档中显示，提升文档的可读性和专业性。
    @Operation(summary = "获取当前用户")
    public Result<CurrentUserVO> me() {
        return Result.success(authService.getCurrentUser());
    }
}
