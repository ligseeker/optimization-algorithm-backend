import { expect, test } from '@playwright/test'

const API_BASE_URL = process.env.E2E_API_BASE_URL ?? 'http://127.0.0.1:8081'

test('logs in and completes the workspace to graph editor flow', async ({
  page,
  request,
}) => {
  const stamp = Date.now()
  const workspaceName = `Codex E2E ${stamp}`
  const graphName = `Graph ${stamp}`
  let token = ''
  let graphId: number | null = null
  let workspaceId: number | null = null

  try {
    await page.goto('/login')
    await page.getByLabel('用户名').fill('admin')
    await page.getByLabel('密码').fill('admin123')
    await page.getByRole('button', { name: '进入控制台' }).click()

    await expect(page).toHaveURL(/\/dashboard/)
    await expect(
      page.getByRole('heading', { name: 'Industrial Control Surface' }),
    ).toBeVisible()

    token =
      (await page.evaluate(() => window.localStorage.getItem('oab.auth.token'))) ?? ''
    expect(token).not.toBe('')

    await page.getByRole('menuitem', { name: /Workspaces/ }).click()
    await expect(page.getByRole('heading', { name: '工作空间舰队' })).toBeVisible()

    await page.getByRole('button', { name: /新建工作空间/ }).click()
    const workspaceDialog = page.getByRole('dialog', { name: '新建工作空间' })
    await workspaceDialog.getByLabel('名称').fill(workspaceName)
    await workspaceDialog.getByLabel('描述').fill('Playwright runtime workspace')
    await workspaceDialog.getByRole('button', { name: /创\s*建/ }).click()

    const workspaceEntry = page.getByRole('button', { name: workspaceName }).first()
    await expect(workspaceEntry).toBeVisible()
    await workspaceEntry.click()

    await expect(page).toHaveURL(/\/workspaces\/\d+\/graphs/)
    workspaceId = Number(page.url().match(/workspaces\/(\d+)\/graphs/)?.[1] ?? 0)
    expect(workspaceId).toBeGreaterThan(0)

    await expect(page.getByRole('heading', { name: '流程图阵列' })).toBeVisible()
    await page.getByRole('button', { name: /新建流程图/ }).click()
    const graphDialog = page.getByRole('dialog', { name: '新建流程图' })
    await graphDialog.getByLabel('名称').fill(graphName)
    await graphDialog.getByLabel('描述').fill('Playwright runtime graph')
    await graphDialog.getByLabel('来源类型').fill('MANUAL')
    await graphDialog.getByLabel('流程图状态').fill('READY')
    await graphDialog.getByRole('button', { name: /创\s*建/ }).click()

    const graphEntry = page.getByRole('button', { name: graphName }).first()
    await expect(graphEntry).toBeVisible()
    await graphEntry.click()

    await expect(page).toHaveURL(/\/graphs\/\d+\/detail/)
    graphId = Number(page.url().match(/graphs\/(\d+)\/detail/)?.[1] ?? 0)
    expect(graphId).toBeGreaterThan(0)
    await expect(page.getByRole('heading', { name: graphName })).toBeVisible()

    await page.getByRole('button', { name: '进入编辑器' }).click()
    await expect(page).toHaveURL(new RegExp(`/graphs/${graphId}/editor`))
    await expect(page.getByText(/左侧维护图元资源/)).toBeVisible()

    await page.getByRole('button', { name: '退出' }).click()
    await expect(page).toHaveURL(/\/login/)
  } finally {
    if (token && graphId) {
      await request.delete(`${API_BASE_URL}/api/graphs/${graphId}`, {
        headers: { satoken: token },
      })
    }

    if (token && workspaceId) {
      await request.delete(`${API_BASE_URL}/api/workspaces/${workspaceId}`, {
        headers: { satoken: token },
      })
    }
  }
})
