# 前端 API 契约与对接指南 (API Contract)

本文档基于后端 `README.md`、重构文档（`03_API设计.md`、`07_开发任务清单.md`）以及实际代码实现生成，旨在为前端开发提供准确的接口调用规范和优先级指引。

## 1. 后端系统功能总览

`optimization-algorithm-backend` 是一个基于 Spring Boot + MyBatis-Plus + Redis + Sa-Token 的流程优化任务管理后端。其核心功能包括：
- **认证与权限**：登录/退出、当前用户信息获取、工作空间隔离（普通用户只能访问自己的资源，管理员可全局访问）。
- **结构化业务实体 CRUD**：工作空间（Workspace）、流程图（Graph）、流程节点（Node）、流程路径（Path）、装备（Equipment）、约束条件（Constraint）。
- **数据导入导出**：支持 YAML 格式的流程图导入与导出。
- **异步优化任务**：提交算法优化任务（利用 ThreadPoolTaskExecutor 异步执行）、任务状态轮询查询、失败任务重试。
- **结果与差异分析**：优化结果查询（包含时间、精度、成本变化）、结构差异（Diff）分析及供前端渲染的 mapCode（Mermaid 格式）输出。

---

## 2. 新旧接口区分说明

后端项目目前处于重构阶段，代码中存在两套平行的接口：

- **新版重构接口（前端应当对接的目标）**
  - **路径特征**：统一以 `/api` 为前缀（例如：`/api/auth/login`, `/api/workspaces`）。
  - **代码位置**：位于 `src/main/java/com/example/optimization_algorithm_backend/module/**/controller/` 包下。
  - **结构特征**：统一使用 `Result<T>` 包装响应（包含 `code`, `message`, `data`），受 Sa-Token 鉴权保护。

- **旧版遗留接口（前端应忽略）**
  - **路径特征**：无统一前缀（例如：`/optimizeByFile`, `/addNode`, `/getRandomMap` 等）。
  - **代码位置**：位于 `src/main/java/com/example/optimization_algorithm_backend/Controller/` 根包下。
  - **废弃计划**：新功能不再使用，前端在重构过程中无需调用，未来版本将逐步下线。

---

## 3. 前端需要调用的接口清单 (新版 API)

以下接口为前端需要对接的核心功能清单。根据后端实际代码的扫描结果，标记了各接口的实现状态。

> ⚠️ **注意：部分代码实际路径与《03_API设计.md》存在微调，以下表格以实际代码（或最接近设计）为准进行批注。**

### 3.1 认证模块 (Auth)

| 接口名称 | Method | Path | 代码状态 | 认证要求 | 使用页面 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 登录 | POST | `/api/auth/login` | ✅ 已存在 | 无 | 登录页 |
| 退出登录 | POST | `/api/auth/logout` | ✅ 已存在 | 需登录 | 顶部导航栏 |
| 当前用户 | GET | `/api/auth/me` | ✅ 已存在 | 需登录 | 路由守卫/全局状态 |

### 3.2 工作空间模块 (Workspace)

| 接口名称 | Method | Path | 代码状态 | 认证要求 | 使用页面 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 创建工作空间 | POST | `/api/workspaces` | ✅ 已存在 | 需登录 | 工作空间列表/新建弹窗 |
| 查询工作空间列表 | GET | `/api/workspaces` | ✅ 已存在 | 需登录 | 工作空间列表页 |
| 查询详情 | GET | `/api/workspaces/{workspaceId}` | ✅ 已存在 | 需登录 | - |
| 修改工作空间 | PUT | `/api/workspaces/{workspaceId}` | ✅ 已存在 | 需登录 | 工作空间编辑弹窗 |
| 删除工作空间 | DELETE | `/api/workspaces/{workspaceId}` | ✅ 已存在 | 需登录 | 工作空间列表/删除确认 |

### 3.3 流程图模块 (Graph)

| 接口名称 | Method | Path | 代码状态 | 认证要求 | 使用页面 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 创建流程图 | POST | `/api/workspaces/{workspaceId}/graphs` | ✅ 已存在 | 需登录 | 流程图列表/新建弹窗 |
| 查询流程图列表 | GET | `/api/workspaces/{workspaceId}/graphs` | ✅ 已存在 | 需登录 | 流程图列表页 |
| 查询基础信息 | GET | `/api/graphs/{graphId}` | ✅ 已存在 | 需登录 | - |
| **查询聚合详情** | GET | `/api/graphs/{graphId}/detail` | ✅ 已存在 | 需登录 | 流程图画布页（核心展示数据） |
| 更新基础信息 | PUT | `/api/graphs/{graphId}` | ✅ 已存在 | 需登录 | 流程图属性侧边栏 |
| 删除流程图 | DELETE | `/api/graphs/{graphId}` | ✅ 已存在 | 需登录 | 流程图列表 |

