# 流程优化任务管理系统 - 前端设计文档 (Frontend Design)

## 0. 文档说明
本文档旨在为 `optimization-algorithm-backend` 项目的配套前端工程提供专业的功能与架构设计。基于已有的 API 契约与后端系统能力，详细规划前端的模块拆分、页面路由、组件复用、接口适配策略以及迭代演进路线。

---

## 1. 功能模块设计

本系统前端按业务领域划分为以下核心模块：

### 1.1 登录认证 (Auth)
- 负责用户身份凭证的获取与注销。
- 维护全局的用户会话状态（Zustand Store）。
- 拦截未授权的路由访问，保障系统安全。

### 1.2 工作空间管理 (Workspace)
- **数据隔离基座**：用户的所有业务数据（图、任务、结果）均挂载于特定工作空间下。
- 支持工作空间的创建、编辑、删除与列表查询。
- 提供全局的“当前工作空间”切换上下文。

### 1.3 流程图管理 (Graph Management)
- 维护工作空间下的流程图元数据（名称、描述、来源等）。
- 提供卡片式/列表式的流程图浏览界面。
- 支持流程图的新建、编辑基础信息以及删除操作。

### 1.4 流程图编辑器 (Graph Editor)
- **可视化核心**：基于 React Flow 实现的拓扑图画布。
- 支持节点（Node）、连线（Path）、装备（Equipment）和约束（Constraint）的可视化展示与交互式 CRUD。
- 提供属性侧边栏，实现选中图元的详细参数配置。

### 1.5 YAML 导入导出 (Import/Export)
- 屏蔽底层数据构造复杂度，允许用户通过上传 YAML 文件快速生成完整的流程图拓扑。
- 支持将当前画布状态导出为标准 YAML 格式，供离线分析或归档。

### 1.6 优化任务中心 (Task Center)
- 提供发起优化算法的入口（配置权重与算法模式）。
- **任务大厅**：展示历史与进行中的优化任务，支持多状态筛选。
- **状态轮询**：针对执行中的任务提供进度追踪与自动刷新机制。
- 提供失败任务的快捷重试能力。

### 1.7 优化结果展示 (Result Dashboard)
- 展示算法优化前后的核心指标对比（耗时、精度、成本）。
- 提供结构差异可视化（Diff 分析），展示新增或移除的节点与路径。
- 基于 `mapCode` 渲染优化后的目标拓扑（Mermaid 渲染或只读 React Flow 画布）。

### 1.8 首页仪表盘 (Dashboard)
- 工作空间维度的数据汇总：流程图总数、累计执行任务数、成功率、近期活跃图表等。
- 提供快捷操作入口（新建图、最近任务）。

### 1.9 操作日志 (Operation Log)
- 系统级审计模块。
- 列表化展示用户的关键写操作记录，支持按时间、操作类型检索。

### 1.10 系统设置 (Settings)
- 提供基础的系统级参数配置界面（预留扩展位）。
- 个人资料设置、默认工作空间设置等。

---

## 2. 页面设计

系统采用标准的后台管理布局（Top-Side-Content 模式）。

### 2.1 登录页
- **路由**: `/login`
- **布局**: BlankLayout (无导航栏的居中布局)
- **用户操作流程**: 用户输入账号密码 -> 点击登录 -> 存储 Token -> 获取用户信息 -> 跳转至 `/workspace`。
- **对应接口**:
  - `POST /api/auth/login` (登录)
  - `GET /api/auth/me` (获取当前用户)

### 2.2 工作空间列表页
- **路由**: `/workspace`
- **布局**: BasicLayout (带顶部用户信息栏)
- **主要组件**: `WorkspaceCard`, `CreateWorkspaceModal`
- **用户操作流程**: 查看拥有的空间列表 -> 点击某空间卡片 -> 将空间 ID 存入全局上下文 -> 跳转至 `/workspace/:workspaceId/dashboard`。
- **对应接口**:
  - `GET /api/workspaces` (列表查询)
  - `POST /api/workspaces` (创建)
  - `PUT /api/workspaces/{id}` (编辑)
  - `DELETE /api/workspaces/{id}` (删除)

