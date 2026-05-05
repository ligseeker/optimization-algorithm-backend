# Agent Team 角色定义

## 协作总则

- 主控 Agent 负责排程、审批、边界管控与冲突仲裁
- 任一时刻同一文件只能由一个 Agent 修改
- Agent 不得越权修改未授权目录
- 每个 Agent 交付后必须同步更新进度、问题、验证记录

## 1. 主控 Agent

- 职责：拆解任务、分配角色、控制节奏、审核交付、维护 backlog / progress / issues / decision log
- 允许修改的目录：`docs/agent-team/**`
- 禁止修改的目录：`src/**`、`frontend/**`、`docs/frontend/**`，除非进入对应阶段并明确调度
- 交付物：阶段计划、任务状态更新、阻塞升级、跨 Agent 决策记录
- 验收标准：任务顺序清晰、边界不冲突、状态同步及时、无越权修改

## 2. API Agent

- 职责：读取后端控制器、DTO、OpenAPI，整理接口契约、错误码、字段说明与调用示例
- 允许修改的目录：`docs/frontend/API_CONTRACT.md`、`frontend/src/api/**`、`frontend/src/types/**`、`docs/agent-team/**`
- 禁止修改的目录：`frontend/src/pages/**`、`frontend/src/components/**`、`src/main/**`
- 交付物：API 契约文档、Axios 封装、领域 API 模块、类型定义
- 验收标准：优先使用 `/api` 新接口、legacy 仅在 `legacyAlgorithm.ts`、字段命名与后端一致、鉴权与错误处理清晰

## 3. Frontend Scaffold Agent

- 职责：初始化前端工程、基础目录、Vite 配置、通用脚本与质量门禁
- 允许修改的目录：`frontend/**`
- 禁止修改的目录：`src/**`、`docs/agent-team/**`，除非主控要求补充说明
- 交付物：可启动的前端工程、基础依赖、脚本、环境变量模板、目录骨架
- 验收标准：`npm run typecheck`、`npm run lint`、`npm run build` 可执行；结构符合 AGENTS 约束

## 4. Auth/Layout Agent

- 职责：登录页、登出、用户信息获取、路由守卫、应用主布局、导航框架
- 允许修改的目录：`frontend/src/pages/auth/**`、`frontend/src/layouts/**`、`frontend/src/router/**`、`frontend/src/store/**`、`frontend/src/components/app/**`
- 禁止修改的目录：`frontend/src/api/**`、`frontend/src/types/**`、`src/main/**`
- 交付物：登录流程、token 生命周期处理、基础布局、鉴权路由
- 验收标准：登录失效可清理 token 并跳转 `/login`；页面具备 loading / error / empty 处理

## 5. Workspace/Graph List Agent

- 职责：工作空间与流程图列表、增删改查、筛选、分页、空态处理
- 允许修改的目录：`frontend/src/pages/workspaces/**`、`frontend/src/pages/graphs/**`、`frontend/src/components/workspace/**`、`frontend/src/components/graph/**`
- 禁止修改的目录：`frontend/src/api/**`、`frontend/src/types/**`、`src/main/**`
- 交付物：工作空间页、流程图列表页、CRUD 交互、确认弹窗
- 验收标准：列表状态完整、删除二次确认、交互基于 API 层而非页面直写请求

## 6. Graph Editor Agent

- 职责：流程图编辑器、节点/路径/装备/约束 CRUD、画布交互、详情装配
- 允许修改的目录：`frontend/src/pages/graph-editor/**`、`frontend/src/components/graph-editor/**`、`frontend/src/hooks/graph-editor/**`
- 禁止修改的目录：`frontend/src/api/**`、`frontend/src/types/**`、`src/main/**`
- 交付物：基于 React Flow 的编辑器、图元面板、保存与刷新逻辑
- 验收标准：编辑态与展示态职责分离；轮询或订阅资源可正确清理；页面状态完整

## 7. Task/Result Agent

- 职责：优化任务提交、任务中心、状态轮询、失败重试、结果展示、图表渲染
- 允许修改的目录：`frontend/src/pages/tasks/**`、`frontend/src/pages/results/**`、`frontend/src/components/tasks/**`、`frontend/src/components/results/**`
- 禁止修改的目录：`frontend/src/api/**`、`frontend/src/types/**`、`src/main/**`
- 交付物：任务提交入口、任务列表、结果页、ECharts 可视化
- 验收标准：轮询在卸载时停止；重试行为明确；结果页处理空数据与失败态

## 8. Import/Export Agent

- 职责：YAML 导入、校验错误展示、导出下载、兼容旧文件流或文本返回
- 允许修改的目录：`frontend/src/pages/import-export/**`、`frontend/src/components/import-export/**`
- 禁止修改的目录：`frontend/src/api/**`、`frontend/src/types/**`、`src/main/**`
- 交付物：上传导入流程、结构化错误展示、Blob 下载逻辑
- 验收标准：支持 `/api/import/graphs` 和 `/api/export/graphs/{graphId}/yaml`；用户可感知错误详情

## 9. QA Agent

- 职责：编写与修复单元测试、集成测试、E2E 测试，执行质量门禁并回归
- 允许修改的目录：`frontend/src/**/*.test.*`、`frontend/tests/**`、`frontend/playwright/**`、`docs/agent-team/06_VALIDATION_REPORT.md`
- 禁止修改的目录：`src/main/**`
- 交付物：Vitest / Playwright 测试、缺陷复现与修复记录、验证报告
- 验收标准：关键流程可自动化验证；报告记录通过与失败原因

## 10. Review Agent

- 职责：审查代码结构、边界约束、回归风险、缺失测试、文档遗漏
- 允许修改的目录：原则上只读；如主控授权，可修改 `docs/agent-team/**`
- 禁止修改的目录：未经授权不得修改任何业务代码
- 交付物：Review 结论、风险清单、整改建议
- 验收标准：结论明确、问题分级清晰、可直接进入修复循环

## 11. Documentation Agent

- 职责：维护 README、部署文档、联调说明、演示说明、用户操作手册
- 允许修改的目录：`README.md`、`docs/frontend/**`、`docs/agent-team/**`
- 禁止修改的目录：`src/main/**`、未经授权的 `frontend/src/**`
- 交付物：README、部署文档、演示脚本、接口与页面说明
- 验收标准：文档可独立指导开发、部署、联调与演示，且与当前实现一致
