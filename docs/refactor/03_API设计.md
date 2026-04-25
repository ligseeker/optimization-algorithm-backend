# 03_API设计

## 1. 结论

基于当前项目现状，现有 API 设计文档需要修改，原因如下：

- 需要与第一版数据库范围保持一致；
- 需要统一采用 `/api` 前缀和 `Result<T>` 返回结构；
- 需要覆盖工作空间、流程图、节点、路径、装备、约束、YAML 导入导出、优化任务、结果查询、失败重试；
- 需要明确 Sa-Token 鉴权、Redis 缓存和 ThreadPoolTaskExecutor 异步任务的接口边界。

本版只做第一版 API 设计，不覆盖：

- 复杂 RBAC 管理接口；
- 消息队列相关接口；
- 完整图版本与完整图 Diff 接口。

## 2. 通用规范

### 2.1 基础前缀

所有新接口统一使用：

```text
/api
```

### 2.2 鉴权方式

除登录接口外，默认要求登录。

Sa-Token 第一版约定请求头：

```http
satoken: <token>
```

### 2.3 统一响应结构

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

错误示例：

```json
{
  "code": 404001,
  "message": "流程图不存在",
  "data": null
}
```

### 2.4 通用错误码

| code | 含义 |
|------|------|
| 0 | 成功 |
| 400001 | 参数错误 |
| 401001 | 未登录 |
| 403001 | 无权限 |
| 404001 | 资源不存在 |
| 409001 | 数据冲突 |
| 500001 | 系统异常 |
| 600001 | 任务执行失败 |
| 700001 | YAML 导入导出失败 |

### 2.5 分页响应约定

列表接口统一使用：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [],
    "pageNo": 1,
    "pageSize": 10,
    "total": 0
  }
}
```

## 3. 认证接口

### 3.1 登录

`POST /api/auth/login`

请求示例：

```json
{
  "username": "admin",
  "password": "123456"
}
```

响应示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "userId": 1,
    "username": "admin",
    "nickname": "系统管理员",
    "roleCode": "ADMIN",
    "token": "xxxxxxxx",
    "tokenName": "satoken"
  }
}
```

### 3.2 退出登录

`POST /api/auth/logout`

响应示例：

```json
{
  "code": 0,
  "message": "退出成功",
  "data": true
}
```

### 3.3 当前用户

`GET /api/auth/me`

响应示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "userId": 1,
    "username": "admin",
    "nickname": "系统管理员",
    "roleCode": "ADMIN"
  }
}
```

## 4. 工作空间接口

### 4.1 创建工作空间

`POST /api/workspaces`

请求示例：

```json
{
  "name": "默认工作空间",
  "description": "管理员默认工作空间"
}
```

响应示例：

```json
{
  "code": 0,
  "message": "创建成功",
  "data": {
    "id": 1,
    "name": "默认工作空间",
    "description": "管理员默认工作空间",
    "ownerUserId": 1,
    "status": 1
  }
}
```

### 4.2 工作空间列表

`GET /api/workspaces?pageNo=1&pageSize=10&keyword=默认`

响应示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "默认工作空间",
        "description": "管理员默认工作空间",
        "ownerUserId": 1,
        "status": 1,
        "createdAt": "2026-04-25 18:00:00"
      }
    ],
    "pageNo": 1,
    "pageSize": 10,
    "total": 1
  }
}
```

### 4.3 工作空间详情

`GET /api/workspaces/{workspaceId}`

### 4.4 修改工作空间

`PUT /api/workspaces/{workspaceId}`

请求示例：

```json
{
  "name": "研发空间",
  "description": "新的说明",
  "status": 1
}
```

### 4.5 删除工作空间

`DELETE /api/workspaces/{workspaceId}`

响应示例：

```json
{
  "code": 0,
  "message": "删除成功",
  "data": true
}
```

## 5. 流程图接口

### 5.1 创建流程图

`POST /api/workspaces/{workspaceId}/graphs`

请求示例：

```json
{
  "name": "测试流程图",
  "description": "导入前手工创建",
  "sourceType": "MANUAL"
}
```

响应示例：

```json
{
  "code": 0,
  "message": "创建成功",
  "data": {
    "id": 1001,
    "workspaceId": 1,
    "name": "测试流程图",
    "sourceType": "MANUAL",
    "graphStatus": "DRAFT"
  }
}
```

### 5.2 流程图列表

`GET /api/workspaces/{workspaceId}/graphs?pageNo=1&pageSize=10&keyword=测试`

### 5.3 流程图详情

