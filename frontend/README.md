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
