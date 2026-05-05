# Frontend API Contract

## 1. 文档定位

- 目标：为后续 `frontend/src/api` 与 `frontend/src/types` 提供唯一接口契约基线
- 适用范围：仅覆盖前端目标新接口与明确保留的兼容接口
- 生成依据：
- 真实控制器：`src/main/java/**/module/**/controller/**`
- 真实 DTO / VO：`src/main/java/**/module/**/dto/**`、`src/main/java/**/module/**/vo/**`
- 通用响应类型：`common/response/Result.java`、`PageResult.java`、`ErrorCode.java`
- 运行态 OpenAPI：`http://127.0.0.1:8081/v3/api-docs`
- 设计草案：`docs/refactor/03_API设计.md`

## 2. 契约优先级

1. 真实控制器映射
2. DTO / VO / 通用响应类型
3. 运行态 OpenAPI
4. 历史设计文档

当设计文档与真实代码不一致时，必须以代码和运行态 OpenAPI 为准。

## 3. 全局约定

### 3.1 Base URL

- 前端必须通过 `VITE_API_BASE_URL` 指向后端
- 本地默认后端地址：`http://127.0.0.1:8081`

### 3.2 认证

- 登录接口：`POST /api/auth/login`
- 除登录外，目标新接口默认要求登录
- 当前高置信约定：请求头传递 `satoken: <token>`
- 登录返回体会返回：
- `token`
- `tokenName`
- `userId`
- `username`
- `nickname`
- `roleCode`

备注：Header 方案已被设计文档、Sa-Token 配置和登录返回结构共同支持；最终运行时联调确认放在 Phase 0 后续任务中完成。

### 3.3 统一响应结构

所有新接口统一使用：

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

对应前端通用类型建议：

```ts
type ApiResult<T> = {
  code: number;
  message: string;
  data: T;
};
```

### 3.4 分页响应结构

分页列表统一包装在 `data` 中：

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

对应前端通用类型建议：

```ts
type PageResult<T> = {
  records: T[];
  pageNo: number;
  pageSize: number;
  total: number;
};
```

### 3.5 通用错误码

| code | 含义 |
| --- | --- |
| `0` | 成功 |
| `400001` | 参数错误 |
| `401001` | 未登录 |
| `403001` | 无权限 |
| `404001` | 资源不存在 |
| `409001` | 数据冲突 |
| `500001` | 系统异常 |
| `600001` | 任务执行失败 |
| `700001` | 文件解析失败 |

## 4. 前端目标接口白名单

本章只列前端应接入的新接口。凡未出现在本章的路径，即使出现在 OpenAPI 中，也不视为默认前端目标接口。

### 4.1 Auth

| 方法 | 路径 | 请求类型 | 响应类型 | 说明 |
| --- | --- | --- | --- | --- |
| `POST` | `/api/auth/login` | Body: `LoginRequest` | `ApiResult<LoginResponseVO>` | 登录 |
| `POST` | `/api/auth/logout` | 无 | `ApiResult<boolean>` | 退出登录 |
| `GET` | `/api/auth/me` | 无 | `ApiResult<CurrentUserVO>` | 当前用户 |

#### 类型摘要

- `LoginRequest`
  - required: `username`, `password`
- `LoginResponseVO`
  - `userId`, `username`, `nickname`, `roleCode`, `token`, `tokenName`
- `CurrentUserVO`
  - `userId`, `username`, `nickname`, `roleCode`

### 4.2 Workspace

