import { defineConfig } from '@playwright/test'

const apiBaseUrl = process.env.VITE_API_BASE_URL
const webServerCommand = apiBaseUrl
  ? `powershell -NoProfile -Command "$env:VITE_API_BASE_URL='${apiBaseUrl}'; npm run dev -- --host 127.0.0.1 --port 7777"`
  : 'npm run dev -- --host 127.0.0.1 --port 7777'

export default defineConfig({
  testDir: './tests/e2e',
  timeout: 30_000,
  use: {
    baseURL: 'http://127.0.0.1:7777',
    trace: 'on-first-retry',
  },
  webServer: {
    command: webServerCommand,
    url: 'http://127.0.0.1:7777',
    reuseExistingServer: !apiBaseUrl,
  },
})
