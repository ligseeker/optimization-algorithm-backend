# 项目上下文

## 项目概览

- 后端项目名称：`optimization-algorithm-backend`
- 仓库定位：当前仓库以 Spring Boot 后端为主，前端将在同仓库 `frontend/` 目录中建设并与后端联调
- 当前目标：先建立 agent team 工作流控制台，后续以连续执行的方式推进前端系统开发

## 后端技术栈

- Java 11
- Spring Boot 2.7.14
- MyBatis-Plus 3.5.5
- MySQL 8
- Redis
- Sa-Token
- Springdoc OpenAPI
- ThreadPoolTaskExecutor
- JUnit 5 + Mockito

## 后端核心功能

- 登录、退出、当前用户信息查询
- 工作空间 CRUD
- 流程图 CRUD
- 节点、路径、装备、约束的结构化 CRUD
- YAML 导入与导出
- 异步优化任务提交、状态查询、失败重试、结果查询
- Diff / mapCode 输出
- 操作日志记录

## 前端目标

- 基于 `React 18 + TypeScript + Vite` 建立前端应用
- 完成登录认证、布局导航、路由守卫
- 完成工作空间与流程图列表页
- 完成任务中心与优化结果页
- 完成 YAML 导入导出能力
- 完成流程图编辑器与图元 CRUD
- 建立可维护的 API 层、类型层、状态管理、测试与文档体系

## 前后端联调方式

- 后端本地默认端口：`http://127.0.0.1:8081`
- OpenAPI JSON：`http://127.0.0.1:8081/v3/api-docs`
- Swagger UI：`http://127.0.0.1:8081/swagger-ui.html`
- 前端必须通过 `VITE_API_BASE_URL` 配置后端地址，禁止硬编码
- 认证方案以 Sa-Token 为准，默认重点确认 `satoken` 的请求头传递方式、过期行为和登出清理行为
- 接口优先使用 `/api` 前缀的新接口；旧接口仅用于兼容，且必须隔离在 `legacyAlgorithm.ts`
- 登录接口返回 `token` 与 `tokenName`，其中 `tokenName` 当前代码返回 `satoken`
- 新接口统一采用 `Result<T>` 包装；分页列表采用 `Result<PageResult<T>>`

## 已确认的新接口分组

- 认证：`/api/auth`
- 工作空间：`/api/workspaces`
- 流程图：`/api/workspaces/{workspaceId}/graphs`、`/api/graphs/{graphId}`
- 节点：`/api/graphs/{graphId}/nodes`
- 路径：`/api/graphs/{graphId}/paths`
- 装备：`/api/graphs/{graphId}/equipments`
- 约束：`/api/graphs/{graphId}/constraints`
- 优化任务：`/api/optimize/tasks`
- YAML：`/api/import/graphs`、`/api/export/graphs/{graphId}/yaml`

## 当前已确认的契约细节

- `POST /api/import/graphs` 使用 `multipart/form-data`
- YAML 导入除文件外还需要表单字段：`workspaceId`，可选 `graphName`
- `GET /api/export/graphs/{graphId}/yaml` 当前返回 `GraphYamlExportResponse` JSON，而不是直接文件流
- `POST /api/optimize/tasks` 的请求体中包含 `graphId`，而不是把 `graphId` 放在路径里
- `POST /api/optimize/tasks/{taskId}/retry` 当前无需请求体
- `GET /api/graphs/{graphId}` 返回基础信息，`GET /api/graphs/{graphId}/detail` 返回聚合详情
- 运行中的 `OpenAPI` 文档当前可访问，但会同时暴露 `/api/**` 新接口与历史 legacy 控制器接口

## 契约来源优先级

- 第一优先级：`src/main/java/**/controller/**` 中的真实控制器映射
- 第二优先级：对应 `dto` / `vo` / `common/response` 类型定义
- 第三优先级：运行中的 OpenAPI 文档
- 第四优先级：`docs/refactor/03_API设计.md` 等设计草案

当设计草案与真实代码不一致时，必须以控制器与 DTO/VO 为准。

## 兼容旧接口

- `/optimizeByFile`
- `/optimizeByInput`
- `/uploadFile`
- `/downloadFile`

这些接口仅作为兼容能力规划，不得污染新业务 API 设计。

## 重要约束

- 本轮只允许创建 `docs/agent-team/` 下的控制台文件
- 暂时不要创建或修改前端工程代码
- 暂时不要写 React 代码
- 不要修改后端业务代码
- 不做大范围重构
- 所有接口调用未来必须放在 `frontend/src/api`
- 所有接口类型未来必须放在 `frontend/src/types`
- 所有页面未来必须覆盖 `loading / error / empty`
- 所有表单未来必须具备前端校验
- 所有删除操作未来必须二次确认
- 所有异步轮询未来必须在页面卸载时停止
- 登录失效未来必须清理 token 并跳转 `/login`
- 接口相关代码改动后必须同步维护 `docs/frontend/API_CONTRACT.md`
- 不允许多个 agent 同时修改同一个文件，除非主控 Agent 明确允许

## 当前仓库观察

- 根目录实际存在的规则文件为 `AGENTS.md`
- 仓库中已存在 `frontend/` 目录，但本轮不对其进行初始化、清理或改造
- 当前工作区存在其他未由本轮产生的 Git 变更，后续执行需避免误覆盖
- `OpenAPI` 不能直接等价于“前端可用接口白名单”，因为其中混有遗留控制器暴露出的旧接口
