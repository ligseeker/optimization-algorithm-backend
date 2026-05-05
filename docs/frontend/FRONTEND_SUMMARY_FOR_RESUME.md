# 前端项目简历摘要

## 项目一句话

为流程优化任务管理后端建设 React + TypeScript 前端系统，覆盖登录认证、工作空间/流程图管理、图元编辑、YAML 导入导出、异步优化任务和结果可视化。

## 技术关键词

- React 18、TypeScript、Vite
- Ant Design、React Router
- TanStack Query、Zustand
- Axios 统一请求层、Sa-Token Header 鉴权
- React Flow 图编辑器
- ECharts 指标对比图
- Vitest、Playwright

## 可写入简历的职责表达

- 设计并实现前端 API 分层，将请求封装集中在 `src/api`，类型定义集中在 `src/types`，避免页面直接耦合 HTTP 细节。
- 基于 Zustand 实现登录态、token 持久化、会话恢复和失效清理，并通过路由守卫保护业务页面。
- 使用 TanStack Query 管理工作空间、流程图、任务和结果数据，覆盖 loading、error、empty、分页、轮询和缓存失效刷新。
- 基于 React Flow 实现流程图展示和图元 CRUD 交互，支持节点、路径、装备、约束的表单校验、删除确认和保存后重新加载图详情。
- 接入 YAML 导入导出能力，按后端 JSON 导出响应生成 Blob 下载，并对导入结构化错误进行展示。
- 建立前端质量门禁和联调文档，使用 Vitest 覆盖基础页面渲染，使用 Playwright 执行 smoke 测试。

## 项目亮点边界

- 可以强调“前端工程化分层”“接口契约治理”“异步任务状态轮询”“图编辑器基础交互”“结果可视化”。
- 不应夸大为完整低代码平台或复杂实时协同编辑器。
- 当前 React Flow 版本支持图展示和 CRUD，不支持拖拽布局持久化。
- 当前 `resultGraph` 和 `diff` 后端结构未完全明确，前端采用安全 JSON 展示。

## 面试可展开问题

- 为什么页面层不能直接写 axios？
- TanStack Query 如何处理任务轮询和页面卸载？
- 登录失效为什么放在请求层统一处理？
- YAML 导出为什么不是直接下载文件流？
- React Flow 图数据如何从后端 `GraphDetailVO` 映射？
- Review Gate 如何发现硬编码后端地址等边界问题？
