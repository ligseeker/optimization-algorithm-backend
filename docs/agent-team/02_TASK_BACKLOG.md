# 前端开发任务队列

## 状态说明

- `TODO`：未开始
- `IN_PROGRESS`：正在执行
- `DONE`：已完成
- `BLOCKED`：存在外部依赖或关键信息缺失

## 任务清单

### CTRL-001 建立 Agent Team 工作流控制台

- Task ID：`CTRL-001`
- 任务名称：建立 `docs/agent-team/` 控制台文件
- 负责 Agent：主控 Agent
- 前置依赖：无
- 允许修改文件范围：`docs/agent-team/**`
- 禁止修改文件范围：`frontend/**`、`src/**`
- 验收标准：7 个控制台文件创建完成，覆盖角色、阶段、进度、决策、问题、验证维度
- 需要运行的检查命令：`git status --short`
- 状态：`DONE`

### P0-T01 读取后端控制器与现有文档

- Task ID：`P0-T01`
- 任务名称：盘点后端模块、接口分组、联调端点与旧接口
- 负责 Agent：API Agent
- 前置依赖：`CTRL-001`
- 允许修改文件范围：`docs/frontend/API_CONTRACT.md`、`docs/agent-team/00_PROJECT_CONTEXT.md`、`docs/agent-team/05_ISSUES.md`
- 禁止修改文件范围：`frontend/**`、`src/main/**`
- 验收标准：完成 `/api` 新接口和 legacy 接口边界梳理；标注端口、鉴权、OpenAPI 地址
- 需要运行的检查命令：`mvn -q -DskipTests compile`、`curl http://127.0.0.1:8081/v3/api-docs`
- 状态：`DONE`

### P0-T02 生成前端接口契约文档

- Task ID：`P0-T02`
- 任务名称：输出 `docs/frontend/API_CONTRACT.md`
- 负责 Agent：API Agent
- 前置依赖：`P0-T01`
- 允许修改文件范围：`docs/frontend/API_CONTRACT.md`
- 禁止修改文件范围：`frontend/**`、`src/main/**`
- 验收标准：每个领域接口包含路径、方法、请求类型、响应类型、分页/错误码/鉴权说明；legacy 单列一章
- 需要运行的检查命令：`curl http://127.0.0.1:8081/v3/api-docs`
- 状态：`DONE`

### P0-T03 确认联调前置条件

- Task ID：`P0-T03`
- 任务名称：确认数据库、Redis、测试账号、token 传递方式、OpenAPI 可访问性
- 负责 Agent：API Agent
- 前置依赖：`P0-T01`
- 允许修改文件范围：`docs/agent-team/05_ISSUES.md`、`docs/agent-team/04_DECISION_LOG.md`
- 禁止修改文件范围：`frontend/**`、`src/main/**`
- 验收标准：阻塞项被登记并分级；联调必须条件明确
- 需要运行的检查命令：`mvn spring-boot:run`、`curl http://127.0.0.1:8081/swagger-ui.html`
- 状态：`DONE`

### P1-T01 初始化前端工程骨架

- Task ID：`P1-T01`
- 任务名称：创建 Vite + React + TypeScript 前端工程
- 负责 Agent：Frontend Scaffold Agent
- 前置依赖：`P0-T02`
- 允许修改文件范围：`frontend/**`
- 禁止修改文件范围：`src/**`、`docs/agent-team/**`
- 验收标准：目录结构符合 AGENTS 规则，包含 `src/api`、`src/types`、`src/pages`、`src/components`
- 需要运行的检查命令：`npm install`、`npm run typecheck`、`npm run lint`、`npm run build`
- 状态：`DONE`

### P1-T02 建立工程基础设施

- Task ID：`P1-T02`
- 任务名称：配置路由、查询库、状态管理、UI 框架、环境变量模板与测试脚本
- 负责 Agent：Frontend Scaffold Agent
- 前置依赖：`P1-T01`
- 允许修改文件范围：`frontend/**`
- 禁止修改文件范围：`src/**`
- 验收标准：基础依赖安装完毕；`VITE_API_BASE_URL` 生效；脚本可运行
- 需要运行的检查命令：`npm run typecheck`、`npm run lint`、`npm run build`
- 状态：`DONE`

### P2-T01 建立 HTTP Client 与鉴权拦截