### 3.4 流程节点模块 (Node)

| 接口名称 | Method | Path | 代码状态 | 认证要求 | 使用页面 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 新增节点 | POST | `/api/graphs/{graphId}/nodes` | ✅ 已存在 | 需登录 | 画布/节点编辑栏 |
| 查询节点列表 | GET | `/api/graphs/{graphId}/nodes` | ✅ 已存在 | 需登录 | 画布初始化(也可由detail接口提供) |
| 查询节点详情 | GET | `/api/graphs/{graphId}/nodes/{nodeId}` | ✅ 已存在 | 需登录 | 节点属性面板 |
| 修改节点 | PUT | `/api/graphs/{graphId}/nodes/{nodeId}` | ✅ 已存在 | 需登录 | 节点属性面板 |
| 删除节点 | DELETE | `/api/graphs/{graphId}/nodes/{nodeId}` | ✅ 已存在 | 需登录 | 画布右键/删除操作 |

### 3.5 流程路径模块 (Path)

| 接口名称 | Method | Path | 代码状态 | 认证要求 | 使用页面 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 新增路径 | POST | `/api/graphs/{graphId}/paths` | ✅ 已存在 | 需登录 | 画布连线操作 |
| 查询路径列表 | GET | `/api/graphs/{graphId}/paths` | ✅ 已存在 | 需登录 | 画布初始化 |
| 查询路径详情 | GET | `/api/graphs/{graphId}/paths/{pathId}` | ✅ 已存在 | 需登录 | 连线属性面板 |
| 修改路径 | PUT | `/api/graphs/{graphId}/paths/{pathId}` | ✅ 已存在 | 需登录 | 连线属性面板 |
| 删除路径 | DELETE | `/api/graphs/{graphId}/paths/{pathId}` | ✅ 已存在 | 需登录 | 选中连线按 Delete |

### 3.6 装备模块 (Equipment)

| 接口名称 | Method | Path | 代码状态 | 认证要求 | 使用页面 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 新增装备 | POST | `/api/graphs/{graphId}/equipments` | ✅ 已存在 | 需登录 | 装备管理字典页/弹窗 |
| 查询装备列表 | GET | `/api/graphs/{graphId}/equipments` | ✅ 已存在 | 需登录 | 节点下拉选择/装备字典页 |
| 修改装备 | PUT | `/api/graphs/{graphId}/equipments/{equipmentId}`| ✅ 已存在 | 需登录 | 装备编辑 |
| 删除装备 | DELETE | `/api/graphs/{graphId}/equipments/{equipmentId}`| ✅ 已存在 | 需登录 | 装备管理 |
| 查询装备详情 | GET | `/api/graphs/{graphId}/equipments/{equipmentId}`| ✅ 已存在 | 需登录 | - |
| 上传装备图片 | POST | `/api/graphs/{graphId}/equipments/{equipmentId}/image` | ⚠️ 待后端确认 | 需登录 | 装备管理/上传图片 |

### 3.7 约束模块 (Constraint)

| 接口名称 | Method | Path | 代码状态 | 认证要求 | 使用页面 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 新增约束 | POST | `/api/graphs/{graphId}/constraints` | ✅ 已存在 | 需登录 | 画布/约束配置面板 |
| 修改约束 | PUT | `/api/graphs/{graphId}/constraints/{constraintId}`| ✅ 已存在 | 需登录 | 约束配置面板 |
| 删除约束 | DELETE | `/api/graphs/{graphId}/constraints/{constraintId}`| ✅ 已存在 | 需登录 | 约束配置面板 |
| 查询约束列表 | GET | `/api/graphs/{graphId}/constraints` | ✅ 已存在 | 需登录 | - |

### 3.8 导入导出模块 (Import/Export)

| 接口名称 | Method | Path | 代码状态 | 认证要求 | 使用页面 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 导入 YAML 为流程图 | POST | `/api/import/graphs` | ✅ 已存在 | 需登录 | 流程图列表/导入弹窗 |
| 导出流程图为 YAML | GET | `/api/export/graphs/{graphId}/yaml` | ✅ 已存在 | 需登录 | 流程图操作栏 |
| 导出优化结果 YAML | GET | `/api/optimize-tasks/{taskId}/export-yaml` | ⚠️ 待后端确认 | 需登录 | 优化结果展示页 |