### 2.3 流程图列表页
- **路由**: `/workspace/:workspaceId/graph`
- **布局**: BasicLayout (带侧边菜单栏)
- **主要组件**: `GraphTable`/`GraphGrid`, `ImportYamlModal`
- **用户操作流程**: 在当前空间下查看流程图 -> 点击“导入”上传 YAML 文件 -> 点击某流程图“详情”进入编辑器。
- **对应接口**:
  - `GET /api/workspaces/{workspaceId}/graphs`
  - `POST /api/workspaces/{workspaceId}/graphs`
  - `POST /api/import/graphs` (导入 YAML)
  - `GET /api/export/graphs/{graphId}/yaml` (导出 YAML)
  - `DELETE /api/graphs/{graphId}`

### 2.4 流程图编辑器/详情页
- **路由**: `/workspace/:workspaceId/graph/:graphId`
- **布局**: FullScreenLayout (全屏无侧边栏布局，最大化画布空间)
- **主要组件**: `ReactFlowCanvas`, `ToolBar`, `PropertiesPanel` (节点/连线属性表单)
- **用户操作流程**: 进入页面请求聚合详情 -> 渲染画布 -> 选中节点/连线在右侧面板修改参数 -> 点击保存调用后端接口 -> 顶部工具栏点击“提交优化”。
- **对应接口**:
  - `GET /api/graphs/{graphId}/detail` (获取图与所有子资源的聚合数据)
  - `POST/PUT/DELETE /api/graphs/{graphId}/nodes` (节点 CRUD)
  - `POST/PUT/DELETE /api/graphs/{graphId}/paths` (路径 CRUD)
  - `POST/PUT/DELETE /api/graphs/{graphId}/constraints` (约束 CRUD)
  - `POST/PUT/DELETE /api/graphs/{graphId}/equipments` (装备 CRUD)

### 2.5 优化任务中心
- **路由**: `/workspace/:workspaceId/task`
- **布局**: BasicLayout
- **主要组件**: `TaskFilterBar`, `TaskTable`, `StatusBadge`
- **用户操作流程**: 筛选任务状态 -> 观察进行中任务的进度 -> 对失败任务点击“重试” -> 对成功任务点击“查看结果”跳转至结果页。
- **对应接口**:
  - `POST /api/optimize/tasks` (提交任务，通常在编辑器页调用)
  - `GET /api/optimize/tasks` (查询任务列表)
  - `GET /api/optimize/tasks/{taskId}` (轮询状态)
  - `POST /api/optimize/tasks/{taskId}/retry` (重试)

### 2.6 优化结果视图
- **路由**: `/workspace/:workspaceId/task/:taskId/result`
- **布局**: BasicLayout
- **主要组件**: `MetricsComparisonCard` (ECharts指标卡), `DiffViewer` (结构差异分析), `TopologyPreview` (基于 mapCode 的结果预览)
- **用户操作流程**: 进入页面获取结果 -> 顶部查看三大指标(耗时/精度/成本)的升降 -> 中部查看删减的节点/连线 -> 底部查看最终生成的拓扑图。
- **对应接口**:
  - `GET /api/optimize/tasks/{taskId}/result` (获取结果明细、指标差异与 mapCode)
  - `GET /api/optimize-tasks/{taskId}/export-yaml` (导出结果为 YAML)

---

## 3. 组件设计

组件层面采用 Atomic Design 理念，提取高复用度 UI。

### 3.1 通用基础组件 (Common)
- **`ProTable`**: 基于 AntD Table 二次封装，内置分页逻辑、请求 loading 联动。
- **`ModalForm`**: 弹窗表单组件，封装常用的新增/编辑交互与表单验证规则。
- **`StatusTag`**: 状态标签，统一管理诸如 `PENDING` (蓝色), `RUNNING` (动态蓝色), `SUCCESS` (绿色), `FAILED` (红色) 的 UI 映射。

### 3.2 业务复用组件 (Business)
- **`WorkspaceSelector`**: 顶部导航栏的下拉组件，用于快速切换当前激活的工作空间。
- **`MetricsTrend`**: 带上升/下降箭头的数值卡片，专用于优化结果页的性能指标展示。

### 3.3 图编辑器组件 (Graph Editor)
- **`FlowCanvas`**: 封装 React Flow 核心逻辑，处理节点拖拽、连线逻辑及与 Zustand 的状态同步。
- **`CustomNode`**: 定制的 React Flow 节点组件，支持展示节点名称、图标及状态角标。
- **`InspectorPanel`**: 右侧可收起的多态属性检查器。根据画布选中的元素类型（图本身 / Node / Path / Constraint），动态渲染对应的配置表单。

