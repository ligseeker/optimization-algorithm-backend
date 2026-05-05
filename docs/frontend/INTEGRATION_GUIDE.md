# 前后端联调指南

## 1. 联调目标

本指南用于本地验证前端与后端主链路是否可以协同运行，覆盖登录、工作空间、流程图、图元、任务、结果、YAML 导入导出和退出登录。

## 2. 环境要求

- Node.js：使用当前前端 `package-lock.json` 对应依赖版本
- Java：11
- Maven：可运行后端 `mvn spring-boot:run`
- MySQL：已初始化 `schema.sql` 和必要升级脚本
- Redis：本地或配置指向可用实例

## 3. 后端启动

在仓库根目录启动后端：

```bash
mvn spring-boot:run
```

默认后端地址：

```text
http://127.0.0.1:8081
```

启动后先检查：

```bash
curl http://127.0.0.1:8081/v3/api-docs
```

如果端口冲突，先确认是否已有用户手动启动的后端实例；不要直接修改后端业务代码。

## 4. 前端启动

在 `frontend/` 目录执行：

```bash
npm install
npm run dev
```

开发环境变量：

```env
VITE_API_BASE_URL=http://127.0.0.1:8081
```

前端页面、组件不得直接写后端地址，必须通过 `VITE_API_BASE_URL` 和 `src/api/**`。

## 5. 测试账号

```text
username: admin
password: admin123
```

登录成功后后端返回 `token` 与 `tokenName`，前端默认按以下请求头访问受保护接口：

```text
satoken: <token>
```

## 6. 手动主链路验证

1. 打开前端开发地址，进入 `/login`。
2. 使用 `admin / admin123` 登录。
3. 登录成功后进入 `/dashboard`。
4. 进入 `/workspaces`，查询工作空间列表。
5. 创建一个工作空间，确认列表刷新。
6. 点击工作空间进入 `/workspaces/:workspaceId/graphs`。
7. 创建一个流程图，确认列表刷新。
8. 进入 `/graphs/:graphId/editor`。
9. 新增节点、路径、装备、约束，确认保存后页面重新加载图详情。
10. 从流程图列表导入 `.yaml / .yml` 文件，确认导入统计展示。
11. 从流程图列表导出 YAML，确认浏览器下载文件。
12. 进入 `/tasks` 提交优化任务。
13. 在 `/tasks/:taskId` 查看任务状态，运行中任务会自动轮询。
14. 任务成功后进入 `/tasks/:taskId/result` 查看指标、结果图、diff 和 mapCode。
15. 点击顶部退出登录，确认会清理会话并返回 `/login`。

## 7. 质量门禁

前端本地提交前至少运行：

```bash
npm run typecheck
npm run lint
npm run build
```

涉及页面或交互变更时运行：

```bash
npm run test
```

端到端联调阶段运行：

```bash
npm run test:e2e
```

## 8. 失败归属记录

如果发现接口问题，不直接修改 `src/main/**`。按以下方式记录：

- 前端适配问题：在当前前端任务中做最小兼容并记录到 `docs/agent-team/05_ISSUES.md`
- 后端接口缺陷：记录到 `docs/agent-team/05_ISSUES.md` 和 `docs/agent-team/07_BACKEND_CHANGE_REQUESTS.md`
- 测试数据缺失：记录缺少的数据、影响页面和阻塞任务
- API 契约不一致：优先以真实控制器和运行态响应为准，更新问题记录后再决定是否需要用户授权修改后端

## 9. 当前已知说明

- YAML 导出接口返回 JSON 包装的 `yamlContent`，前端转 Blob 下载。
- `resultGraph` 和 `diff` 当前按 `unknown` 保守展示为 JSON，不编造后端字段。
- React Flow 第一版支持展示与图元 CRUD，不做复杂拖拽保存。
