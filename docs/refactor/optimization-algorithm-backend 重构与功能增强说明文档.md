

# optimization-algorithm-backend 重构与功能增强说明文档

## 0. 文档目的

本文面向 `optimization-algorithm-backend` 项目的后续重构开发，说明在原有 Spring Boot 后端项目基础上，如何将当前基于文件读写的流程优化系统，重构为基于 **MySQL + Redis + MyBatis-Plus + Sa-Token + 异步任务执行** 的后端服务。

重构目标不是推倒重写，而是在保留原有算法能力、流程图结构、节点/路径/约束/装备管理逻辑的基础上，完成以下升级：

1. 将 YAML/JSON 文件存储改造为 MySQL 结构化持久化；
2. 将 YAML 文件保留为导入/导出格式，而不是系统主存储；
3. 引入 Redis 缓存流程图详情、优化任务状态和优化结果；
4. 引入 Sa-Token 完成登录认证、角色校验和工作空间数据隔离；
5. 将原同步算法接口改造成优化任务中心，使用线程池异步执行；
6. 增加工作空间、任务状态机、简化版结果 Diff、导入校验、统一异常处理、接口文档和 Docker Compose 部署能力；
7. 提升项目在后端工程化、可维护性、可扩展性和面试展示方面的价值。

------

# 1. 原项目现状与主要问题

## 1.1 当前技术栈

当前项目是 Spring Boot 项目，Java 版本为 11，Spring Boot 版本为 2.7.14。`pom.xml` 中目前主要包含 Web、Test、Devtools 和 `android-json` 等依赖，尚未引入数据库、Redis、ORM、认证鉴权、接口文档等工程化组件。

当前项目的基础技术栈可以概括为：

```text
Java 11
Spring Boot 2.7.14
Spring Web
文件存储
YAML/JSON 解析
自定义算法模块
```

需要新增的技术栈：

```text
MySQL 8
MyBatis-Plus
Redis
Sa-Token
ThreadPoolTaskExecutor
Knife4j / Springdoc OpenAPI
Spring Validation
Docker Compose
JUnit
Apifox / Postman
```

------

## 1.2 当前模块结构

当前项目已经有 Controller、Service、algorithm/model 的初步结构：

```text
Controller
├── AlgorithmController
├── NodeController
├── EquipmentController
├── ConstraintController
├── PathController
├── DirectoryController
├── ExampleController
└── MapController

Service
└── Impl
    ├── AlgorithmServiceImpl
    ├── NodeServiceImpl
    ├── EquipmentServiceImpl
    ├── ConstraintServiceImpl
    ├── PathServiceImpl
    └── ExampleServiceImpl

algorithm
├── algorithm1
├── algorithm2
├── algorithm3
└── model
    ├── ProcessMap
    ├── MultiNode
    ├── ProcessPath
    ├── Equipment
    └── ConstraintCondition
```

问题在于：

- 缺少 Mapper / Repository 数据访问层；
- Controller 中混入了较多业务逻辑；
- Service 层同时承担文件读写、算法调用、临时文件管理、结果转换等职责；
- 算法模型和业务数据模型没有区分；
- 接口命名偏函数式，不符合 REST 风格；
- 返回值多为 `Map<String, Object>`，缺少统一响应结构；
- 文件路径、文件名、业务对象强耦合。

------

## 1.3 当前数据流

当前典型流程为：

```text
前端上传 YAML/JSON 文件
    ↓
Controller 接收 MultipartFile
    ↓
Service 保存文件到 history/input/output/temp 目录
    ↓
读取文件并转换为 ProcessMap
    ↓
调用 Algorithm1 / Algorithm2 / Algorithm3
    ↓
生成新的 ProcessMap
    ↓
写入 output/temp YAML 文件
    ↓
返回 mapCode、节点、路径、约束、指标等数据
```

例如，`AlgorithmController` 中的优化接口会接收文件、保存文件、读取为 `ProcessMap`、创建算法对象、执行优化并组装返回结果。

节点操作也是类似逻辑：`NodeController` 根据 `example` 拼接 YAML 文件路径，检查文件是否存在，再调用服务修改 YAML 文件。

------

## 1.4 当前核心模型

原有算法模型需要保留，但不能直接作为数据库 Entity 使用。

`ProcessMap` 当前用于表示完整流程图，内部包含节点、路径、约束、装备以及总时间、总精度、总成本等字段。