*(注：API设计文档中写的导入路径是 `/api/workspaces/{workspaceId}/graphs/import-yaml`，但在 `GraphYamlController` 中实际路径为 `/api/import/graphs`。建议前端按照代码实际路径调用，并在请求体或参数中附带 workspaceId)*

### 3.9 优化任务与结果模块 (Optimize Task)

| 接口名称 | Method | Path | 代码状态 | 认证要求 | 使用页面 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 提交优化任务 | POST | `/api/optimize/tasks` | ✅ 已存在 | 需登录 | 流程图画布/提交优化 |
| 查询优化任务列表 | GET | `/api/optimize/tasks` | ✅ 已存在 | 需登录 | 任务大厅/侧边栏 |
| 查询任务状态 | GET | `/api/optimize/tasks/{taskId}` | ✅ 已存在 | 需登录 | 任务轮询/进度条 |
| 重试失败任务 | POST | `/api/optimize/tasks/{taskId}/retry` | ✅ 已存在 | 需登录 | 任务失败时的重试按钮 |
| 查询优化结果 | GET | `/api/optimize/tasks/{taskId}/result` | ✅ 已存在 | 需登录 | 优化结果图比对页 |
| 查询工作空间下结果| GET | `/api/optimize-results` | ⚠️ 待后端确认 | 需登录 | 结果大厅 |

*(注：API设计文档中写的提交任务路径是 `/api/graphs/{graphId}/optimize-tasks`，但在 `OptimizeTaskController` 中实际映射根路径是 `/api/optimize/tasks`。前端应遵循实际代码路径，graphId 应放在 request body 中。)*

---

## 4. 通用响应与错误处理建议

### 4.1 统一响应结构
所有 `/api/**` 接口遵循如下格式：
```json
{
  "code": 0,
  "message": "success",
  "data": {} // 实际业务数据或分页对象(records, total, pageNo, pageSize)
}
```

### 4.2 全局 Axios 拦截器处理策略
前端在封装 Axios 实例时，应进行以下统一处理：
1. **Header 注入**：从 `localStorage` 或 `Zustand` 读取 token，写入 `satoken` header。
2. **响应拦截**：
   - 若 `res.data.code === 0`，提取并返回 `res.data.data`。
   - 若 `res.data.code === 401001`，触发前端强制退出登逻辑（清除 token 并跳转 `/login`）。
   - 其他非 0 错误（如 `400001 参数错误`, `500001 系统异常` 等），通过 Ant Design 的 `message.error(res.data.message)` 进行统一的 UI 错误提示。

---

## 5. 前端开发优先级与阶段建议

为了平稳对接后端重构并快速产出可用原型，建议前端开发按下述优先级执行：

### P0 阶段：基础框架与工作空间隔离
1. 搭建基础框架 (Vite + React 18 + Zustand + React Router + Ant Design)。
2. 实现全局 Request 封装与错误拦截。
3. **对接 Auth 接口**：登录页面及全局路由鉴权（Auth Guard）。
4. **对接 Workspace 接口**：实现工作空间的展示、创建和切换，这是后续所有资源的入口前置条件。

### P1 阶段：核心实体操作与拓扑图搭建
1. 实现针对某个 Workspace 的 **Graph 列表**及其基础信息编辑。
2. 引入 `React Flow`。
3. **对接 Graph Detail 与子实体 CRUD**：通过调用 `/api/graphs/{graphId}/detail` 获取整体图数据渲染至 React Flow 画布，同时实现左侧/右侧边栏，完成 Node, Path, Equipment, Constraint 的新建与修改调用。
4. **导入导出**：打通 `/api/import/graphs` 实现无感初始化图结构。

### P2 阶段：优化计算闭环与结果可视化
1. 提交优化任务，并实现在界面的 Loading 状态及轮询 `/api/optimize/tasks/{taskId}` 接口更新任务状态。
2. 渲染失败重试机制。
3. 任务成功后，调用 `/api/optimize/tasks/{taskId}/result`：
   - 渲染优化前后指标变化（ECharts 柱状图/指标卡）。
   - 将返回的 `mapCode` 交给 Mermaid 或再次利用 React Flow 结合 Diff 节点颜色进行拓扑图的前后差异对比展示。