| 方法 | 路径 | 请求类型 | 响应类型 | 说明 |
| --- | --- | --- | --- | --- |
| `POST` | `/api/workspaces` | Body: `CreateWorkspaceRequest` | `ApiResult<WorkspaceVO>` | 创建工作空间 |
| `GET` | `/api/workspaces` | Query: `WorkspaceQueryRequest` | `ApiResult<PageResult<WorkspaceVO>>` | 分页查询 |
| `GET` | `/api/workspaces/{workspaceId}` | Path: `workspaceId` | `ApiResult<WorkspaceVO>` | 详情 |
| `PUT` | `/api/workspaces/{workspaceId}` | Path + Body: `UpdateWorkspaceRequest` | `ApiResult<WorkspaceVO>` | 更新 |
| `DELETE` | `/api/workspaces/{workspaceId}` | Path: `workspaceId` | `ApiResult<boolean>` | 删除 |

#### 类型摘要

- `CreateWorkspaceRequest`
  - required: `name`
  - fields: `name`, `description`
- `UpdateWorkspaceRequest`
  - required: `name`
  - fields: `name`, `description`, `status`
- `WorkspaceQueryRequest`
  - fields: `pageNo`, `pageSize`, `keyword`
- `WorkspaceVO`
  - fields: `id`, `ownerUserId`, `name`, `description`, `status`, `createdAt`, `updatedAt`

### 4.3 Graph

| 方法 | 路径 | 请求类型 | 响应类型 | 说明 |
| --- | --- | --- | --- | --- |
| `POST` | `/api/workspaces/{workspaceId}/graphs` | Path + Body: `CreateGraphRequest` | `ApiResult<GraphVO>` | 创建流程图 |
| `GET` | `/api/workspaces/{workspaceId}/graphs` | Path + Query: `GraphQueryRequest` | `ApiResult<PageResult<GraphVO>>` | 分页查询流程图 |
| `GET` | `/api/graphs/{graphId}` | Path: `graphId` | `ApiResult<GraphVO>` | 基础信息 |
| `GET` | `/api/graphs/{graphId}/detail` | Path: `graphId` | `ApiResult<GraphDetailVO>` | 聚合详情 |
| `PUT` | `/api/graphs/{graphId}` | Path + Body: `UpdateGraphRequest` | `ApiResult<GraphVO>` | 更新基础信息 |
| `DELETE` | `/api/graphs/{graphId}` | Path: `graphId` | `ApiResult<boolean>` | 删除 |

#### 类型摘要

- `CreateGraphRequest`
  - required: `name`
  - fields: `name`, `description`, `sourceType`, `graphStatus`
- `UpdateGraphRequest`
  - required: `name`
  - fields: `name`, `description`, `graphStatus`, `totalTime`, `totalPrecision`, `totalCost`
- `GraphQueryRequest`
  - fields: `pageNo`, `pageSize`, `keyword`
- `GraphVO`
  - fields: `id`, `workspaceId`, `name`, `description`, `sourceType`, `graphStatus`, `graphVersion`, `totalTime`, `totalPrecision`, `totalCost`, `createdAt`, `updatedAt`
- `GraphDetailVO`
  - fields: `graph`, `nodes`, `paths`, `equipments`, `constraints`

说明：

- `GET /api/graphs/{graphId}` 只返回基础信息
- `GET /api/graphs/{graphId}/detail` 才是图编辑/展示页的重要聚合接口

### 4.4 Node

| 方法 | 路径 | 请求类型 | 响应类型 |
| --- | --- | --- | --- |
| `POST` | `/api/graphs/{graphId}/nodes` | Path + Body: `CreateNodeRequest` | `ApiResult<NodeVO>` |
| `GET` | `/api/graphs/{graphId}/nodes` | Path + Query: `NodeQueryRequest` | `ApiResult<PageResult<NodeVO>>` |
| `GET` | `/api/graphs/{graphId}/nodes/{nodeId}` | Path | `ApiResult<NodeVO>` |
| `PUT` | `/api/graphs/{graphId}/nodes/{nodeId}` | Path + Body: `UpdateNodeRequest` | `ApiResult<NodeVO>` |
| `DELETE` | `/api/graphs/{graphId}/nodes/{nodeId}` | Path | `ApiResult<boolean>` |

#### 类型摘要