`GET /api/graphs/{graphId}`

响应示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "graph": {
      "id": 1001,
      "workspaceId": 1,
      "name": "测试流程图",
      "description": "导入前手工创建",
      "sourceType": "MANUAL",
      "totalTime": 120,
      "totalPrecision": 0.8100,
      "totalCost": 300
    },
    "nodes": [],
    "paths": [],
    "equipments": [],
    "constraints": []
  }
}
```

缓存说明：

- 详情接口可使用 Redis 缓存：
  - `graph:detail:{graphId}`

### 5.4 修改流程图

`PUT /api/graphs/{graphId}`

请求示例：

```json
{
  "name": "测试流程图-已整理",
  "description": "补充了约束和装备"
}
```

### 5.5 删除流程图

`DELETE /api/graphs/{graphId}`

## 6. 节点接口

### 6.1 新增节点

`POST /api/graphs/{graphId}/nodes`

请求示例：

```json
{
  "nodeCode": "Ab",
  "nodeName": "工序A",
  "nodeDescription": "原始节点A",
  "equipmentId": 2001,
  "timeCost": 10,
  "precisionValue": 0.9500,
  "costValue": 20,
  "sortNo": 1
}
```

响应示例：

```json
{
  "code": 0,
  "message": "新增成功",
  "data": {
    "id": 3001,
    "graphId": 1001,
    "nodeCode": "Ab"
  }
}
```

### 6.2 节点列表

`GET /api/graphs/{graphId}/nodes?pageNo=1&pageSize=20&keyword=A`

### 6.3 节点详情

`GET /api/graphs/{graphId}/nodes/{nodeId}`

### 6.4 修改节点

`PUT /api/graphs/{graphId}/nodes/{nodeId}`

请求示例：

```json
{
  "nodeName": "工序A-更新",
  "nodeDescription": "更新描述",
  "equipmentId": 2002,
  "timeCost": 12,
  "precisionValue": 0.9600,
  "costValue": 24,
  "sortNo": 2
}
```

### 6.5 删除节点

`DELETE /api/graphs/{graphId}/nodes/{nodeId}`

说明：

- 删除节点时需要事务内同步删除关联路径和约束。

## 7. 路径接口

### 7.1 新增路径

`POST /api/graphs/{graphId}/paths`

请求示例：

```json
{
  "startNodeId": 3001,
  "endNodeId": 3002,
  "relationType": "NORMAL",
  "remark": "主链路"
}
```

响应示例：

```json
{
  "code": 0,
  "message": "新增成功",
  "data": {
    "id": 4001,
    "graphId": 1001
  }
}
```

### 7.2 路径列表

`GET /api/graphs/{graphId}/paths`

### 7.3 路径详情

`GET /api/graphs/{graphId}/paths/{pathId}`

### 7.4 修改路径

`PUT /api/graphs/{graphId}/paths/{pathId}`

请求示例：

```json
{
  "startNodeId": 3001,
  "endNodeId": 3003,
  "relationType": "NORMAL",
  "remark": "调整后的链路"
}
```

### 7.5 删除路径

`DELETE /api/graphs/{graphId}/paths/{pathId}`

## 8. 装备接口

### 8.1 新增装备

`POST /api/graphs/{graphId}/equipments`

请求示例：

```json
{
  "name": "equipment1",
  "description": "高精度设备",
  "color": "#1677ff"
}
```

### 8.2 装备列表

`GET /api/graphs/{graphId}/equipments`

### 8.3 装备详情

`GET /api/graphs/{graphId}/equipments/{equipmentId}`

### 8.4 修改装备

`PUT /api/graphs/{graphId}/equipments/{equipmentId}`

请求示例：

```json
{
  "name": "equipment1-new",
  "description": "更新后的说明",
  "color": "#13c2c2"
}
```

### 8.5 删除装备

`DELETE /api/graphs/{graphId}/equipments/{equipmentId}`

说明：

- 删除装备时，节点上的 `equipmentId` 需要清空或做业务校验。

### 8.6 上传装备图片

`POST /api/graphs/{graphId}/equipments/{equipmentId}/image`

请求：

- `multipart/form-data`
- 字段名：`file`

响应示例：

```json
{
  "code": 0,
  "message": "上传成功",
  "data": {
    "equipmentId": 2001,
    "imagePath": "equipment/20260425/abc.png"
  }
}
```

## 9. 约束接口

### 9.1 新增约束

`POST /api/graphs/{graphId}/constraints`

请求示例：

```json
{
  "conditionCode": "AbBb",
  "conditionType": "FOLLOW",
  "conditionDescription": "A 必须在 B 之前",
  "nodeId1": 3001,
  "nodeId2": 3002,
  "enabled": 1
}
```

### 9.2 约束列表

`GET /api/graphs/{graphId}/constraints`

### 9.3 约束详情

`GET /api/graphs/{graphId}/constraints/{constraintId}`

### 9.4 修改约束

`PUT /api/graphs/{graphId}/constraints/{constraintId}`

请求示例：

```json
{
  "conditionType": "CONNECT",
  "conditionDescription": "修改为连接约束",
  "nodeId1": 3001,
  "nodeId2": 3002,
  "enabled": 1
}
```

### 9.5 删除约束

`DELETE /api/graphs/{graphId}/constraints/{constraintId}`

## 10. YAML 导入导出接口

### 10.1 导入 YAML

`POST /api/workspaces/{workspaceId}/graphs/import-yaml`

请求：

- `multipart/form-data`
- 字段：
  - `file`
  - `graphName` 可选

响应示例：

```json
{
  "code": 0,
  "message": "导入成功",
  "data": {
    "graphId": 1002,
    "workspaceId": 1,
    "name": "input30-test",
    "sourceType": "YAML_IMPORT"
  }
}
```

导入流程：

```text
上传 YAML -> 解析为算法模型 -> 校验结构 -> 转换为数据库实体 -> 写入 MySQL -> 清理旧缓存
```

### 10.2 导出流程图 YAML

`GET /api/graphs/{graphId}/export-yaml`

响应：

- 文件流下载
- 文件名示例：`graph-1001.yaml`

### 10.3 导出优化结果 YAML

`GET /api/optimize-tasks/{taskId}/export-yaml`

响应：

- 文件流下载
- 内容来源于 `optimize_result.result_graph_json` 转 YAML

## 11. 优化任务接口

### 11.1 提交优化任务

`POST /api/graphs/{graphId}/optimize-tasks`

请求示例：

```json
{
  "algorithmType": 1,
  "algorithmMode": 2,
  "timeWeight": 1,
  "precisionWeight": 1,
  "costWeight": 1
}
```

响应示例：

```json
{
  "code": 0,
  "message": "任务已提交",
  "data": {
    "taskId": 5001,
    "taskNo": "TASK202604250001",
    "taskStatus": "PENDING"
  }
}
```

说明：

- Controller 只负责创建任务和入线程池；
- 实际执行由 `ThreadPoolTaskExecutor` 异步处理。

### 11.2 任务列表

`GET /api/optimize-tasks?pageNo=1&pageSize=10&workspaceId=1&graphId=1001&taskStatus=FAILED`

### 11.3 任务详情 / 状态查询

`GET /api/optimize-tasks/{taskId}`

响应示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "taskId": 5001,
    "taskNo": "TASK202604250001",
    "workspaceId": 1,
    "graphId": 1001,
    "algorithmType": 1,
    "algorithmMode": 2,
    "timeWeight": 1,
    "precisionWeight": 1,
    "costWeight": 1,
    "taskStatus": "RUNNING",
    "retryCount": 0,
    "queueTime": "2026-04-25 19:00:00",
    "startedAt": "2026-04-25 19:00:01",
    "finishedAt": null,
    "errorCode": null,
    "errorMessage": null
  }
}
```