`MultiNode` 当前用于表示流程节点，包含节点 ID、节点描述、装备名称、时间、精度、成本等字段。

`ProcessPath` 当前表示流程路径，包含路径 ID、起点节点 ID、终点节点 ID。

`ConstraintCondition` 当前表示约束条件，包含约束 ID、约束描述、约束类型、两个关联节点 ID。

重构时应区分：

```text
algorithm.model：算法执行使用的内存模型
entity：数据库表映射对象
dto：接口请求对象
vo：接口响应对象
converter：算法模型、Entity、DTO、VO 之间转换
```

------

## 1.5 当前主要问题

### 1. 文件存储不适合作为主存储

当前节点新增、删除、修改等操作都是读取 YAML 文件，修改内存对象后再写回文件。

问题：

- 查询效率低；
- 无法分页、筛选、排序；
- 多用户并发修改存在覆盖写风险；
- 删除节点、路径、约束时无法使用数据库事务；
- 很难进行权限隔离；
- 数据迁移、审计、统计困难。

### 2. Service 存在并发风险

`AlgorithmServiceImpl` 中存在 `mapList`、`inputInfo`、`oldPaths` 等成员变量，并且该类作为 Spring Service 默认是单例对象。

风险：

- 多个用户同时提交优化任务时，成员变量可能互相覆盖；
- 中间结果可能串任务；
- 结果 Diff 可能使用错误的 oldPaths；
- 线上并发环境下存在不可预期问题。

重构要求：

> 算法执行上下文必须局部化，不允许把任务中间状态放在单例 Service 成员变量中。

### 3. Controller 职责过重

当前 Controller 中包含文件保存、算法实例化、返回结果组装等逻辑。
重构后 Controller 只负责：

```text
接收请求
参数校验
调用 Service
返回 Result<T>
```

### 4. 接口风格不统一

当前接口类似：

```text
/addNode
/updateNode
/deleteNode
/getNodeInfo
/optimizeByFile
/optimizeByInput
```

重构后应改为 REST 风格：

```text
POST   /api/graphs/{graphId}/nodes
PUT    /api/graphs/{graphId}/nodes/{nodeId}
DELETE /api/graphs/{graphId}/nodes/{nodeId}
GET    /api/graphs/{graphId}/nodes/{nodeId}

POST /api/optimize/tasks
GET  /api/optimize/tasks/{taskId}
GET  /api/optimize/tasks/{taskId}/result
```

### 5. 缺少用户、权限和数据隔离

当前所有数据通过文件目录区分，没有用户隔离、工作空间隔离和访问控制。

重构后需要：

```text
用户
  └── 工作空间
        └── 流程图
              ├── 节点
              ├── 路径
              ├── 装备
              └── 约束
```

普通用户只能访问自己的工作空间，管理员可以访问全部数据。

------

# 2. 重构总体目标

## 2.1 最终定位

项目重构后定位为：

> **规则约束驱动的流程优化任务管理平台后端**

核心业务链路：

```text
用户登录
    ↓
创建工作空间
    ↓
导入 / 创建流程图
    ↓
管理节点、路径、装备、约束
    ↓
提交优化任务
    ↓
线程池异步执行算法
    ↓
查询任务状态
    ↓
查询优化结果
    ↓
查看简化版 Diff
    ↓
导出 YAML / 结果文件
```

------

## 2.2 最终技术栈

```text
Java 11
Spring Boot 2.7.14
MyBatis-Plus
MySQL 8
Redis
Sa-Token
ThreadPoolTaskExecutor
Spring Validation
Knife4j 或 Springdoc OpenAPI
Docker Compose
JUnit
Apifox / Postman
```

### 技术选择理由

| 技术                   | 选择理由                                                |
| ---------------------- | ------------------------------------------------------- |
| MyBatis-Plus           | 适合快速重构 CRUD、分页、逻辑删除、条件查询             |
| MySQL                  | 适合结构化管理流程图、节点、路径、约束和任务数据        |
| Redis                  | 适合缓存流程图详情、任务状态、优化结果                  |
| Sa-Token               | 比 Spring Security 轻量，适合快速实现登录认证和角色权限 |
| ThreadPoolTaskExecutor | 适合第一版实现异步优化任务，避免引入 MQ 造成复杂度过高  |
| Knife4j / OpenAPI      | 用于接口文档展示，提升前后端联调和项目展示效果          |
| Docker Compose         | 一键启动 MySQL、Redis、后端服务，便于部署和演示         |

------

# 3. 重构范围确定