---

## 4. API 适配设计

针对后端的 `/api` 规范，在前端架构中建立坚固的 API 适配层。

### 4.1 目录结构
```text
src/
  ├── api/
  │    ├── request.ts      # Axios 实例及拦截器
  │    ├── auth.ts         # 认证接口定义
  │    ├── workspace.ts    # 工作空间接口
  │    ├── graph.ts        # 流程图及画布元数据接口
  │    └── task.ts         # 优化任务与结果接口
  ├── types/
  │    ├── api.d.ts        # 通用请求/响应类型
  │    └── models.d.ts     # Graph, Node, Task, Result 等业务实体类型
```

### 4.2 TypeScript 类型设计
确保类型与后端 DTO/VO 1:1 对齐：
```typescript
// 通用响应
export interface Result<T> {
  code: number;
  message: string;
  data: T;
}

// 分页响应
export interface PageResult<T> {
  records: T[];
  pageNo: number;
  pageSize: number;
  total: number;
}
```

### 4.3 Axios 拦截器与错误处理 (Interceptors)
- **请求拦截 (Request)**:
  - 从 `localStorage` 获取 `token`。
  - 自动注入 Header: `satoken: <token_value>`。
- **响应拦截 (Response)**:
  - **HTTP 状态码层面**: 处理网络断开、502 网关错误等极端情况。
  - **业务状态码层面** (`res.data.code`):
    - `code === 0`: 剥离外层结构，直接 `return res.data.data`。
    - `code === 401001 (未登录/Token过期)`: 拦截器内部触发登出动作，清空本地存储，并重定向至 `/login`。
    - `code === 403001 (无权限)`: 提示权限不足，可跳转至 403 异常页。
    - `code === 400001 / 500001 / 600001` 等: 统一调用 Ant Design 的 `message.error(res.data.message)` 进行静默提示，并阻断 Promise 链 (`Promise.reject`)，防止页面渲染错误数据。

---

## 5. 开发优先级与迭代演进路线

为匹配后端重构节奏，快速验证业务闭环，前端实施以下分阶段开发计划：

### 第一阶段 (MVP - 最小业务闭环)
**目标**：跑通“创建环境 -> 导入数据 -> 发起计算 -> 查看结果”的主流程，暂时绕过复杂的图编辑器开发。
- **实现内容**:
  1. 基础架构搭建 (Vite + React + AntD + Axios 拦截器)。
  2. 登录页 (`/login`) 与全站 Auth 路由守卫。
  3. 工作空间 CRUD 及其列表页 (`/workspace`)。
  4. 流程图列表页，并接入 **YAML 导入导出** 接口。
  5. 流程图详情页（**只读模式**）：调用 `detail` 接口，仅使用简单的表格或树状组件展示节点/路径数据，暂不渲染画布。
  6. 提交优化任务对话框。
  7. 任务列表页与状态轮询。
  8. 结果展示页（渲染 ECharts 指标对比及基于 `mapCode` 的 Mermaid 轻量级图表预览）。

### 第二阶段 (核心攻坚 - 流程图编辑器)
**目标**：彻底取代 YAML 文件手写，实现网页端的图拓扑结构全量可视与可编辑。
- **实现内容**:
  1. 引入 `React Flow`。
  2. 开发流程图编辑器页面 (`/workspace/:wsId/graph/:graphId`)。
  3. 将后端的 `detail` 数据结构解析为 React Flow 的 `nodes` 与 `edges`。
  4. 实现可视化画布上的节点拖拽、连线连接。
  5. 对接后端的 `/nodes`, `/paths`, `/equipments`, `/constraints` CRUD 接口，实现画布操作与后端持久化存储的双向绑定。

### 第三阶段 (体验完善与运营支撑)
**目标**：提升系统监控能力与易用性。
- **实现内容**:
  1. 工作空间首页仪表盘（Dashboard 数据统计）。
  2. 操作日志列表页对接。
  3. 系统全局配置、异常状态流转体验优化（如细化导入 YAML 时的错误报告渲染）。
  4. 装备（Equipment）模块的图片上传交互完善。