缓存说明：

- 优先查 Redis 任务状态缓存；
- 未命中再回源 MySQL。

### 11.4 任务失败重试

`POST /api/optimize-tasks/{taskId}/retry`

请求示例：

```json
{
  "reason": "人工触发重试"
}
```

响应示例：

```json
{
  "code": 0,
  "message": "重试任务已提交",
  "data": {
    "taskId": 5001,
    "taskStatus": "PENDING",
    "retryCount": 1
  }
}
```

约束：

- 仅 `FAILED` 状态允许重试；
- 超过 `maxRetryCount` 后返回业务错误；
- 重试复用原任务参数，不允许在该接口改算法参数。

## 12. 优化结果接口

### 12.1 查询优化结果

`GET /api/optimize-tasks/{taskId}/result`

响应示例：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "resultId": 6001,
    "taskId": 5001,
    "sourceGraphId": 1001,
    "scoreRatio": 0.286500,
    "metrics": {
      "time": {
        "before": 120,
        "after": 100,
        "change": -20
      },
      "precision": {
        "before": 0.8100,
        "after": 0.8700,
        "change": 0.0600
      },
      "cost": {
        "before": 300,
        "after": 260,
        "change": -40
      }
    },
    "diff": {
      "addedPaths": [
        {
          "fromNodeCode": "Eb",
          "toNodeCode": "Hb"
        }
      ],
      "removedPaths": [
        {
          "fromNodeCode": "Fb",
          "toNodeCode": "Hb"
        }
      ]
    },
    "mapCode": "flowchart LR\nEb == add ==> Hb\nFb -. remove .-> Hb\n",
    "resultGraph": {
      "nodes": [],
      "paths": [],
      "constraints": [],
      "equipments": []
    }
  }
}
```

### 12.2 查询工作空间下优化结果列表

`GET /api/optimize-results?pageNo=1&pageSize=10&workspaceId=1&graphId=1001`

说明：

- 该接口适合结果中心列表页；
- `GET /api/optimize-tasks/{taskId}/result` 适合任务结果详情页。

## 13. 权限与资源校验规则

### 13.1 通用规则

- 所有资源最终都要落到 `workspace` 校验。
- 普通用户只能访问自己的工作空间及其下资源。
- 管理员可查看全部资源。

### 13.2 典型校验链

```text
taskId -> optimize_task.workspace_id -> workspace.owner_user_id -> 当前登录用户
graphId -> flow_graph.workspace_id -> workspace.owner_user_id -> 当前登录用户
nodeId/pathId/equipmentId/constraintId -> graph_id -> workspace_id -> owner_user_id
```

## 14. 旧接口保留策略

第一阶段保留旧接口，逐步切到新接口：

- 旧接口继续存在；
- 新功能只在 `/api` 下提供；
- 新接口统一加 Sa-Token 鉴权；
- 新接口统一返回 `Result<T>`；
- 当前旧接口不作为长期演进目标。

## 15. 第一版接口清单总表

### 15.1 认证

- `POST /api/auth/login`
- `POST /api/auth/logout`
- `GET /api/auth/me`

### 15.2 工作空间

- `POST /api/workspaces`
- `GET /api/workspaces`
- `GET /api/workspaces/{workspaceId}`
- `PUT /api/workspaces/{workspaceId}`
- `DELETE /api/workspaces/{workspaceId}`

### 15.3 流程图

- `POST /api/workspaces/{workspaceId}/graphs`
- `GET /api/workspaces/{workspaceId}/graphs`
- `GET /api/graphs/{graphId}`
- `PUT /api/graphs/{graphId}`
- `DELETE /api/graphs/{graphId}`

### 15.4 节点

- `POST /api/graphs/{graphId}/nodes`
- `GET /api/graphs/{graphId}/nodes`
- `GET /api/graphs/{graphId}/nodes/{nodeId}`
- `PUT /api/graphs/{graphId}/nodes/{nodeId}`
- `DELETE /api/graphs/{graphId}/nodes/{nodeId}`

### 15.5 路径

- `POST /api/graphs/{graphId}/paths`
- `GET /api/graphs/{graphId}/paths`
- `GET /api/graphs/{graphId}/paths/{pathId}`
- `PUT /api/graphs/{graphId}/paths/{pathId}`
- `DELETE /api/graphs/{graphId}/paths/{pathId}`

### 15.6 装备

- `POST /api/graphs/{graphId}/equipments`
- `GET /api/graphs/{graphId}/equipments`
- `GET /api/graphs/{graphId}/equipments/{equipmentId}`
- `PUT /api/graphs/{graphId}/equipments/{equipmentId}`
- `DELETE /api/graphs/{graphId}/equipments/{equipmentId}`
- `POST /api/graphs/{graphId}/equipments/{equipmentId}/image`

### 15.7 约束

- `POST /api/graphs/{graphId}/constraints`
- `GET /api/graphs/{graphId}/constraints`
- `GET /api/graphs/{graphId}/constraints/{constraintId}`
- `PUT /api/graphs/{graphId}/constraints/{constraintId}`
- `DELETE /api/graphs/{graphId}/constraints/{constraintId}`

### 15.8 YAML 导入导出

- `POST /api/workspaces/{workspaceId}/graphs/import-yaml`
- `GET /api/graphs/{graphId}/export-yaml`
- `GET /api/optimize-tasks/{taskId}/export-yaml`

### 15.9 优化任务与结果

- `POST /api/graphs/{graphId}/optimize-tasks`
- `GET /api/optimize-tasks`
- `GET /api/optimize-tasks/{taskId}`
- `POST /api/optimize-tasks/{taskId}/retry`
- `GET /api/optimize-tasks/{taskId}/result`
- `GET /api/optimize-results`