## 3.1 必做功能

```text
1. MySQL 持久化
2. MyBatis-Plus 数据访问层
3. Redis 缓存
4. Sa-Token 登录认证
5. 工作空间
6. 流程图 CRUD
7. 节点 CRUD
8. 路径 CRUD
9. 装备 CRUD
10. 约束 CRUD
11. YAML 导入
12. YAML 导出
13. 优化任务提交
14. 任务状态查询
15. 线程池异步执行
16. 优化结果查询
17. 简化版结果 Diff
18. 统一返回体
19. 全局异常处理
20. 参数校验
21. 操作日志
22. 接口文档
23. Docker Compose 部署
```

------

## 3.2 简化版功能边界

为了保证可落地，第一版不做过度复杂设计。

### 权限控制

第一版只做：

```text
ADMIN
USER
```

不做：

```text
菜单权限
按钮权限
复杂 RBAC
部门权限
多租户权限表达式
```

### 优化任务

第一版只做：

```text
PENDING
RUNNING
SUCCESS
FAILED
```

暂不做：

```text
任务取消
精确进度条
任务暂停
任务优先级队列
分布式任务调度
```

### Diff

第一版只做：

```text
新增路径
删除路径
总时间变化
总成本变化
总精度变化
```

暂不做：

```text
复杂节点属性 Diff
约束 Diff
装备 Diff
图版本回滚
完整 graph_diff 表
```

### 缓存

第一版只缓存：

```text
流程图详情
优化任务状态
优化结果详情
```

暂不缓存所有分页列表。

------

# 4. 推荐重构后的包结构

建议重构为：

```text
com.example.optimization
├── OptimizationApplication.java
├── common
│   ├── result
│   │   ├── Result.java
│   │   ├── PageResult.java
│   │   └── ErrorCode.java
│   ├── exception
│   │   ├── BusinessException.java
│   │   └── GlobalExceptionHandler.java
│   ├── config
│   │   ├── MybatisPlusConfig.java
│   │   ├── RedisConfig.java
│   │   ├── SaTokenConfig.java
│   │   ├── WebMvcConfig.java
│   │   └── ThreadPoolConfig.java
│   ├── constant
│   └── util
├── auth
│   ├── controller
│   ├── service
│   ├── entity
│   ├── mapper
│   ├── dto
│   └── vo
├── workspace
│   ├── controller
│   ├── service
│   ├── entity
│   ├── mapper
│   ├── dto
│   └── vo
├── graph
│   ├── controller
│   ├── service
│   ├── entity
│   ├── mapper
│   ├── dto
│   ├── vo
│   └── converter
├── node
├── path
├── equipment
├── constraint
├── importfile
│   ├── controller
│   ├── service
│   ├── parser
│   ├── validator
│   └── vo
├── optimize
│   ├── controller
│   ├── service
│   ├── executor
│   ├── entity
│   ├── mapper
│   ├── dto
│   ├── vo
│   └── converter
├── log
│   ├── aspect
│   ├── entity
│   ├── mapper
│   └── annotation
└── algorithm
    ├── algorithm1
    ├── algorithm2
    ├── algorithm3
    └── model
```

注意：

- `algorithm` 包尽量少改，避免破坏已有算法；
- 新增 `converter` 负责 Entity 和 `ProcessMap` 转换；
- 原 Controller 不建议直接保留，应逐步迁移为 REST API；
- 文件导入导出逻辑单独放到 `importfile` 模块；
- 优化任务相关逻辑放到 `optimize` 模块。

------

# 5. 数据库设计说明

## 5.1 第一版核心表

第一版建议使用以下表：

```text
sys_user
workspace
flow_graph
equipment
process_node
process_path
constraint_condition
optimize_task
optimize_result
operation_log
```

如时间充足，可以再加：

```text
import_record
```

暂时不单独建：

```text
graph_diff
flow_version
optimize_step_result
```

这些可以在第二版扩展。

------

## 5.2 表关系

```text
sys_user 1 - N workspace
workspace 1 - N flow_graph
flow_graph 1 - N equipment
flow_graph 1 - N process_node
flow_graph 1 - N process_path
flow_graph 1 - N constraint_condition
flow_graph 1 - N optimize_task
optimize_task 1 - 1 optimize_result
```

------

## 5.3 建表重点

### `sys_user`

用于登录认证。

核心字段：

```text
id
username
password_hash
nickname
role
status
created_at
updated_at
deleted
```

