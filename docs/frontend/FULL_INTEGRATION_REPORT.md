# P8-T02 全链路联调报告

## 联调环境

- 日期：2026-05-05
- 后端地址：`http://127.0.0.1:8081`
- 测试账号：`admin / admin123`
- 登录态请求头：`satoken: <token>`
- 执行方式：PowerShell API 自动化脚本 + 前端质量门禁 + Playwright smoke

## 通过项

| 步骤 | 结果 | 证据摘要 |
| --- | --- | --- |
| 登录 | 通过 | `admin` 登录成功，`tokenName=satoken` |
| 获取当前用户 | 通过 | `/api/auth/me` 返回 `ADMIN` 用户 |
| 查询工作空间 | 通过 | 分页接口返回 `total=3` |
| 创建工作空间 | 通过 | 创建 `workspaceId=4` |
| 查询流程图列表 | 通过 | 当前工作空间流程图 `total=1` |
| 创建流程图 | 通过 | 创建 `graphId=9` |
| 新增装备 | 通过 | 创建 `equipmentId=22` |
| 新增节点 | 通过 | 创建 `nodeId=448`、`nodeId=449` |
| 新增路径 | 通过 | 创建 `pathId=505` |
| 新增约束 | 通过 | 创建 `constraintId=555` |
| 查询流程图详情 | 通过 | 返回 2 nodes / 1 path / 1 equipment / 1 constraint |
| 提交优化任务 | 通过 | 创建 `taskId=14`，初始状态 `PENDING` |
| 查询任务状态 | 通过 | 轮询后状态 `SUCCESS` |
| 查询优化结果 | 通过 | 返回 `resultId=13` |
| YAML 导出 | 通过 | 返回 `graph-9.yaml`，`yamlContent.length=491` |
| 退出登录 | 通过 | `/api/auth/logout` 返回成功 |

## 失败项

### YAML 导入

- 接口：`POST /api/import/graphs`
- 请求方式：`multipart/form-data`
- 字段：`file`、`workspaceId=4`、`graphName=Codex Imported Retry ...`
- 实际响应：

```json
{
  "code": 700001,
  "message": "YAML导入校验失败",
  "data": {
    "totalErrors": 2,
    "errors": [
      {
        "code": "PRECISION_INVALID",
        "location": "ProcessNodes[1].precision",
        "message": "precision必须在0到1之间"
      },
      {
        "code": "CONSTRAINT_TYPE_INVALID",
        "location": "ConstraintConditions[0].conditionType",
        "message": "约束类型非法: NORMAL"
      }
    ]
  }
}
```

## 归属判断

- `PRECISION_INVALID`：前端可做保守兼容，节点精度输入限制为 `0-1`，避免 UI 继续产生不符合 YAML 导入校验的数据。
- `CONSTRAINT_TYPE_INVALID`：后端导出 YAML 中的约束类型与导入校验允许值不一致，属于后端导入/导出 round-trip 契约问题，已记录到 `docs/agent-team/07_BACKEND_CHANGE_REQUESTS.md`。

## 前端修复动作

- 将图元编辑器节点表单的 `precisionValue` 限制为 `0-1`。
- 将 Playwright smoke 调整为验证 `/login` 页面，避免未登录访问 `/` 时与路由守卫预期冲突。

## 结论

除 YAML 导出后再导入的 round-trip 问题外，核心前后端链路已可运行。该 YAML 问题不阻塞前端继续开发，但需要后端确认导入校验和导出枚举映射。