- `CreateNodeRequest`
  - required: `nodeCode`
  - fields: `nodeCode`, `nodeName`, `nodeDescription`, `equipmentId`, `timeCost`, `precisionValue`, `costValue`, `sortNo`
- `UpdateNodeRequest`
  - required: `nodeCode`
  - fields: `nodeCode`, `nodeName`, `nodeDescription`, `equipmentId`, `timeCost`, `precisionValue`, `costValue`, `sortNo`
- `NodeQueryRequest`
  - fields: `pageNo`, `pageSize`, `keyword`
- `NodeVO`
  - fields: `id`, `graphId`, `nodeCode`, `nodeName`, `nodeDescription`, `equipmentId`, `timeCost`, `precisionValue`, `costValue`, `sortNo`, `createdAt`, `updatedAt`

### 4.5 Path

| 方法 | 路径 | 请求类型 | 响应类型 |
| --- | --- | --- | --- |
| `POST` | `/api/graphs/{graphId}/paths` | Path + Body: `CreatePathRequest` | `ApiResult<PathVO>` |
| `GET` | `/api/graphs/{graphId}/paths` | Path + Query: `PathQueryRequest` | `ApiResult<PageResult<PathVO>>` |
| `GET` | `/api/graphs/{graphId}/paths/{pathId}` | Path | `ApiResult<PathVO>` |
| `PUT` | `/api/graphs/{graphId}/paths/{pathId}` | Path + Body: `UpdatePathRequest` | `ApiResult<PathVO>` |
| `DELETE` | `/api/graphs/{graphId}/paths/{pathId}` | Path | `ApiResult<boolean>` |

#### 类型摘要

- `CreatePathRequest`
  - required: `startNodeId`, `endNodeId`
  - fields: `startNodeId`, `endNodeId`, `relationType`, `remark`
- `UpdatePathRequest`
  - required: `startNodeId`, `endNodeId`
  - fields: `startNodeId`, `endNodeId`, `relationType`, `remark`
- `PathQueryRequest`
  - fields: `pageNo`, `pageSize`
- `PathVO`
  - fields: `id`, `graphId`, `startNodeId`, `endNodeId`, `relationType`, `remark`, `createdAt`, `updatedAt`

### 4.6 Equipment

| 方法 | 路径 | 请求类型 | 响应类型 |
| --- | --- | --- | --- |
| `POST` | `/api/graphs/{graphId}/equipments` | Path + Body: `CreateEquipmentRequest` | `ApiResult<EquipmentVO>` |
| `GET` | `/api/graphs/{graphId}/equipments` | Path + Query: `EquipmentQueryRequest` | `ApiResult<PageResult<EquipmentVO>>` |
| `GET` | `/api/graphs/{graphId}/equipments/{equipmentId}` | Path | `ApiResult<EquipmentVO>` |
| `PUT` | `/api/graphs/{graphId}/equipments/{equipmentId}` | Path + Body: `UpdateEquipmentRequest` | `ApiResult<EquipmentVO>` |
| `DELETE` | `/api/graphs/{graphId}/equipments/{equipmentId}` | Path | `ApiResult<boolean>` |

#### 类型摘要

- `CreateEquipmentRequest`
  - required: `name`
  - fields: `name`, `description`, `color`, `imagePath`
- `UpdateEquipmentRequest`
  - required: `name`
  - fields: `name`, `description`, `color`, `imagePath`
- `EquipmentQueryRequest`
  - fields: `pageNo`, `pageSize`, `keyword`
- `EquipmentVO`
  - fields: `id`, `graphId`, `name`, `description`, `color`, `imagePath`, `createdAt`, `updatedAt`

备注：当前新接口白名单中未发现新的 `/api/**` 装备图片上传接口，不应按旧设计稿自行补接。

### 4.7 Constraint

