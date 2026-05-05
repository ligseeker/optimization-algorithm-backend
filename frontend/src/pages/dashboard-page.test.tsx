import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import DashboardPage from './dashboard-page'

vi.mock('../api/workspace', () => ({
  getWorkspacePage: vi.fn(() =>
    Promise.resolve({
      records: [],
      pageNo: 1,
      pageSize: 6,
      total: 0,
    }),
  ),
}))

vi.mock('../api/optimize-task', () => ({
  getOptimizeTaskPage: vi.fn(() =>
    Promise.resolve({
      records: [],
      pageNo: 1,
      pageSize: 12,
      total: 0,
    }),
  ),
}))

describe('DashboardPage', () => {
  beforeEach(() => {
    Object.defineProperty(window, 'matchMedia', {
      writable: true,
      value: (query: string) => ({
        matches: false,
        media: query,
        onchange: null,
        addListener: () => {},
        removeListener: () => {},
        addEventListener: () => {},
        removeEventListener: () => {},
        dispatchEvent: () => false,
      }),
    })
  })

  it('renders the dashboard console heading', () => {
    const queryClient = new QueryClient({
      defaultOptions: {
        queries: { retry: false },
      },
    })

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <DashboardPage />
        </MemoryRouter>
      </QueryClientProvider>,
    )

    expect(
      screen.getByRole('heading', { name: 'Industrial Control Surface' }),
    ).toBeInTheDocument()
  })
})
