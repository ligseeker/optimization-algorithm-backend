# 问题与阻塞记录

## I-001 后端接口字段仍需契约化确认

- 类型：不确定项
- 现状：已确认接口路径分组，但请求体、响应体、分页结构、错误码字段仍需系统化整理
- 影响：若直接开始前端开发，类型定义可能反复改动
- 责任 Agent：API Agent
- 下一步：执行 `P0-T01`、`P0-T02`
- 状态：`OPEN`

## I-002 登录 token 的前端传递细节待确认

- 类型：不确定项
- 现状：`2026-05-05` 运行态日志确认 Sa-Token 配置为 `isReadHeader=true`、`isReadCookie=true`、`isWriteHeader=false`，且登录接口响应体返回 `token` 与 `tokenName`
- 影响：前端默认可按 Header 方案实现；Cookie 兼容不是主链路阻塞项
- 责任 Agent：API Agent
- 下一步：在真实登录联调时确认失效跳转与登出清理行为
- 状态：`RESOLVED`

## I-003 OpenAPI 可访问性未实测

- 类型：潜在阻塞
- 现状：`2026-05-05` 已通过 `curl http://127.0.0.1:8081/v3/api-docs` 验证可访问
- 影响：OpenAPI 可作为契约辅助来源，但不能直接作为白名单，因为其中混有 legacy 接口
- 责任 Agent：API Agent
- 下一步：在 `P0-T02` 生成契约文档时过滤 legacy 控制器，只保留前端目标接口
- 状态：`RESOLVED`

## I-004 联调依赖 MySQL、Redis 和测试账号

- 类型：潜在阻塞
- 现状：`2026-05-05` 已通过临时 `8082` 启动验证确认应用能够正常连接数据库并完成 Web 容器启动；用户已补充可用测试账号 `admin / admin123`
- 影响：Phase 0 范围内已不存在因测试账号缺失导致的联调阻塞
- 责任 Agent：主控 Agent / API Agent
- 下一步：进入真实登录联调时验证该账号的权限范围与返回数据
- 状态：`RESOLVED`

## I-005 新旧接口并存，存在误接 legacy 的风险

- 类型：架构风险
- 现状：仓库中同时存在 `/api/**` 新控制器与顶层 `Controller/` 包下的大量旧接口；legacy 风险不只限于 `/optimizeByFile` 等 4 个路径
- 影响：若不做边界治理，前端容易把兼容接口接入主流程
- 责任 Agent：主控 Agent / API Agent
- 下一步：已在 `API_CONTRACT.md` 中完成白名单/兼容层分离；后续代码实现阶段继续通过 `legacyAlgorithm.ts` 强制隔离
- 状态：`OPEN`

## I-006 仓库存在其他未合并工作区变更

- 类型：协作风险
- 现状：`git status --short` 显示当前工作树中已有非本轮产生的修改与删除
- 影响：后续开发需要避免覆盖他人变更，并在提交前严格核对范围
- 责任 Agent：主控 Agent
- 下一步：后续每轮开工前检查 diff，必要时隔离分支或明确文件所有权
- 状态：`OPEN`

## I-007 `frontend/` 目录已存在但状态未知

- 类型：不确定项
- 现状：仓库已有 `frontend/` 目录，但本轮未对其内容和可用性做初始化判断
- 影响：Phase 1 需要先判断是接管现有目录还是重新整理结构
- 责任 Agent：Frontend Scaffold Agent
- 下一步：在 `P1-T01` 开始前检查目录内容、脚本和依赖状态
- 状态：`OPEN`

## I-008 前端脚本名称尚未最终固定

- 类型：待决事项
- 现状：backlog 中预置了 `npm run typecheck`、`npm run lint`、`npm run build`、`npm run test`、`npm run test:e2e`
- 影响：Phase 1 初始化时需要保证这些脚本实际存在
- 责任 Agent：Frontend Scaffold Agent
- 下一步：在工程脚手架阶段落实标准脚本
- 状态：`OPEN`

## I-009 旧 API 设计文档与真实控制器存在偏差

- 类型：已识别风险
- 现状：`docs/refactor/03_API设计.md` 中部分路径与真实代码不一致，例如优化任务写成 `/api/optimize-tasks`、YAML 导入写成 `/api/workspaces/{workspaceId}/graphs/import-yaml`、导出写成文件流，而真实控制器分别是 `/api/optimize/tasks`、`/api/import/graphs`、`Result<GraphYamlExportResponse>`
- 影响：如果后续以前者为准，会导致前端 API 封装和类型定义走偏
- 责任 Agent：API Agent
- 下一步：`P0-T02` 编写 `docs/frontend/API_CONTRACT.md` 时必须以 controller + dto/vo 为准，并把设计差异单独注明
- 状态：`OPEN`

