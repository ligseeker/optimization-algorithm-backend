# Backend Change Requests

## BCR-001 YAML 导出结果无法再次导入

- BCR ID：`BCR-001`
- 发现任务 ID：`P8-T02`
- 影响页面或模块：YAML 导入导出、流程图列表导入/导出按钮
- 接口路径：`/api/export/graphs/{graphId}/yaml`、`/api/import/graphs`
- 请求方法：`GET`、`POST`
- 请求参数：先导出 `graphId=9`，再以 multipart 导入导出的 YAML；导入字段包含 `file`、`workspaceId=4`、`graphName`
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

- 预期响应：后端导出的 YAML 应能够被当前导入接口再次导入，或导出接口应保证字段值满足导入校验规则
- 问题描述：联调中先通过新接口创建流程图、节点、路径、装备、约束，再调用导出接口得到 YAML。将该 YAML 原样导入时失败，说明导入和导出之间存在 round-trip 契约不一致。
- 严重级别：`P1`
- 是否阻塞当前任务：否
- 是否存在前端 workaround：部分存在。前端已将节点精度表单限制在 `0-1`；但 `conditionType=NORMAL` 与导入校验不一致无法由前端可靠修复，因为导出内容由后端生成。
- 建议后端修改方案：统一 YAML 导入校验和导出枚举映射；若导入只允许有限枚举，则导出时不要生成非法 `conditionType=NORMAL`；同时在节点创建/更新接口层补充 `precisionValue` 范围校验或明确契约。
- 是否需要用户授权修改后端：否。`2026-05-05` 用户已明确授权 Backend Patch Agent 做最小范围修复。
- 当前状态：`RESOLVED`
- 实际修复落点：
  - `CreateNodeRequest` / `UpdateNodeRequest`：补充 `precisionValue <= 1` 校验
  - `NodeAppServiceImpl`：增加服务层精度范围校验，防止绕过控制器校验进入数据层
  - `ConstraintAppServiceImpl`：增加 `conditionType` 规范化与白名单校验
  - `ConstraintTypeSupport`：提取共享约束类型枚举支持，供约束服务与 YAML 导入校验复用
  - `ProcessMapConverter`：导出遇到历史非法约束类型时明确失败，不再兜底映射为伪语义
  - `GraphYamlServiceImpl`：导入校验改用共享约束类型规则
- 验证结果：
  - `mvn -q -DskipTests compile` 通过
  - `mvn -q "-Dtest=GraphYamlServiceImplTest,NodePathConstraintCrudTest" test` 通过
  - 在 `http://127.0.0.1:8083` 上完成真实 API round-trip 复测：
    - 非法节点精度被拒绝
    - 约束类型 `" follow "` 被规范化为 `FOLLOW`
    - 非法约束类型 `NORMAL` 被拒绝
    - YAML 导出后可被 `/api/import/graphs` 原样重新导入