| 方法 | 路径 | 请求类型 | 响应类型 |
| --- | --- | --- | --- |
| `POST` | `/api/graphs/{graphId}/constraints` | Path + Body: `CreateConstraintRequest` | `ApiResult<ConstraintVO>` |
| `GET` | `/api/graphs/{graphId}/constraints` | Path + Query: `ConstraintQueryRequest` | `ApiResult<PageResult<ConstraintVO>>` |
| `GET` | `/api/graphs/{graphId}/constraints/{constraintId}` | Path | `ApiResult<ConstraintVO>` |
| `PUT` | `/api/graphs/{graphId}/constraints/{constraintId}` | Path + Body: `UpdateConstraintRequest` | `ApiResult<ConstraintVO>` |
| `DELETE` | `/api/graphs/{graphId}/constraints/{constraintId}` | Path | `ApiResult<boolean>` |

#### 类型摘要

- `CreateConstraintRequest`
  - required: `conditionCode`, `conditionType`, `nodeId1`, `nodeId2`
  - fields: `conditionCode`, `conditionType`, `conditionDescription`, `nodeId1`, `nodeId2`, `enabled`
- `UpdateConstraintRequest`
  - required: `conditionCode`, `conditionType`, `nodeId1`, `nodeId2`
  - fields: `conditionCode`, `conditionType`, `conditionDescription`, `nodeId1`, `nodeId2`, `enabled`
- `ConstraintQueryRequest`
  - fields: `pageNo`, `pageSize`, `keyword`
- `ConstraintVO`
  - fields: `id`, `graphId`, `conditionCode`, `conditionType`, `conditionDescription`, `nodeId1`, `nodeId2`, `enabled`, `createdAt`, `updatedAt`

### 4.8 Optimize Task & Result

| 方法 | 路径 | 请求类型 | 响应类型 | 说明 |
| --- | --- | --- | --- | --- |
| `POST` | `/api/optimize/tasks` | Body: `CreateOptimizeTaskRequest` | `ApiResult<OptimizeTaskSubmitVO>` | 提交优化任务 |
| `GET` | `/api/optimize/tasks` | Query: `OptimizeTaskQueryRequest` | `ApiResult<PageResult<OptimizeTaskVO>>` | 任务分页列表 |
| `GET` | `/api/optimize/tasks/{taskId}` | Path | `ApiResult<OptimizeTaskVO>` | 查询任务状态 |
| `GET` | `/api/optimize/tasks/{taskId}/result` | Path | `ApiResult<OptimizeResultVO>` | 查询优化结果 |
| `POST` | `/api/optimize/tasks/{taskId}/retry` | Path | `ApiResult<OptimizeTaskSubmitVO>` | 重试失败任务 |

#### 类型摘要

- `CreateOptimizeTaskRequest`
  - required: `graphId`, `algorithmType`, `algorithmMode`
  - fields: `graphId`, `algorithmType`, `algorithmMode`, `timeWeight`, `precisionWeight`, `costWeight`
- `OptimizeTaskQueryRequest`
  - fields: `pageNo`, `pageSize`, `workspaceId`, `graphId`, `taskStatus`
- `OptimizeTaskSubmitVO`
  - fields: `taskId`, `taskNo`, `taskStatus`
- `OptimizeTaskVO`
  - fields: `id`, `taskNo`, `workspaceId`, `graphId`, `userId`, `algorithmType`, `algorithmMode`, `timeWeight`, `precisionWeight`, `costWeight`, `taskStatus`, `retryCount`, `maxRetryCount`, `queueTime`, `startedAt`, `finishedAt`, `errorCode`, `errorMessage`, `resultId`, `createdAt`, `updatedAt`
- `OptimizeResultVO`
  - fields: `id`, `taskId`, `workspaceId`, `sourceGraphId`, `resultName`, `resultGraph`, `diff`, `mapCode`, `totalTimeBefore`, `totalPrecisionBefore`, `totalCostBefore`, `totalTimeAfter`, `totalPrecisionAfter`, `totalCostAfter`, `scoreRatio`, `createdAt`