## I-010 OpenAPI 运行校验前，token 传递方式只能视为高置信推断

- 类型：不确定项
- 现状：运行态日志已确认后端同时读取 Header 和 Cookie，但不主动写 Header
- 影响：Header 方案已足以作为前端主实现
- 责任 Agent：API Agent
- 下一步：保留登录态过期与登出行为验证
- 状态：`RESOLVED`

## I-011 OpenAPI 暴露了遗留控制器接口，需要人工筛选

- 类型：已识别风险
- 现状：`/v3/api-docs` 除 `/api/**` 新接口外，还包含 `/optimizeByFile`、`/uploadFile` 等历史接口，以及其他旧控制器暴露的非目标路径
- 影响：若直接代码生成或照抄 OpenAPI，前端可能误接 legacy 能力
- 责任 Agent：API Agent
- 下一步：后续开发继续以 `docs/frontend/API_CONTRACT.md` 为唯一白名单来源
- 状态：`RESOLVED`

## I-012 默认测试账号缺失

- 类型：当前阻塞
- 现状：用户已提供测试账号 `username: admin`、`password: admin123`
- 影响：登录、权限路由和后续联调已有可用账号基线
- 责任 Agent：主控 Agent / API Agent
- 下一步：在后续联调阶段实际验证该账号登录与权限边界
- 状态：`RESOLVED`

## I-013 默认端口 8081 已被现有实例占用

- 类型：环境风险
- 现状：用户已确认此前 8081 占用来自手动启动的后端实例，当前占用已解除；后续联调优先使用 `http://127.0.0.1:8081`
- 影响：默认端口不再是当前阻塞项；仅保留为环境注意事项
- 责任 Agent：主控 Agent / API Agent
- 下一步：若后续再遇端口冲突，优先确认是否已有用户手动启动实例
- 状态：`RESOLVED`

## P4-T01 阻塞检查

- 类型：任务检查记录
- 现状：工作空间列表与 CRUD 开发、检查命令和前端构建均已完成；本任务未新增阻塞项
- 影响：可继续执行 `P4-T02`
- 责任 Agent：Workspace/Graph List Agent
- 下一步：进入流程图列表与 CRUD
- 状态：`NO_NEW_ISSUE`

## P4-T02 阻塞检查

- 类型：任务检查记录
- 现状：流程图列表与 CRUD 开发、检查命令和前端构建均已完成；本任务未新增阻塞项
- 影响：可继续执行 `P5-T01`
- 责任 Agent：Workspace/Graph List Agent
- 下一步：进入任务中心
- 状态：`NO_NEW_ISSUE`

## P5-T01 阻塞检查

- 类型：任务检查记录
- 现状：任务中心开发、检查命令和前端构建均已完成；本任务未新增阻塞项
- 影响：可继续执行 `P5-T02`
- 责任 Agent：Task/Result Agent
- 下一步：进入结果页与可视化
- 状态：`NO_NEW_ISSUE`

## P5-T02 阻塞检查

- 类型：任务检查记录
- 现状：结果页与可视化开发、检查命令和前端构建均已完成；本任务未新增阻塞项
- 影响：可继续执行 `P6-T01`
- 责任 Agent：Task/Result Agent
- 下一步：进入 YAML 导入
- 状态：`NO_NEW_ISSUE`

## P6-T01 阻塞检查

- 类型：任务检查记录
- 现状：YAML 导入开发、检查命令和前端构建均已完成；本任务未新增阻塞项
- 影响：可继续执行 `P6-T02`
- 责任 Agent：Import/Export Agent
- 下一步：进入 YAML 导出
- 状态：`NO_NEW_ISSUE`

## P6-T02 阻塞检查

- 类型：任务检查记录
- 现状：YAML 导出开发、检查命令和前端构建均已完成；本任务未新增阻塞项
- 影响：可继续执行 `P7-T01`
- 责任 Agent：Import/Export Agent
- 下一步：进入流程图编辑器基础框架
- 状态：`NO_NEW_ISSUE`

## P7-T01 阻塞检查

- 类型：任务检查记录
- 现状：流程图编辑器基础框架开发、检查命令和前端构建均已完成；本任务未新增阻塞项
- 影响：可继续执行 `P7-T02`
- 责任 Agent：Graph Editor Agent
- 下一步：进入图元 CRUD 与编辑交互
- 状态：`NO_NEW_ISSUE`

## P7-T02 阻塞检查

- 类型：任务检查记录
- 现状：图元 CRUD 与编辑交互开发、检查命令和前端构建均已完成；本任务未新增阻塞项
- 影响：可继续执行 `P8-T01`
- 责任 Agent：Graph Editor Agent
- 下一步：进入联调环境与脚本说明
- 状态：`NO_NEW_ISSUE`