第一版可以直接在用户表中放 `role` 字段，不必拆 `sys_role` 和 `sys_user_role`。

原因：

- 项目时间紧；
- 只有 ADMIN / USER 两种角色；
- 后续需要复杂权限时再拆表。

------

### `workspace`

用于工作空间隔离。

核心字段：

```text
id
user_id
name
description
status
created_at
updated_at
deleted
```

注意事项：

- 普通用户只能访问自己的 workspace；
- 管理员可以访问全部 workspace；
- 删除 workspace 时建议逻辑删除，不做物理删除；
- 删除 workspace 时，其下 flow_graph 也应逻辑删除。

------

### `flow_graph`

用于流程图基本信息。

核心字段：

```text
id
workspace_id
name
description
source_type
total_time
total_precision
total_cost
created_by
created_at
updated_at
deleted
```

注意事项：

- `source_type` 可取值：`MANUAL`、`IMPORT`、`OPTIMIZED`；
- `total_time`、`total_precision`、`total_cost` 是冗余字段，便于列表查询；
- 详情接口仍需查询节点、路径、装备、约束。

------

### `equipment`

对应原装备对象。

核心字段：

```text
id
graph_id
name
description
color
image_path
created_at
updated_at
deleted
```

注意事项：

- 不要再用 `equipmentName` 作为节点强关联；
- 节点表中应使用 `equipment_id`；
- 装备名称允许修改，不应影响节点关联。

------

### `process_node`

对应原 `MultiNode`。

核心字段：

```text
id
graph_id
node_code
node_description
equipment_id
node_type
time_cost
precision_value
cost_value
created_at
updated_at
deleted
```

注意事项：

- `node_code` 对应原来的 `nodeID`；
- 同一个 `graph_id` 下，`node_code` 必须唯一；
- 删除节点时，要同步删除相关路径和约束；
- 节点的 `time_cost`、`precision_value`、`cost_value` 后续用于优化指标计算。

------

### `process_path`

对应原 `ProcessPath`。

核心字段：

```text
id
graph_id
start_node_id
end_node_id
relation_type
created_at
deleted
```

注意事项：

- `start_node_id` 和 `end_node_id` 使用数据库节点主键；
- 同一流程图下，不允许重复路径；
- 新增路径时必须校验起点、终点都存在；
- 删除节点时必须删除关联路径。

------

### `constraint_condition`

对应原 `ConstraintCondition`。

核心字段：

```text
id
graph_id
condition_code
condition_type
condition_description
node_id_1
node_id_2
enabled
created_at
updated_at
deleted
```

约束类型：

```text
CONNECT
SAME
FOLLOW
CONTAIN
CALL
NORMAL
PARTICIPATE
```

注意事项：

- 约束关联节点必须存在；
- 新增/修改约束时要校验 `condition_type` 是否合法；
- 可以复用原 `checkMap` 中的约束校验逻辑，但应迁移到 `ConstraintValidateService`。

------

### `optimize_task`

用于优化任务状态管理。

核心字段：

```text
id
task_no
graph_id
workspace_id
user_id
algorithm_type
time_weight
precision_weight
cost_weight
x1
x2
status
error_message
started_at
finished_at
created_at
updated_at
deleted
```

任务状态：

```text
PENDING
RUNNING
SUCCESS
FAILED
```

注意事项：

- 提交任务时先写入 `PENDING`；
- 线程池开始执行时改为 `RUNNING`；
- 成功后改为 `SUCCESS`；
- 异常时改为 `FAILED`，并记录 `error_message`；
- 任务状态同时写 MySQL 和 Redis；
- MySQL 是最终状态源，Redis 是加速查询。

------

### `optimize_result`

用于保存优化结果。

核心字段：

```text
id
task_id
graph_id
workspace_id
result_graph_json
diff_json
map_code
total_time
total_precision
total_cost
created_at
```

注意事项：

- 第一版可以把完整结果存在 `result_graph_json` 中；
- 简化版 Diff 存在 `diff_json` 中；
- `map_code` 保留当前前端图展示能力；
- 常用指标冗余为普通字段，便于列表排序和查询。

------

### `operation_log`

用于操作日志。

核心字段：

```text
id
user_id
operation_type
object_type
object_id
request_uri
request_method
request_params
result_status
error_message
cost_time_ms
created_at
```

注意事项：

- 使用 AOP 实现；
- 不要记录密码、token 等敏感信息；
- 操作失败也要记录；
- 先记录关键接口即可，不必覆盖所有接口。

