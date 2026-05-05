# 02_MyBatisPlus与持久化分层

## 一、为什么重构后文件一下子变多了

因为项目从“功能集中写在 Service 里”拆成了多层：

- DTO
- VO
- Converter
- Entity
- Mapper
- AppService
- PersistenceService

这种拆分的目的不是形式主义，而是把“谁负责什么”拆清楚。

## 二、每一层到底负责什么

### DTO

位置：

- `module/*/dto`

职责：

- 接收请求参数
- 承载分页、筛选、创建、更新字段

不负责：

- 业务编排
- 数据库逻辑
- 返回前端的完整展示

### VO

位置：

- `module/*/vo`

职责：

- 作为 API 返回视图

不负责：

- 数据库存储
- 算法输入

### Entity

位置：

- `infrastructure/persistence/entity`

职责：

- 对应数据库表

你看到它时要下意识问：

- 这对应哪张表
- 这是不是事实数据源里的字段

而不是问“它要不要直接返回前端”。

### Mapper

位置：

- `infrastructure/persistence/mapper`

职责：

- MyBatis-Plus 的直接数据库访问入口

当前项目很多条件构造都在 AppService 中完成，再交给 Mapper 执行。

### AppService

位置：

- `module/*/service/impl/*AppServiceImpl`

职责：

- 真正组织一次业务用例

比如：

- graph detail 聚合
- YAML 导入
- optimize task 提交

### PersistenceService

位置：

- `infrastructure/persistence/service`

职责：

- 基于 MyBatis-Plus 的技术层服务封装

当前项目里它更多是技术储备和部分模块使用的基础设施能力，不是所有业务都必须经过它。

### Converter

位置：

- `module/*/converter`

职责：

- 跨层翻译

尤其是：

- `Entity -> VO`
- `ProcessMap <-> Entity`

## 三、MyBatis-Plus 在项目里到底帮了什么

它主要帮了 3 件事：

1. 提供 `BaseMapper`
2. 提供 `ServiceImpl/IService`
3. 提供 `Page`、`LambdaQueryWrapper`、`LambdaUpdateWrapper`

所以你会经常看到：

- `selectPage`
- `selectCount`
- `update(null, new LambdaUpdateWrapper<>())`

## 四、为什么算法模型不能直接做 Entity

`ProcessMap`、`MultiNode`、`ProcessPath`、`ConstraintCondition` 是算法领域模型。

如果直接拿它们做数据库 Entity，会导致 3 个问题：

1. 算法模型的字段和结构不稳定
2. 平台需要的权限、审计、分页、查询字段不一定在算法模型里
3. 算法层和平台层会高度耦合

所以项目才通过 `ProcessMapConverter` 做桥接。

## 五、你看分层时的正确心法

### 问题 1：这个类是在“做决定”，还是“做搬运”

- 做决定：通常是 AppService
- 做搬运：通常是 DTO/VO/Converter/Mapper

### 问题 2：这个对象是给谁看的

- 前端看：VO
- 数据库看：Entity
- 算法看：算法模型
- 请求进来时看：DTO

### 问题 3：一个字段为什么在多个对象里都出现

因为它们服务的“层”不同，不代表重复设计就是错的。
