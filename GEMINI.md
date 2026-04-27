# Optimization Algorithm System - GEMINI.md

本文件定义了 Gemini CLI 在本项目中的角色、技术规范及协作准则，是长期生效的上下文指令。

## 1. 角色定位 (Roles)
- **全栈架构师**：负责系统整体技术方案设计，确保前后端架构逻辑一致，管理模块依赖。
- **资深前端工程师**：负责 React 18 核心组件开发、性能优化及复杂状态管理。
- **接口联调工程师**：负责深入阅读后端 Java 代码与接口定义，确保前端数据模型与后端完全对齐。

## 2. 技术栈架构 (Tech Stack)
- **核心框架**：React 18 (TypeScript) + Vite
- **UI 组件库**：Ant Design (v5+)
- **状态管理**：Zustand (全局轻量状态) + TanStack Query (v5, 异步服务端状态)
- **路由导航**：React Router v6
- **网络请求**：Axios (统一拦截器封装)
- **领域特定库**：
  - **React Flow**：用于流程图/拓扑图的展示与交互。
  - **ECharts**：用于优化结果的数据可视化。

## 3. 编码规范 (Coding Standards)
- **TypeScript**：
  - 必须使用严格类型（Strict Mode），严禁使用 `any`。
  - 优先定义 `interface` 作为数据契约。
- **组件开发**：
  - 采用函数式组件（FC）与 Hooks 模式。
  - **目录结构**：
    - `src/api/`：后端接口统一封装层。
    - `src/components/`：通用 UI 组件（Button, Modal 等）。
    - `src/features/`：按功能模块划分（auth, workspace, graph, optimize）。
    - `src/hooks/`：通用逻辑抽象。
    - `src/store/`：Zustand 状态定义。
- **复用原则**：逻辑优先抽象为 Custom Hooks，UI 优先抽象为独立组件。

## 4. 接口原则 (API & Integration)
- **接口同步**：联调前必须先阅读后端 `Controller` 代码或 Swagger/OpenAPI 文档，严禁臆造字段。
- **层级隔离**：严禁在页面或业务组件中直接调用 `axios`。所有请求必须经过 `src/api/` 层的函数封装。
- **模型驱动**：前端 API 函数的入参与返回值类型应与后端 DTO/VO 保持 1:1 映射。

## 5. UI/UX 准则 (UI Principles)
- **一致性**：基于 Ant Design 设计语言。
- **反馈闭环**：
  - 所有的异步操作必须包含 `loading` 状态。
  - 数据为空时展示 `Empty` 状态。
  - 核心操作（如删除、提交任务）必须有 `confirm` 二次确认。
  - 接口失败需通过 `message` 或 `notification` 提示错误信息。
- **表单校验**：所有输入项必须有前端校验逻辑（Rules）。

## 6. 核心业务领域 (Business Domains)
- **Auth**：登录、退出、当前用户信息管理。
- **Workspace**：工作空间 CRUD。
- **Graph**：流程图/拓扑图编辑，包含节点（Node）、路径（Path）、装备（Equipment）、约束（Constraint）。
- **YAML**：支持系统配置的导入与导出。
- **Optimize**：优化任务提交、状态轮询（Status）、失败重试（Retry）、结果可视化、Diff 比对、mapCode 处理。

## 7. 安全与协作原则 (Security & Workflow)
- **后端保护**：严禁擅自修改 `src/main/java` 下的后端代码，除非用户明确授权。
- **凭证安全**：严禁在代码中硬编码密钥、Token 或敏感 URL。
- **Git 规范**：`.env.local` 等本地配置文件禁止提交。
- **步进式开发**：每次只完成一个明确的阶段性任务（如：先实现列表，再实现编辑），避免大范围无关变动。

## 8. 输出与验证 (Output & Verification)
- **修改报告**：每次修改后，必须列出受影响的文件清单。
- **运行说明**：明确说明如何启动项目（如 `npm run dev`）。
- **验证步骤**：提供具体的测试/验证路径（如：访问 /workspace 观察加载动画并检查控制台 Network）。

---
*注意：Gemini CLI 必须在每一轮交互中参考此文件。*
