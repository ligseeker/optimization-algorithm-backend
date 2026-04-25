# CLAUDE.md

## 项目定位

本项目是 optimization-algorithm-backend，原本基于 YAML/JSON 文件读写管理流程图数据，并调用多目标优化算法。当前重构目标是将项目升级为基于 Spring Boot + MyBatis-Plus + MySQL + Redis + Sa-Token 的流程优化任务管理平台。

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

## 重构边界

- 不要推倒重写整个项目。
- algorithm 包中的核心算法尽量少改。
- 不要把 ProcessMap、MultiNode、ProcessPath、ConstraintCondition 直接改成数据库 Entity。
- 新增 entity、dto、vo、mapper、converter 层。
- YAML 只作为导入/导出格式，不再作为主存储。
- 旧接口暂时保留，新接口使用 /api 前缀。
- 所有新接口统一返回 Result<T>。
- 所有写操作要考虑事务。
- 所有资源访问必须校验当前用户权限。
- Redis 只是缓存，MySQL 是最终数据源。
- 优化任务使用线程池异步执行，暂不引入 MQ。

## 功能目标

必须实现：
1. 用户登录与 Sa-Token 鉴权
2. 工作空间管理
3. 流程图、节点、路径、装备、约束结构化管理
4. YAML 导入导出
5. 优化任务中心
6. 任务状态查询
7. 优化结果查询
8. 简化版 Diff
9. Redis 缓存
10. 操作日志
11. 接口文档
12. Docker Compose

## 代码风格

- Controller 只负责参数接收和响应。
- Service 负责业务逻辑。
- Mapper 负责数据库访问。
- Converter 负责 Entity/DTO/VO/算法模型转换。
- 不要在单例 Service 中保存任务上下文。
- 不要直接返回 Map<String, Object>。
- 不要在 Controller 中直接 new Algorithm1/2/3。