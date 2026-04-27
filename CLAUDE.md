# CLAUDE.md

## 项目背景

本项目是流程优化任务管理系统，后端基于 Spring Boot + MyBatis-Plus + MySQL + Redis + Sa-Token。前端目标是实现流程图管理、节点/路径/装备/约束编辑、YAML 导入导出、异步优化任务提交、任务状态轮询、优化结果展示和操作日志查看。

## 技术栈

前端使用：

- React 18
- TypeScript
- Vite
- Ant Design
- React Router
- TanStack Query
- Zustand
- Axios
- React Flow
- ECharts
- Vitest
- Playwright

## 后端接口约定

- 新接口统一使用 `/api` 前缀。
- 统一返回结构为 `Result<T>`。
- 登录认证使用 Sa-Token。
- 前端请求需要在 header 中携带 token。
- 401001 表示未登录或登录失效。
- 数据库不可用时可能返回 500001。
- 流程图详情接口优先使用 `GET /api/graphs/{graphId}/detail`。
- 优化任务状态通过 `GET /api/optimize/tasks/{taskId}` 查询。
- 优化结果通过 `GET /api/optimize/tasks/{taskId}/result` 查询。

## 代码规范

- 所有前端代码使用 TypeScript。
- 禁止使用 any，除非添加 TODO 注释说明原因。
- API 类型必须放在 `src/types`。
- API 请求必须放在 `src/api`。
- 页面组件放在 `src/pages`。
- 可复用组件放在 `src/components`。
- 状态管理放在 `src/stores`。
- 不要在页面组件中直接写 axios 请求。
- 所有异步请求优先使用 TanStack Query。
- 所有表单必须有前端校验。
- 所有删除操作必须有二次确认。
- 所有接口错误必须通过统一错误处理展示。

## 常用命令

```bash
cd frontend
pnpm install
pnpm dev
pnpm lint
pnpm typecheck
pnpm test
pnpm build