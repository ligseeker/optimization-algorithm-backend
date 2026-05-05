# 前端演示流程

## 演示准备

- 后端：`http://127.0.0.1:8081`
- 前端：`npm run dev`
- 测试账号：`admin / admin123`
- 浏览器入口：前端开发服务地址，例如 `http://127.0.0.1:7777` 或 Vite 输出地址

## 推荐演示路径

1. 登录系统
   - 打开 `/login`
   - 使用 `admin / admin123`
   - 登录成功后进入 `/dashboard`

2. 工作空间管理
   - 进入 `/workspaces`
   - 展示分页、搜索、空态/加载态
   - 新建工作空间
   - 编辑工作空间描述
   - 删除前展示二次确认

3. 流程图管理
   - 从工作空间进入 `/workspaces/:workspaceId/graphs`
   - 新建流程图
   - 展示流程图基础字段：来源、状态、版本、总时间、总精度、总成本
   - 进入详情页或编辑器

4. YAML 导入导出
   - 在流程图列表点击 `导入 YAML`
   - 上传 `.yaml / .yml` 文件
   - 查看导入成功统计或结构化错误
   - 点击单行 `导出`，确认下载 YAML 文件

5. 流程图编辑器
   - 进入 `/graphs/:graphId/editor`
   - 展示左侧资源统计、中间 React Flow 画布、右侧属性面板
   - 新增节点、路径、装备、约束
   - 点击节点或路径查看属性
   - 删除图元时确认二次弹窗

6. 任务中心
   - 进入 `/tasks`
   - 使用 workspaceId / graphId / taskStatus 筛选
   - 提交优化任务
   - 查看 `/tasks/:taskId`
   - 运行中任务会自动刷新状态

7. 结果页
   - 成功任务点击 `查看结果`
   - 展示优化前后指标、ECharts 对比图
   - 展示 `resultGraph`、`diff`、`mapCode`

8. 退出登录
   - 点击顶部退出
   - 确认回到 `/login`

## 当前已知演示限制

- YAML 导出后原样再次导入存在后端 round-trip 问题，详见 `docs/agent-team/07_BACKEND_CHANGE_REQUESTS.md` 的 `BCR-001`。
- `resultGraph` 和 `diff` 当前按未知结构安全展示为 JSON，不做字段编造。
- React Flow 第一版支持展示和图元 CRUD，不支持拖拽后保存布局。