说明：

- 实际任务中心路径是 `/api/optimize/tasks`
- `graphId` 在请求体里，不在 URL 路径里
- `retry` 当前无需额外 Body

### 4.9 YAML Import / Export

| 方法 | 路径 | 请求类型 | 响应类型 | 说明 |
| --- | --- | --- | --- | --- |
| `POST` | `/api/import/graphs` | `multipart/form-data`：`file` + `GraphImportRequest` | `ApiResult<GraphImportResponse>` | 导入流程图 |
| `GET` | `/api/export/graphs/{graphId}/yaml` | Path | `ApiResult<GraphYamlExportResponse>` | 导出 YAML 文本 |

#### 类型摘要

- `GraphImportRequest`
  - fields: `workspaceId`, `graphName`
- `GraphImportResponse`
  - fields: `graphId`, `workspaceId`, `graphName`, `sourceType`, `nodeCount`, `pathCount`, `equipmentCount`, `constraintCount`
- `GraphYamlExportResponse`
  - fields: `graphId`, `graphName`, `fileName`, `yamlContent`

说明：

- 导入接口的 `workspaceId` 放在表单字段中，不在路径中
- 导出接口返回 JSON 包装的文本内容，前端应自行转 Blob 下载
- 不能按“浏览器直下文件流”假设实现

## 5. legacy 兼容接口清单

### 5.1 前端明确保留的兼容优化接口

这些接口只能收敛在未来的 `frontend/src/api/legacyAlgorithm.ts` 中：

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `POST` | `/optimizeByFile` | 旧文件优化 |
| `POST` | `/optimizeByInput` | 旧输入优化 |
| `POST` | `/uploadFile` | 旧上传 |
| `GET` | `/downloadFile` | 旧下载 |

### 5.2 更宽泛的 legacy 定义

当前仓库中，顶层 `src/main/java/com/example/optimization_algorithm_backend/Controller/` 包应整体视为兼容层，而不仅仅是以上 4 个接口。

示例 legacy 路径还包括：

- `/upload`
- `/save`
- `/optimize`
- `/getMapCode`
- `/addNode`
- `/deletePath`
- `/uploadEquipmentImage`
- `/exportExample`

规则：

- 新业务页面不得默认接入这些接口
- 若未来必须兼容接入，需先由主控 Agent 在 backlog 中显式加任务
- 未经批准，不得将这些接口混入新领域 API 模块

## 6. 当前已识别的设计偏差

以下差异已经确认存在：

- 历史设计稿中把优化任务路径写成 `/api/optimize-tasks`，真实代码是 `/api/optimize/tasks`
- 历史设计稿中把 YAML 导入写成 `/api/workspaces/{workspaceId}/graphs/import-yaml`，真实代码是 `/api/import/graphs`
- 历史设计稿中把导出描述为文件流，真实代码返回 `GraphYamlExportResponse`
- 历史设计稿中把任务提交设计成图路径下资源，真实代码是任务中心资源，且 `graphId` 在 Body 中
- 历史设计稿中包含装备图片上传的新接口设想，但当前 `/api/**` 白名单中未发现对应真实控制器

## 7. 前端实现要求映射

- 所有请求封装必须放在 `frontend/src/api`
- 所有接口类型必须放在 `frontend/src/types`
- 页面层禁止直接写 axios
- 登录失效必须统一清理 token 并跳转 `/login`
- 所有新业务优先走本合同中的白名单接口
- 所有 legacy 兼容请求必须单独隔离，禁止污染新模块

## 8. 待 Phase 0 继续确认的事项

- `satoken` 是否仅走请求头，还是同时兼容 Cookie
- 真实登录、退出、`/me` 的运行时行为与异常格式
- YAML 导入失败时 `ImportErrorReport` 的实际返回形态
- 需要哪些测试账号和角色才能覆盖前端主流程
