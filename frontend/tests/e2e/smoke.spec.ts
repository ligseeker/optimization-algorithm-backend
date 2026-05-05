import { expect, test } from '@playwright/test'

test('renders the login shell', async ({ page }) => {
  await page.goto('/login')

  await expect(page.getByRole('heading', { name: '登录系统' })).toBeVisible()
  await expect(page.getByRole('button', { name: /登\s*录/ })).toBeVisible()
})