------

# 6. Redis 缓存设计

## 6.1 缓存内容

第一版只缓存三类：

```text
graph:detail:{graphId}
optimize:task:status:{taskId}
optimize:result:{taskId}
```

------

## 6.2 流程图详情缓存

Key：

```text
graph:detail:{graphId}
```

Value：

```json
{
  "graph": {},
  "nodes": [],
  "paths": [],
  "equipments": [],
  "constraints": []
}
```

过期时间：

```text
30 分钟
```

失效时机：

```text
新增/修改/删除节点
新增/删除路径
新增/修改/删除装备
新增/修改/删除约束
导入流程图
删除流程图
优化生成新结果
```

------

## 6.3 优化任务状态缓存

Key：

```text
optimize:task:status:{taskId}
```

Value：

```json
{
  "taskId": 1,
  "status": "RUNNING"
}
```

过期时间：

```text
任务执行中：10 分钟
任务完成后：1 小时
```

注意：

- 前端轮询任务状态时优先查 Redis；
- Redis 未命中时查 MySQL；
- MySQL 状态必须是最终可信状态。

------

## 6.4 优化结果缓存

Key：

```text
optimize:result:{taskId}
```

过期时间：

```text
2 小时
```

缓存内容：

```text
result_graph_json
diff_json
map_code
total_time
total_precision
total_cost
```

------

## 6.5 缓存一致性策略

采用 Cache Aside 模式：

```text
读数据：
先查 Redis
未命中查 MySQL
再写入 Redis

写数据：
先写 MySQL
成功后删除 Redis
```

注意：

- 不做 Redis 和 MySQL 双写；
- 不在事务未提交前写缓存；
- 删除缓存失败时至少记录日志；
- 第一版不做复杂消息队列补偿。

------

# 7. 权限与工作空间设计

## 7.1 使用 Sa-Token

使用 Sa-Token 实现：

```text
登录
退出
获取当前用户
角色校验
登录校验
工作空间访问控制
```

第一版建议角色：

```text
ADMIN
USER
```

------

## 7.2 登录接口

```text
POST /api/auth/login
POST /api/auth/logout
GET  /api/auth/me
```

登录成功后返回：

```json
{
  "userId": 1,
  "username": "admin",
  "role": "ADMIN",
  "token": "xxx"
}
```

------

## 7.3 权限边界

| 操作                   | ADMIN | USER   |
| ---------------------- | ----- | ------ |
| 查看全部工作空间       | 可以  | 不可以 |
| 查看自己工作空间       | 可以  | 可以   |
| 创建工作空间           | 可以  | 可以   |
| 修改他人工作空间       | 可以  | 不可以 |
| 删除他人流程图         | 可以  | 不可以 |
| 提交自己流程图优化任务 | 可以  | 可以   |
| 查看他人优化结果       | 可以  | 不可以 |

实现方式：

```text
1. 从 Sa-Token 获取当前 userId
2. 查询 workspace.user_id 是否等于当前 userId
3. 如果是 ADMIN，放行
4. 如果是 USER 且不是本人资源，拒绝访问
```

不要只依赖前端传入的 userId。

------

# 8. 优化任务中心设计

## 8.1 为什么要做任务中心

原项目中优化接口是同步执行，接口直接调用算法并返回结果。
重构后应改为：

```text
提交优化任务
    ↓
返回 taskId
    ↓
后台线程池执行算法
    ↓
前端轮询任务状态
    ↓
查询优化结果
```

这样有几个好处：

- 避免接口长时间阻塞；
- 支持失败重试；
- 支持任务历史记录；
- 支持结果缓存；
- 更接近真实企业系统；
- 适合面试深挖。

------

## 8.2 任务状态机

第一版状态：

```text
PENDING  待执行
RUNNING  执行中
SUCCESS  执行成功
FAILED   执行失败
```

状态流转：

```text
PENDING -> RUNNING -> SUCCESS
PENDING -> RUNNING -> FAILED
FAILED  -> PENDING -> RUNNING
```

暂不做：

```text
CANCELED
PAUSED
PROGRESS
```

------

## 8.3 异步执行流程

