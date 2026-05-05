import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import TasksPage from './tasks-page'

vi.mock('../../api/optimize-task', () => ({
  createOptimizeTask: vi.fn(),
  getOptimizeTaskPage: vi.fn(() =>
    Promise.resolve({
      records: [],
      pageNo: 1,
      pageSize: 10,
      total: 0,
    }),
  ),
}))

function renderWithProviders() {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
    },
  })

  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter>
        <TasksPage />
      </MemoryRouter>
    </QueryClientProvider>,
  )
}

describe('TasksPage', () => {
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

  it('renders task center controls', { timeout: 10000 }, async () => {
    renderWithProviders()

    expect(screen.getByRole('heading', { name: '任务中心' })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /提交优化任务/ })).toBeInTheDocument()
    expect(await screen.findByText('暂无优化任务')).toBeInTheDocument()
  })
})
