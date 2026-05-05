# 前端部署说明

## 1. 构建产物

前端构建命令：

```bash
cd frontend
npm install
npm run build
```

构建产物输出到：

```text
frontend/dist/
```

## 2. 环境变量

生产环境需要提供：

```env
VITE_API_BASE_URL=https://your-backend.example.com
```

本地开发默认：

```env
VITE_API_BASE_URL=http://127.0.0.1:8081
```

前端不会在页面中硬编码后端地址；请求统一通过 `frontend/src/api/request.ts` 读取 `VITE_API_BASE_URL`。

## 3. 静态资源托管

`dist/` 可部署到 Nginx、对象存储静态站点、企业内部静态服务器等。

Nginx 示例：

```nginx
server {
    listen 80;
    server_name your-frontend.example.com;

    root /opt/optimization-frontend/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }
}
```

## 4. 后端跨域与认证

- 前端通过 `satoken: <token>` 请求头携带登录态。
- 若前后端不同域部署，后端需要允许前端域名跨域访问，并允许 `satoken` 请求头。
- 登录失效由前端统一清理本地 token 并跳转 `/login`。

## 5. 部署前检查

```bash
npm run typecheck
npm run lint
npm run build
npm run test
npm run test:e2e
```

`test:e2e` 默认使用 Playwright，并通过本地 Vite dev server 运行 smoke 测试。

## 6. 已知注意事项

- 当前 Vite 构建会提示 chunk size 超过 500 kB，主要来自 Ant Design、ECharts、React Flow 等依赖；当前为警告，不影响构建产物。
- 如需要进一步优化首屏体积，后续可对路由页面做动态导入拆包。
- YAML 导入导出 round-trip 问题属于后端契约问题，部署前可按 `BCR-001` 决定是否修复。
