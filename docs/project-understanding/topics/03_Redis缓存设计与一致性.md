# 03_Redis缓存设计与一致性

## 一、Redis 在这个项目里的定位

Redis 不是事实数据源，MySQL 才是。

Redis 在当前项目只承担“加速”和“弱依赖兜底”角色，典型场景有两类：

1. graph detail 展示态缓存
2. optimize task 状态 / result 缓存

## 二、为什么要有 `RedisSafeClient`

`RedisSafeClient` 的设计意图很直接：

- Redis 挂了，主业务尽量不要跟着挂

它的做法是：

- 读失败 -> 打 warn，返回 `null`
- 写失败 -> 打 warn，不往外抛
- 删失败 -> 打 warn，不往外抛

这说明当前项目把 Redis 定位成“可失效的加速层”，不是强一致主存储。

## 三、graph detail 缓存

### 入口文件

- `GraphAppServiceImpl`

### key 设计

`graph:detail:{graphId}:v{graphVersion}`

### 为什么要带 `graphVersion`

因为 graph detail 不是只看 `flow_graph` 一张表，而是：

- graph
- nodes
- paths
- equipments
- constraints

任何一个子资源改了，旧缓存都应该失效。  
而当前项目没有做复杂的局部缓存更新，所以采用：

- 写操作后 `graph_version + 1`
- 查询时新版本自然命中新 key

### 为什么不直接覆盖原 key

因为带版本号的方式简单、稳定，而且更适合聚合缓存。

## 四、优化任务缓存

### 1. 任务状态缓存

入口文件：

- `OptimizeTaskCacheServiceImpl`
- `OptimizeTaskStateServiceImpl`
- `OptimizeTaskAppServiceImpl`

key：

`optimize:task:status:{taskId}`

用途：

- 提高任务状态查询速度
- 在任务执行过程中让状态更新更实时

### 2. 优化结果缓存

key：

`optimize:result:{taskId}`

用途：

- 成功任务查询结果时先读缓存
- 缓存未命中再回 MySQL，再回填缓存

## 五、一致性策略是怎么做的

### graph detail

- 事实数据改动在 MySQL
- graphVersion 递增
- 新请求走新缓存 key

这是“版本式失效”。

### optimize task status

- 状态变更先更新数据库
- 同时更新缓存
- 查询时缓存未命中再从数据库回填

### optimize result

- 算法成功后先插入结果表
- 再缓存结果 VO
- 重试前先清掉旧结果缓存

## 六、为什么没有更复杂的缓存策略

因为当前项目更强调：

- 简单可控
- 容易解释
- 不破坏主链可靠性

如果过早做复杂局部更新，会让 graph 聚合逻辑和一致性逻辑显著变重。

## 七、看缓存逻辑时最该抓住的点

1. Redis 失败不会成为主业务致命错误
2. MySQL 永远是最终依据
3. 缓存的是“高价值读模型”，不是所有表
4. `graph_version` 是 graph 聚合缓存的关键辅助机制