- Task ID：`P2-T01`
- 任务名称：实现 Axios 实例、请求响应拦截、登录失效处理
- 负责 Agent：API Agent
- 前置依赖：`P1-T02`、`P0-T02`
- 允许修改文件范围：`frontend/src/api/**`、`frontend/src/types/**`
- 禁止修改文件范围：`frontend/src/pages/**`、`src/**`
- 验收标准：所有请求集中封装；401/鉴权失败能统一清理 token 并触发登录跳转
- 需要运行的检查命令：`npm run typecheck`、`npm run lint`、`npm run build`
- 状态：`DONE`

### P2-T02 建立领域类型定义

- Task ID：`P2-T02`
- 任务名称：为 auth、workspace、graph、node、path、equipment、constraint、task、result、yaml 建立类型
- 负责 Agent：API Agent
- 前置依赖：`P0-T02`
- 允许修改文件范围：`frontend/src/types/**`
- 禁止修改文件范围：`frontend/src/pages/**`、`src/**`
- 验收标准：类型命名清晰；请求/响应/分页/错误结构完整；与契约文档一致
- 需要运行的检查命令：`npm run typecheck`、`npm run lint`
- 状态：`DONE`

### P2-T03 建立领域 API 模块

- Task ID：`P2-T03`
- 任务名称：实现 API 模块与 `legacyAlgorithm.ts`
- 负责 Agent：API Agent
- 前置依赖：`P2-T01`、`P2-T02`
- 允许修改文件范围：`frontend/src/api/**`
- 禁止修改文件范围：`frontend/src/pages/**`、`src/**`
- 验收标准：新接口按领域拆分；legacy 仅在 `legacyAlgorithm.ts`；页面无需直接写 axios
- 需要运行的检查命令：`npm run typecheck`、`npm run lint`、`npm run build`
- 状态：`DONE`

### P3-T01 实现认证与会话状态

- Task ID：`P3-T01`
- 任务名称：实现登录、登出、当前用户、token 存储与恢复
- 负责 Agent：Auth/Layout Agent
- 前置依赖：`P2-T03`
- 允许修改文件范围：`frontend/src/pages/auth/**`、`frontend/src/store/**`、`frontend/src/hooks/**`
- 禁止修改文件范围：`frontend/src/api/**`、`src/**`
- 验收标准：登录成功后进入系统；失效后自动清理并跳转；表单具备前端校验
- 需要运行的检查命令：`npm run typecheck`、`npm run lint`、`npm run build`、`npm run test`
- 状态：`DONE`

### P3-T02 实现布局与路由守卫

- Task ID：`P3-T02`
- 任务名称：实现应用布局、菜单、面包屑、权限路由、基础异常页
- 负责 Agent：Auth/Layout Agent
- 前置依赖：`P3-T01`
- 允许修改文件范围：`frontend/src/layouts/**`、`frontend/src/router/**`、`frontend/src/components/app/**`
- 禁止修改文件范围：`frontend/src/api/**`、`src/**`
- 验收标准：未登录不可进入业务页；已登录可访问受保护路由；空态与错误态有统一承载
- 需要运行的检查命令：`npm run typecheck`、`npm run lint`、`npm run build`
- 状态：`DONE`

### P4-T01 实现工作空间列表与 CRUD

- Task ID：`P4-T01`
- 任务名称：实现工作空间列表、创建、编辑、删除
- 负责 Agent：Workspace/Graph List Agent
- 前置依赖：`P3-T02`
- 允许修改文件范围：`frontend/src/pages/workspaces/**`、`frontend/src/components/workspace/**`
- 禁止修改文件范围：`frontend/src/api/**`、`src/**`
- 验收标准：列表支持 loading / error / empty；删除具备二次确认；表单校验完整
- 需要运行的检查命令：`npm run typecheck`、`npm run lint`、`npm run build`、`npm run test`
- 状态：`DONE`

### P4-T02 实现流程图列表与 CRUD