```text
POST /api/optimize/tasks
    ↓
校验用户是否有 graph 访问权限
    ↓
创建 optimize_task，状态 PENDING
    ↓
写入 Redis 状态
    ↓
提交到 ThreadPoolTaskExecutor
    ↓
线程开始执行，状态改为 RUNNING
    ↓
从 MySQL 查询 graph 结构
    ↓
Converter 转为 ProcessMap
    ↓
调用 Algorithm1 / Algorithm2 / Algorithm3
    ↓
生成结果 ProcessMap
    ↓
计算简化 Diff
    ↓
保存 optimize_result
    ↓
写 Redis 结果缓存
    ↓
任务状态改为 SUCCESS
```

异常处理：

```text
捕获异常
    ↓
记录 error_message
    ↓
状态改为 FAILED
    ↓
写 Redis 状态
    ↓
记录错误日志
```

------

## 8.4 注意事项

1. 不允许在单例 Service 中保存本次任务的 `oldPaths`、`mapList`、`inputInfo`；
2. 任务上下文应定义为局部对象，例如 `OptimizeContext`；
3. 算法执行失败不能导致整个服务崩溃；
4. 任务执行异常必须保存到数据库；
5. 线程池参数不要设置过大；
6. 第一版不需要 MQ；
7. 可以保留后续扩展 MQ 的接口设计。

------

# 9. YAML 导入导出设计

## 9.1 重构原则

YAML 不再作为主存储，而是作为：

```text
导入格式
导出格式
兼容旧数据格式
测试数据格式
```

------

## 9.2 导入流程

```text
上传 YAML
    ↓
解析文件
    ↓
转换为 ProcessMap
    ↓
格式校验
    ↓
业务校验
    ↓
写入 MySQL
    ↓
返回 graphId
```

校验内容：

```text
文件是否为空
文件类型是否为 yaml/json
节点 ID 是否唯一
路径起点是否存在
路径终点是否存在
约束关联节点是否存在
约束类型是否合法
time/cost/precision 是否合法
装备名称是否重复
```

导入失败时返回错误报告，而不是只返回“上传失败”。

------

## 9.3 导出流程

```text
根据 graphId 查询 MySQL
    ↓
组装 ProcessMap
    ↓
生成 YAML
    ↓
返回下载文件
```

注意：

- 导出接口不能直接读取旧文件；
- 导出的 YAML 应来自数据库当前状态；
- 优化结果也可以导出为 YAML 或 ZIP。

------

# 10. 简化版 Diff 设计

## 10.1 第一版 Diff 内容

第一版只计算：

```text
新增路径
删除路径
总时间变化
总成本变化
总精度变化
```

------

## 10.2 Diff 计算方式

输入：

```text
oldMap
newMap
```

路径统一表示为：

```text
startNodeCode + "->" + endNodeCode
```

计算：

```text
addedPaths = newPaths - oldPaths
removedPaths = oldPaths - newPaths
```

指标变化：

```text
timeDiff = newTotalTime - oldTotalTime
costDiff = newTotalCost - oldTotalCost
precisionDiff = newTotalPrecision - oldTotalPrecision
```

结果示例：

```json
{
  "addedPaths": [
    {"from": "A", "to": "B"}
  ],
  "removedPaths": [
    {"from": "C", "to": "D"}
  ],
  "metricDiff": {
    "time": {"before": 120, "after": 100, "change": -20},
    "cost": {"before": 300, "after": 260, "change": -40},
    "precision": {"before": 0.81, "after": 0.87, "change": 0.06}
  }
}
```

第一版直接存入 `optimize_result.diff_json` 即可。

------

# 11. 接口设计规范

## 11.1 统一响应

所有接口统一返回：

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

错误示例：

```json
{
  "code": 400001,
  "message": "参数错误：节点 ID 不能为空",
  "data": null
}
```

------

## 11.2 错误码建议

```text
0       成功
400001  参数错误
401001  未登录
403001  无权限
404001  资源不存在
409001  数据冲突
500001  系统异常
600001  优化任务执行失败
700001  文件解析失败
```

------

## 11.3 REST API 命名

工作空间：

```text
POST   /api/workspaces
GET    /api/workspaces
GET    /api/workspaces/{workspaceId}
PUT    /api/workspaces/{workspaceId}
DELETE /api/workspaces/{workspaceId}
```

流程图：

```text
POST   /api/workspaces/{workspaceId}/graphs
GET    /api/workspaces/{workspaceId}/graphs
GET    /api/graphs/{graphId}
PUT    /api/graphs/{graphId}
DELETE /api/graphs/{graphId}
```

节点：

