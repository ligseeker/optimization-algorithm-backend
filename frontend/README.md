# Optimization Frontend

## 项目定位

本目录是 `optimization-algorithm-backend` 的前端系统，面向流程优化任务管理场景，提供登录、工作空间、流程图、任务中心、结果展示、YAML 导入导出和流程图编辑器能力。

## 技术栈

- React 18 + TypeScript + Vite
- Ant Design
- React Router
- TanStack Query
- Zustand
- Axios
- React Flow
- ECharts
- Vitest
- Playwright

## 本地开发

```bash
cd frontend
npm install
npm run dev
```

默认开发环境变量位于 `.env.development`：

```env
VITE_API_BASE_URL=http://127.0.0.1:8081
```

不要在页面或组件中硬编码后端地址；所有接口请求都必须通过 `src/api/**`。

## 常用命令

```bash
npm run typecheck
npm run lint
npm run build
npm run test
npm run test:e2e
```

## 联调基线

- 后端地址：`http://127.0.0.1:8081`
- OpenAPI：`http://127.0.0.1:8081/v3/api-docs`
- 测试账号：`admin / admin123`
- 登录态请求头：`satoken: <token>`

完整联调步骤见 `../docs/frontend/INTEGRATION_GUIDE.md`。

## 主要页面

- `/login`：登录页
- `/dashboard`：系统首页
- `/workspaces`：工作空间列表与 CRUD
- `/workspaces/:workspaceId/graphs`：流程图列表、YAML 导入导出
- `/graphs/:graphId/detail`：流程图详情
- `/graphs/:graphId/editor`：React Flow 编辑器与图元 CRUD
- `/tasks`：任务中心
- `/tasks/:taskId`：任务详情与状态轮询
- `/tasks/:taskId/result`：优化结果与指标可视化

## 已知限制

- YAML 导出后原样导入存在后端 round-trip 契约问题，见 `../docs/agent-team/07_BACKEND_CHANGE_REQUESTS.md`。
- React Flow 第一版不保存拖拽布局。
- `resultGraph` 和 `diff` 以安全 JSON 方式展示。