- Task ID：`P4-T02`
- 任务名称：实现工作空间下流程图列表、创建、编辑、删除、详情入口
- 负责 Agent：Workspace/Graph List Agent
- 前置依赖：`P4-T01`
- 允许修改文件范围：`frontend/src/pages/graphs/**`、`frontend/src/components/graph/**`
- 禁止修改文件范围：`frontend/src/api/**`、`src/**`
- 验收标准：可在工作空间维度管理流程图；删除二次确认；详情可跳转编辑器和结果页
- 需要运行的检查命令：`npm run typecheck`、`npm run lint`、`npm run build`、`npm run test`
- 状态：`DONE`

### P5-T01 实现任务中心

- Task ID：`P5-T01`
- 任务名称：实现优化任务提交、任务列表、状态轮询与失败重试
- 负责 Agent：Task/Result Agent
- 前置依赖：`P4-T02`
- 允许修改文件范围：`frontend/src/pages/tasks/**`、`frontend/src/components/tasks/**`
- 禁止修改文件范围：`frontend/src/api/**`、`src/**`
- 验收标准：任务状态可轮询；页面卸载时停止轮询；失败任务可重试并提示结果
- 需要运行的检查命令：`npm run typecheck`、`npm run lint`、`npm run build`、`npm run test`
- 状态：`DONE`

### P5-T02 实现结果页与可视化

- Task ID：`P5-T02`
- 任务名称：实现优化结果详情、Diff / mapCode 展示与 ECharts 图表
- 负责 Agent：Task/Result Agent
- 前置依赖：`P5-T01`
- 允许修改文件范围：`frontend/src/pages/results/**`、`frontend/src/components/results/**`
- 禁止修改文件范围：`frontend/src/api/**`、`src/**`
- 验收标准：成功、失败、空结果均可展示；结果结构与后端保持一致
- 需要运行的检查命令：`npm run typecheck`、`npm run lint`、`npm run build`、`npm run test`
- 状态：`DONE`

### P6-T01 实现 YAML 导入

- Task ID：`P6-T01`
- 任务名称：实现 YAML 文件上传、导入结果反馈、结构化错误展示
- 负责 Agent：Import/Export Agent
- 前置依赖：`P4-T02`
- 允许修改文件范围：`frontend/src/pages/import-export/**`、`frontend/src/components/import-export/**`
- 禁止修改文件范围：`frontend/src/api/**`、`src/**`
- 验收标准：支持 multipart 上传；错误报告可视化；页面状态完整
- 需要运行的检查命令：`npm run typecheck`、`npm run lint`、`npm run build`、`npm run test`
- 状态：`DONE`

### P6-T02 实现 YAML 导出

- Task ID：`P6-T02`
- 任务名称：实现 YAML 文本转 Blob 下载与导出交互
- 负责 Agent：Import/Export Agent
- 前置依赖：`P6-T01`
- 允许修改文件范围：`frontend/src/pages/import-export/**`、`frontend/src/components/import-export/**`
- 禁止修改文件范围：`frontend/src/api/**`、`src/**`
- 验收标准：可从图详情或列表触发导出；下载文件内容正确；异常有提示
- 需要运行的检查命令：`npm run typecheck`、`npm run lint`、`npm run build`、`npm run test`
- 状态：`DONE`

### P7-T01 实现流程图编辑器基础框架

- Task ID：`P7-T01`
- 任务名称：实现 React Flow 画布、图详情装配、基础工具栏与侧栏
- 负责 Agent：Graph Editor Agent
- 前置依赖：`P4-T02`、`P2-T03`
- 允许修改文件范围：`frontend/src/pages/graph-editor/**`、`frontend/src/components/graph-editor/**`、`frontend/src/hooks/graph-editor/**`
- 禁止修改文件范围：`frontend/src/api/**`、`src/**`
- 验收标准：能加载 `graph detail` 数据并映射为画布；页面状态完整；结构可扩展
- 需要运行的检查命令：`npm run typecheck`、`npm run lint`、`npm run build`、`npm run test`
- 状态：`DONE`

### P7-T02 实现图元 CRUD 与编辑交互

- Task ID：`P7-T02`
- 任务名称：实现节点、路径、装备、约束的增删改查与表单交互
- 负责 Agent：Graph Editor Agent
- 前置依赖：`P7-T01`
- 允许修改文件范围：`frontend/src/pages/graph-editor/**`、`frontend/src/components/graph-editor/**`
- 禁止修改文件范围：`frontend/src/api/**`、`src/**`
- 验收标准：所有图元操作通过 API 层完成；删除二次确认；表单校验完整
- 需要运行的检查命令：`npm run typecheck`、`npm run lint`、`npm run build`、`npm run test`
- 状态：`DONE`

