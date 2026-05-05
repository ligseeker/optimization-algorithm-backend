# 进度看板

## 当前阶段

- 阶段名称：`Phase 1 / Frontend Scaffold`
- 阶段目标：在现有 `frontend/` 目录基础上建立可运行的前端工程骨架与基础设施
- 当前状态：`READY`

## 已完成任务

- `CTRL-001` 建立 `docs/agent-team/` 控制台文件
- 已读取根目录 `AGENTS.md` 并纳入执行约束
- 已确认后端基础技术栈、OpenAPI 地址、默认端口与 `/api` 新接口分组
- `P0-T01` 已完成：核对真实控制器、DTO/VO、OpenAPI 与旧设计稿差异，确认应以代码契约为准
- `P0-T02` 已完成：输出 `docs/frontend/API_CONTRACT.md`，建立前端目标接口白名单与 legacy 兼容接口清单
- `P0-T03` 已完成：确认 OpenAPI / Swagger 可访问、应用可启动、Sa-Token 读取策略与联调前置阻塞项
- `P1-T01` 已完成：在现有空 `frontend/` 目录中建立 Vite + React 18 + TypeScript 骨架，并通过安装、类型检查、Lint 与构建验证

## 正在执行任务

- `P1-T02` 建立工程基础设施
- 当前执行 Agent：`Frontend Scaffold Agent`

## 下一步任务

- `P2-T01` 建立 HTTP Client 与鉴权拦截
- `P2-T02` 建立领域类型定义
- 当前联调基线：测试账号 `admin / admin123`，默认后端地址 `http://127.0.0.1:8081`

## 近期里程碑

- 里程碑 M0：接口契约与联调前置信息确认完成
- 里程碑 M1：前端工程初始化完成
- 里程碑 M2：认证、布局、工作空间、流程图列表完成
- 里程碑 M3：任务中心、结果页、YAML 导入导出完成
- 里程碑 M4：流程图编辑器完成
- 里程碑 M5：联调、QA、文档收口完成

## 主控说明

- `P0-T01` 检查结果：`mvn -q -DskipTests compile` 通过，`curl http://127.0.0.1:8081/v3/api-docs` 可访问
- 已确认 OpenAPI 混有 legacy 接口，后续契约文档必须人工过滤
- `P0-T02` 检查结果：`curl http://127.0.0.1:8081/v3/api-docs` 通过，`API_CONTRACT` 已与运行态接口对齐
- `P0-T03` 检查结果：原始 `mvn spring-boot:run` 因 `8081` 被现有实例占用而失败，经最小修复改用临时 `8082` 启动验证成功；`Swagger UI` 可访问
- 用户已补充测试账号，且 8081 占用已解除，Phase 0 的外部阻塞已清除
- `P1-T01` 检查结果：`npm install`、`npm run typecheck`、`npm run lint`、`npm run build` 全部通过
