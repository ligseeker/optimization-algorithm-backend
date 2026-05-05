# 08_YAML导入导出模块

## 一、模块定位

这个模块是“旧格式兼容层”和“算法模型桥接层”的重叠区域。

它回答的问题是：

1. 旧 YAML 文件怎么导入成平台里的 graph、node、path、equipment、constraint
2. 平台里的结构化数据怎么再导出成算法还能理解的 YAML
3. 导入校验失败时，怎么给前端返回结构化错误信息

## 二、模块级三条线

### 请求流

```text
GraphYamlController
  -> GraphYamlServiceImpl
  -> ResourceAccessServiceImpl
  -> FlowGraphMapper / EquipmentMapper / NodeMapper / PathMapper / ConstraintMapper
  -> ProcessMapConverter / algorithm.Main
  -> GraphImportResponse / GraphYamlExportResponse
```

### 数据流

```text
MultipartFile
  -> 临时文件
  -> Main.readData / Main.initMapTest
  -> ProcessMap
  -> ProcessMapConverter
  -> Entity 集合
  -> 数据库

导出方向：
Entity 集合
  -> ProcessMapConverter.toProcessMap
  -> ProcessMap
  -> Main.writeData
  -> yamlContent
```

### 异常流

- 文件为空 / 后缀错误 -> `ImportValidationException`
- YAML 解析失败 -> `ImportValidationException`
- 结构校验失败 -> `ImportValidationException`
- 名称冲突 / 权限问题 -> `BusinessException`
- 最终：
  - `ImportValidationException` 返回 `ImportErrorReport`
  - 其他业务错误返回标准 `Result.fail`

## 三、关键文件逐个说明

### 1. `module/yaml/controller/GraphYamlController.java`

#### 文件职责

提供两个接口：

- 导入 YAML 为 graph
- 导出 graph 为 YAML

#### 主要方法

- `importGraph`
  收 `MultipartFile` 和 `GraphImportRequest`

- `exportGraphYaml`
  按 `graphId` 导出 YAML 文本

#### 最容易看不懂的点

导出接口不是直接下载文件流，而是把 YAML 文本放进 `GraphYamlExportResponse` 里返回。

### 2. `module/yaml/service/impl/GraphYamlServiceImpl.java`

这是整个 YAML 模块最核心的文件。

#### 字段逐项说明

- 一组 `ObjectProvider<...Mapper>`
  说明导入导出不是单表行为，而是 graph 聚合行为
- `resourceAccessService`
  导入前校验 workspace，可导出前校验 graph

#### 主要方法逐项说明

- `importGraph`
  主流程：
  1. 校验 workspace 可访问
  2. 校验文件存在且后缀合法
  3. `parseYamlToProcessMap`
  4. `validateProcessMap`
  5. 归一化 graph 名称
  6. 校验名称唯一
  7. `ProcessMapConverter.toFlowGraphEntity`
  8. 落 graph
  9. 分别保存 equipment / node / path / constraint
  10. 返回导入结果统计

- `exportGraphYaml`
  主流程：
  1. 校验 graph 可访问
  2. 查 graph 及其所有子资源
  3. `ProcessMapConverter.toProcessMap`
  4. `serializeProcessMapToYaml`
  5. 更新 `lastExportAt`
  6. 返回 YAML 文本

- `validateFile`
  平台侧最前置的一层校验

- `parseYamlToProcessMap`
  通过临时文件调用旧 `Main.readData` 和 `Main.initMapTest`

- `validateProcessMap`
  这是最值得细看的校验逻辑：
  - 节点 ID 非空、唯一
  - 路径起终点存在
  - 路径不重复
  - 约束节点存在
  - 约束类型合法

- `saveEquipments / saveNodes / savePaths / saveConstraints`
  把算法模型拆成多张表的实体记录

- `serializeProcessMapToYaml`
  借助旧 `Main.writeData` 生成 YAML 文本

#### 数据如何进入、如何离开

- 进入：`MultipartFile + GraphImportRequest`
- 中间 1：`ProcessMap`
- 中间 2：`FlowGraphEntity + 子资源 Entity`
- 离开：
  - 导入：`GraphImportResponse`
  - 导出：`GraphYamlExportResponse`

#### 可能抛出的异常

- `ImportValidationException`
- `BusinessException(CONFLICT / FORBIDDEN / RESOURCE_NOT_FOUND / SYSTEM_ERROR / FILE_PARSE_FAILED)`

#### 最容易看不懂的点

这个文件看起来像“文件处理类”，其实它更像“平台数据和旧算法世界之间的编排器”。

### 3. `module/yaml/converter/ProcessMapConverter.java`

#### 文件职责

负责两边翻译：

- `ProcessMap -> Entity`
- `Entity 集合 -> ProcessMap`

#### 为什么会有这个文件

这是整次重构最关键的解耦点之一。  
如果没有它，就只能把算法模型直接塞进数据库层，或者把数据库实体直接暴露给算法层，两边都会变得非常难维护。

#### 主要方法逐项说明

- `toFlowGraphEntity`
  从算法输入估算 graph 的基础统计值

- `toEquipmentEntity / toNodeEntity / toPathEntity / toConstraintEntity`
  把算法模型切成多张业务表可用的数据

- `toProcessMap`
  反方向把 graph 及子资源重新组装成算法模型

#### 最容易看不懂的点

`toProcessMap` 不是简单字段复制，它还要：

- 建立 `nodeId -> nodeCode` 映射
- 建立 `equipmentId -> equipmentName` 映射
- 把数据库中的外键关系还原成算法世界的节点编码关系

## 四、样板文件

### DTO

- `GraphImportRequest`

### VO

- `GraphImportResponse`
- `GraphYamlExportResponse`
- `ImportErrorReport`
- `ImportErrorItem`

其中 `ImportErrorReport` 最特别，因为它不是成功返回 VO，而是失败时放进异常响应体的数据结构。

## 五、这个模块和其他模块的关系

- 上游依赖：`workspace`、`graph`
- 横向依赖：`node / path / equipment / constraint`
- 深度依赖：`algorithm/Main` 和 `algorithm/model/*`
- 被谁复用：思路和对象转换方式被 `optimize` 模块复用
