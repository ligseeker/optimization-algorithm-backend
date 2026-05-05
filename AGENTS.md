# AGENTS.md

## 项目背景

本项目是 optimization-algorithm-backend 的前端系统。后端是流程优化任务管理系统，支持登录认证、工作空间管理、流程图管理、节点/路径/装备/约束 CRUD、YAML 导入导出、异步优化任务、任务状态查询、优化结果查询和操作日志。

## 前端技术栈

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

## 通用开发规则

1. 所有接口调用必须放在 `frontend/src/api`。
2. 所有接口类型必须放在 `frontend/src/types`。
3. 页面组件放在 `frontend/src/pages`。
4. 可复用组件放在 `frontend/src/components`。
5. 不允许在页面中直接写 axios 请求。
6. 不允许硬编码后端地址，必须使用 `VITE_API_BASE_URL`。
7. 所有页面必须处理 loading、error、empty 三种状态。
8. 所有表单必须做前端校验。
9. 所有删除操作必须二次确认。
10. 所有异步任务轮询必须在页面卸载时停止。
11. 登录失效时必须清理 token 并跳转 `/login`。
12. 修改代码后必须运行：
    - npm run typecheck
    - npm run lint
    - npm run build
13. 修改接口相关代码后必须同步更新 `docs/frontend/API_CONTRACT.md`。
14. legacy 接口只能放入 `legacyAlgorithm.ts`，不能污染新业务代码。
15. 不允许多个 agent 同时修改同一个文件，除非主控 Agent 明确允许。

## 后端接口原则

优先使用 `/api` 前缀的新接口。旧接口如 `/optimizeByFile`、`/optimizeByInput`、`/uploadFile`、`/downloadFile` 只作为兼容接口处理。


## Git 工作规则

1. 每完成一个独立 Task ID，并且检查命令通过后，可以创建一次 git commit。
2. 每次 commit 前必须运行该任务要求的检查命令，例如：
   - npm run typecheck
   - npm run lint
   - npm run build
   - npm run test
3. commit message 使用以下格式：
   - feat(frontend): T01-01 initialize frontend scaffold
   - feat(api): T02-01 implement api adapter
   - fix(frontend): Txx fix build errors
   - docs(agent): Txx update progress
4. commit 后必须运行 `git status`，确认工作区状态。
5. 不要 amend 历史 commit。
6. 不要 force push。
7. 不要自动 push 到远程仓库，除非用户明确要求。
8. 如果检查命令失败，不允许 commit。
9. 如果当前任务被标记为 BLOCKED，不允许 commit。
10. 如果出现大量无关文件变更，必须停止并记录到 `docs/agent-team/05_ISSUES.md`。

## 提交要求

每个 agent 完成任务后必须输出：

1. 修改了哪些文件；
2. 实现了哪些功能；
3. 运行了哪些检查命令；
4. 是否通过；
5. 遗留问题；
6. 下一步建议。
