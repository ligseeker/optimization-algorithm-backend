# AGENTS.md

## 仓库用途

本仓库是 `optimization-algorithm-backend`，当前处于从“基于 YAML/JSON 文件的流程图与优化算法项目”向“基于 Spring Boot + MyBatis-Plus + MySQL + Redis + Sa-Token 的流程优化任务管理平台”渐进式重构阶段。

## 当前仓库结构认知

- `src/main/java/com/example/optimization_algorithm_backend/Controller`：现有接口入口。
- `src/main/java/com/example/optimization_algorithm_backend/Service`：现有服务层与实现类。
- `src/main/java/com/example/optimization_algorithm_backend/algorithm`：核心优化算法、算法模型与历史样例文件。
- `src/main/resources`：当前配置文件、模板文件、临时 YAML 输入输出目录。
- `docs/refactor`：后续重构设计、拆分、迁移与任务清单文档目录。

后续开发必须基于以上现状做增量重构，不允许假设这是一个全新的空项目。

## 重构目标

后续重构的核心目标如下：

- 使用 `MySQL` 作为最终数据源。
- 使用 `Redis` 作为缓存层。
- 使用 `MyBatis-Plus` 管理数据访问。
- 使用 `Sa-Token` 处理登录态与权限校验。
- 使用 `ThreadPoolTaskExecutor` 异步执行优化任务。

## 技术栈约束

- Java 11
- Spring Boot 2.7.14
- MyBatis-Plus
- MySQL 8
- Redis
- Sa-Token
- ThreadPoolTaskExecutor
- Knife4j 或 Springdoc OpenAPI
- Docker Compose
- JUnit

## 开发规则

- 不要推倒重写整个项目，优先做可回滚、可验证的渐进式改造。
- `algorithm` 包中的核心算法尽量少改，优先通过适配层、转换层与任务编排层接入。
- 新增 `entity`、`dto`、`vo`、`mapper`、`converter` 等分层，不要把原算法模型直接混入持久化模型。
- 旧接口可以暂时保留；新接口统一使用 `/api` 前缀。
- 所有新接口统一返回 `Result<T>`。
- Controller 只负责参数接收、鉴权入口与结果返回。
- Service 负责业务逻辑、事务边界与任务编排。
- Mapper 负责数据库访问。
- Converter 负责 `Entity / DTO / VO / 算法模型` 之间转换。
- 所有写操作要考虑事务。
- 所有资源访问必须校验当前用户权限。
- Redis 只作为缓存，不能替代 MySQL 成为事实数据源。
- 优化任务通过线程池异步执行，暂不引入 MQ。
- YAML 仅作为导入导出格式，不再作为主存储格式。

## 明确禁止事项

- 不要直接删除 `algorithm` 包。
- 不要把 `ProcessMap`、`MultiNode`、`ProcessPath`、`ConstraintCondition` 等算法模型直接改成数据库 Entity。
- 不要让 Controller 直接调用算法实现或直接 `new Algorithm1/2/3`。
- 不要在单例 Service 中保存任务上下文。
- 不要继续把 YAML 作为主存储方案。
- 不要直接返回 `Map<String, Object>` 作为新接口响应。

## 文档约定

- 重构总说明放在 `docs/refactor/00_重构总说明.md`。
- 专题设计文档放在 `docs/refactor/` 目录并按编号递增维护。
- 做结构性调整前，先补充或更新对应设计文档，再开始实现。

## 本轮仓库准备说明

本轮仅允许准备仓库协作文档与目录结构，不修改任何 Java 业务代码。