```text
POST   /api/graphs/{graphId}/nodes
GET    /api/graphs/{graphId}/nodes
GET    /api/graphs/{graphId}/nodes/{nodeId}
PUT    /api/graphs/{graphId}/nodes/{nodeId}
DELETE /api/graphs/{graphId}/nodes/{nodeId}
```

路径：

```text
POST   /api/graphs/{graphId}/paths
GET    /api/graphs/{graphId}/paths
DELETE /api/graphs/{graphId}/paths/{pathId}
```

装备：

```text
POST   /api/graphs/{graphId}/equipments
GET    /api/graphs/{graphId}/equipments
PUT    /api/graphs/{graphId}/equipments/{equipmentId}
DELETE /api/graphs/{graphId}/equipments/{equipmentId}
```

约束：

```text
POST   /api/graphs/{graphId}/constraints
GET    /api/graphs/{graphId}/constraints
PUT    /api/graphs/{graphId}/constraints/{constraintId}
DELETE /api/graphs/{graphId}/constraints/{constraintId}
```

导入导出：

```text
POST /api/import/graphs
GET  /api/export/graphs/{graphId}/yaml
GET  /api/export/tasks/{taskId}/yaml
```

优化任务：

```text
POST /api/optimize/tasks
GET  /api/optimize/tasks
GET  /api/optimize/tasks/{taskId}
GET  /api/optimize/tasks/{taskId}/result
POST /api/optimize/tasks/{taskId}/retry
```

------

# 12. 代码迁移注意事项

## 12.1 不要一次性删除旧接口

建议采用两阶段策略：

```text
第一阶段：新增新接口，旧接口保留
第二阶段：前端切换到新接口后，再删除旧接口
```

原因：

- 防止一次性改动过大；
- 便于对照测试；
- 便于回滚。

------

## 12.2 不要直接把 algorithm.model 改成 Entity

错误做法：

```text
给 ProcessMap / MultiNode / ProcessPath 直接加 @TableName
```

正确做法：

```text
Entity：数据库映射
algorithm.model：算法模型
Converter：二者互转
```

原因：

- 算法模型是内存结构；
- 数据库模型是关系结构；
- 强行合并会导致后续维护困难。

------

## 12.3 不要让 Controller 直接调用算法

错误做法：

```java
Algorithm1 algorithm = new Algorithm1();
algorithm.OptimizeMap(...);
```

正确做法：

```text
Controller
    ↓
OptimizeTaskService
    ↓
AlgorithmExecutor
    ↓
Algorithm1 / Algorithm2 / Algorithm3
```

------

## 12.4 不要把任务上下文放在 Service 成员变量

错误做法：

```java
public ArrayList<ProcessMap> mapList = new ArrayList<>();
public InputInfo inputInfo;
public LinkedList<ProcessPath> oldPaths = new LinkedList<>();
```

正确做法：

```java
OptimizeContext context = new OptimizeContext();
context.setOldMap(oldMap);
context.setOldPaths(oldPaths);
context.setFactors(factors);
```

每个任务创建自己的上下文对象。

------

## 12.5 文件路径不能再作为业务主键

原来大量通过：

```text
historyPath + example + ".yaml"
```

来定位流程图。

重构后应使用：

```text
graphId
workspaceId
taskId
```

YAML 文件名只用于导入导出，不再作为业务主键。

------

## 12.6 删除操作必须用事务

例如删除节点时，需要同步处理：

```text
删除节点
删除关联路径
删除关联约束
删除缓存
更新图指标
```

应使用：

```java
@Transactional
```

注意：

- 删除缓存应在数据库事务成功后执行；
- 如果简单实现，可以先在事务方法最后删除缓存；
- 更严谨做法是事务提交后回调删除缓存。

------

## 12.7 数据权限必须在后端校验

不能相信前端传入的 `userId`、`workspaceId`。

所有涉及资源访问的接口都要做：

```text
当前用户是否登录
当前用户是否有该 workspace 访问权限
当前用户是否有该 graph 操作权限
```

------

# 13. 开发优先级建议

## 第一阶段：基础设施

目标：项目能连接 MySQL、Redis，并有统一接口规范。

任务：

```text
1. 修改 pom.xml
2. 配置 application.yml
3. 配置 MyBatis-Plus
4. 配置 Redis
5. 配置 Sa-Token
6. 新增 Result<T>
7. 新增 BusinessException
8. 新增 GlobalExceptionHandler
9. 新增 Knife4j / OpenAPI
10. 新增 Docker Compose
```

------

## 第二阶段：数据库表与基础 CRUD