### P8-T01 建立联调环境与脚本

- Task ID：`P8-T01`
- 任务名称：打通本地联调配置、测试账号说明、环境变量和联调步骤
- 负责 Agent：Documentation Agent
- 前置依赖：`P3-T02`、`P4-T02`、`P5-T02`
- 允许修改文件范围：`README.md`、`docs/frontend/**`、`docs/agent-team/**`
- 禁止修改文件范围：`src/main/**`
- 验收标准：新成员可按文档启动前后端并完成基本联调
- 需要运行的检查命令：`mvn spring-boot:run`、`npm run dev`
- 状态：`IN_PROGRESS`

### P8-T02 执行前后端全链路联调

- Task ID：`P8-T02`
- 任务名称：验证登录、工作空间、流程图、任务、结果、导入导出全流程
- 负责 Agent：QA Agent
- 前置依赖：`P8-T01`、`P6-T02`、`P7-T02`
- 允许修改文件范围：`frontend/**`、`docs/agent-team/05_ISSUES.md`、`docs/agent-team/06_VALIDATION_REPORT.md`
- 禁止修改文件范围：`src/main/**`，除非用户单独授权修后端缺陷
- 验收标准：主流程可端到端走通；联调问题有结论、复现方式和归属
- 需要运行的检查命令：`npm run typecheck`、`npm run lint`、`npm run build`、`npm run test`、`npm run test:e2e`
- 状态：`TODO`

### P9-T01 QA 自动修复循环

- Task ID：`P9-T01`
- 任务名称：根据测试与联调问题进行缺陷修复、回归验证与风险收敛
- 负责 Agent：QA Agent
- 前置依赖：`P8-T02`
- 允许修改文件范围：`frontend/**`、`docs/agent-team/06_VALIDATION_REPORT.md`
- 禁止修改文件范围：`src/main/**`，除非主控升级范围
- 验收标准：关键缺陷关闭；回归报告完整；三大质量门禁持续通过
- 需要运行的检查命令：`npm run typecheck`、`npm run lint`、`npm run build`、`npm run test`、`npm run test:e2e`
- 状态：`TODO`

### P9-T02 执行 Review Gate

- Task ID：`P9-T02`
- 任务名称：对结构、边界、测试覆盖和风险进行正式审查
- 负责 Agent：Review Agent
- 前置依赖：`P9-T01`
- 允许修改文件范围：`docs/agent-team/**`
- 禁止修改文件范围：未经授权不得修改业务代码
- 验收标准：形成明确 review 结论；阻塞问题必须关闭或被接受
- 需要运行的检查命令：`git diff --stat`、`npm run typecheck`、`npm run lint`、`npm run build`
- 状态：`TODO`

### P10-T01 完成 README、部署文档与演示说明

- Task ID：`P10-T01`
- 任务名称：沉淀最终开发、部署、联调、演示文档
- 负责 Agent：Documentation Agent
- 前置依赖：`P9-T02`
- 允许修改文件范围：`README.md`、`docs/frontend/**`、`docs/agent-team/**`
- 禁止修改文件范围：`src/main/**`
- 验收标准：文档覆盖开发环境、部署方式、联调方法、演示流程、已知限制
- 需要运行的检查命令：`npm run typecheck`、`npm run lint`、`npm run build`
- 状态：`TODO`

## 推荐执行顺序

1. `P0-T01`
2. `P0-T02`
3. `P0-T03`
4. `P1-T01`
5. `P1-T02`
6. `P2-T01`
7. `P2-T02`
8. `P2-T03`
9. `P3-T01`
10. `P3-T02`
11. `P4-T01`
12. `P4-T02`
13. `P5-T01`
14. `P5-T02`
15. `P6-T01`
16. `P6-T02`
17. `P7-T01`
18. `P7-T02`
19. `P8-T01`
20. `P8-T02`
21. `P9-T01`
22. `P9-T02`
23. `P10-T01`

## 当前建议

- 当前建议从 `P0-T01` 开始
- 原因：必须先读取后端、核对 OpenAPI 与 token 传递方式，再启动前端工程，避免脚手架后大面积返工
