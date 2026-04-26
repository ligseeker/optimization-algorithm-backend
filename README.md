# optimization-algorithm-backend

## 项目简介
`optimization-algorithm-backend` 是一个流程优化任务管理后端。项目已从“YAML/JSON 文件驱动”迁移到 “Spring Boot + MyBatis-Plus + MySQL + Redis + Sa-Token” 的结构化架构，支持流程图管理、YAML 导入导出、异步优化任务与结果查询。

## 技术栈
- Java 11
- Spring Boot 2.7.14
- MyBatis-Plus 3.5.5
- MySQL 8
- Redis
- Sa-Token
- Springdoc OpenAPI
- ThreadPoolTaskExecutor
- JUnit 5 + Mockito

## 重构后核心功能
- 登录/退出/当前用户
- 工作空间 CRUD（USER 仅可访问自己的 workspace，ADMIN 可全局访问）
- 流程图、节点、路径、装备、约束的结构化 CRUD
- YAML 导入与导出（YAML 仅作为导入导出格式）
- 优化任务提交、状态查询、失败重试、结果查询
- 简化版 Diff 与 mapCode 输出
- 操作日志 AOP（关键接口全覆盖）

## 数据库表说明
第一版核心表：
- `sys_user`
- `workspace`
- `flow_graph`（含 `graph_version`）
- `equipment`
- `process_node`
- `process_path`
- `constraint_condition`
- `optimize_task`
- `optimize_result`
- `operation_log`

初始化脚本：
- `src/main/resources/db/schema.sql`
- 升级脚本：`src/main/resources/db/upgrade/V9__add_graph_version.sql`

## Redis 缓存策略说明
- 展示态流程图聚合缓存：
  - key：`graph:detail:{graphId}:v{graphVersion}`
  - TTL：30 分钟
  - 仅用于 `GET /api/graphs/{graphId}/detail`
- 编辑态写操作：
  - 只写 MySQL + `graph_version` 自增
  - 不做 Redis 聚合缓存局部更新
- 任务状态缓存：
  - key：`optimize:task:status:{taskId}`
  - 创建/运行/成功/失败均主动更新
  - 查询未命中回源 MySQL 后回填
- 优化结果缓存：
  - key：`optimize:result:{taskId}`
  - 成功后先写 MySQL，再写 Redis
  - retry 复用 taskId 时先清理旧缓存

## 本地启动方式
1. 准备 MySQL 与 Redis。
2. 创建数据库（示例）：`optimization_platform`。
3. 执行 `src/main/resources/db/schema.sql`（首次）和升级脚本（增量）。
4. 配置环境变量（可选）：
   - `DB_URL`
   - `DB_USERNAME`
   - `DB_PASSWORD`
   - `REDIS_HOST`
   - `REDIS_PORT`
   - `REDIS_PASSWORD`
5. 启动：
```bash
mvn spring-boot:run
```

## Docker Compose 启动方式
仓库当前未内置 `docker-compose.yml`，可先使用以下示例创建：
```yaml
version: "3.8"
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: 123456
      MYSQL_DATABASE: optimization_platform
    ports:
      - "3306:3306"
    command: --default-authentication-plugin=mysql_native_password
  redis:
    image: redis:7
    ports:
      - "6379:6379"
```
启动：
```bash
docker compose up -d
```

## 默认测试账号
请在 `sys_user` 表中自行插入测试账号（密码需使用 `PasswordUtils` 生成哈希）。  
示例角色：
- `ADMIN`
- `USER`

## OpenAPI 地址
- Swagger UI：`http://127.0.0.1:8081/swagger-ui.html`
- OpenAPI JSON：`http://127.0.0.1:8081/v3/api-docs`

## 典型接口调用顺序
1. `POST /api/auth/login`
2. `POST /api/workspaces`
3. `POST /api/workspaces/{workspaceId}/graphs`
4. `POST /api/graphs/{graphId}/nodes`
5. `POST /api/graphs/{graphId}/paths`
6. `POST /api/import/graphs`（可选）
7. `GET /api/graphs/{graphId}/detail`
8. `POST /api/optimize/tasks`
9. `GET /api/optimize/tasks/{taskId}`
10. `GET /api/optimize/tasks/{taskId}/result`
11. `GET /api/export/graphs/{graphId}/yaml`

## 常见问题排查
- 登录返回 `500001 数据库未配置或不可用`：
  - 检查 MySQL 连接参数、数据库是否存在、表是否初始化。
- 登录返回 `401001`：
  - 未携带有效 `satoken`。
- 列表接口分页报参数转换异常：
  - 使用 Query 参数，不要传空字符串；当前接口已兼容空值默认分页。
- 导入 YAML 失败：
  - 检查返回中的结构化错误报告（重复节点、路径节点不存在、约束节点不存在等）。
- 导出 YAML 无“浏览器自动下载”：
  - 当前接口返回 YAML 文本（JSON），前端需转 Blob 下载；如需后端文件流可另行扩展。