目标：完成工作空间和流程图基础管理。

任务：

```text
1. 建 sys_user
2. 建 workspace
3. 建 flow_graph
4. 建 equipment
5. 建 process_node
6. 建 process_path
7. 建 constraint_condition
8. 完成 Mapper / Service / Controller
9. 完成分页查询
10. 完成逻辑删除
```

------

## 第三阶段：YAML 导入导出

目标：把旧文件数据接入新数据库结构。

任务：

```text
1. 实现 YAML 上传
2. 复用原解析逻辑生成 ProcessMap
3. 校验节点、路径、约束
4. 转换为 Entity
5. 批量写入 MySQL
6. 返回 graphId
7. 实现从 MySQL 导出 YAML
```

------

## 第四阶段：优化任务中心

目标：将原同步算法接口改造成异步任务。

任务：

```text
1. 建 optimize_task
2. 建 optimize_result
3. 实现任务提交接口
4. 实现 ThreadPoolTaskExecutor
5. 实现 AlgorithmExecutor
6. 实现 ProcessMapConverter
7. 保存优化结果
8. 计算简化 Diff
9. 查询任务状态
10. 查询优化结果
11. 实现失败重试
```

------

## 第五阶段：缓存、日志和完善

目标：增加项目亮点和可维护性。

任务：

```text
1. 缓存流程图详情
2. 缓存任务状态
3. 缓存优化结果
4. 添加操作日志 AOP
5. 完善接口文档
6. 补充单元测试
7. 补充 README
8. 补充部署说明
```

------

# 14. 测试策略

## 14.1 单元测试

重点测试：

```text
ProcessMapConverter
GraphImportValidator
ConstraintValidateService
DiffService
OptimizeTaskService
CacheService
```

测试用例：

```text
节点 ID 重复
路径引用不存在节点
约束关联节点不存在
删除节点后关联路径删除
删除节点后关联约束删除
优化任务成功
优化任务失败
失败任务重试
Diff 新增路径
Diff 删除路径
```

------

## 14.2 集成测试

使用真实 MySQL / Redis 或测试容器。

核心流程：

```text
用户登录
创建工作空间
导入 YAML
查询流程图详情
新增节点
新增路径
新增约束
提交优化任务
查询任务状态
查询优化结果
导出 YAML
```

------

## 14.3 接口测试

使用 Apifox 或 Postman 保存测试集合。

必须覆盖：

```text
登录接口
工作空间接口
流程图接口
节点接口
路径接口
装备接口
约束接口
导入接口
优化任务接口
结果查询接口
导出接口
```

------

## 14.4 性能测试

第一版简单压测即可。

关注：

```text
流程图详情查询
节点分页查询
任务状态轮询
优化结果查询
YAML 导入
```

目标：

```text
流程详情缓存命中 < 100ms
任务状态缓存命中 < 50ms
普通分页查询 < 300ms
中等规模 YAML 导入可稳定完成
```

------

# 15. 最终注意事项清单

重构过程中务必注意：

```text
1. 不要直接删除旧接口，先新增新接口并保持兼容。
2. 不要把 ProcessMap、MultiNode、ProcessPath 直接改成数据库 Entity。
3. 不要让 Controller 直接调用算法类。
4. 不要在单例 Service 中保存任务上下文。
5. 不要继续用文件名作为业务主键。
6. 不要让 YAML 文件继续作为主存储。
7. 所有写操作都要考虑事务。
8. 删除节点时必须处理关联路径和约束。
9. 写数据库后要删除相关 Redis 缓存。
10. 权限校验必须在后端完成，不能相信前端 userId。
11. 优化任务失败必须记录状态和错误信息。
12. Redis 只是缓存，MySQL 才是最终数据源。
13. 第一版不要引入 MQ、Spring Cloud、复杂 RBAC，避免项目失控。
14. 所有新增接口必须纳入接口文档。
15. 所有核心功能必须能通过 Apifox/Postman 演示。
```

------

# 16. 建议最终落地版本

第一版重构完成后，系统应至少支持：

```text
1. 用户登录
2. 工作空间管理
3. 流程图管理
4. 节点管理
5. 路径管理
6. 装备管理
7. 约束管理
8. YAML 导入
9. YAML 导出
10. 优化任务提交
11. 异步任务执行
12. 任务状态查询
13. 优化结果查询
14. 简化版结果 Diff
15. Redis 缓存
16. 操作日志
17. 接口文档
18. Docker Compose 部署
```

