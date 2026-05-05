import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { render, screen } from '@testing-library/react'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import OptimizeResultPage from './optimize-result-page'

vi.mock('echarts', () => ({
  init: vi.fn(() => ({
    dispose: vi.fn(),
    resize: vi.fn(),
    setOption: vi.fn(),
  })),
}))

vi.mock('../../api/optimize-task', () => ({
  getOptimizeTaskDetail: vi.fn(() =>
    Promise.resolve({
      id: 1,
      taskNo: 'TASK-001',
      taskStatus: 'SUCCESS',
    }),
  ),
  getOptimizeResult: vi.fn(() =>
    Promise.resolve({
      id: 10,
      taskId: 1,
      workspaceId: 1,
      sourceGraphId: 2,
      resultName: 'Result Demo',
      resultGraph: { nodes: [] },
      diff: { changed: [] },
      mapCode: 'map-code-demo',
      totalTimeBefore: 10,
      totalPrecisionBefore: 20,
      totalCostBefore: 30,
      totalTimeAfter: 8,
      totalPrecisionAfter: 24,
      totalCostAfter: 25,
      scoreRatio: 1.2,
      createdAt: '2026-05-05 10:00:00',
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
      <MemoryRouter initialEntries={['/tasks/1/result']}>
        <Routes>
          <Route path="/tasks/:taskId/result" element={<OptimizeResultPage />} />
        </Routes>
      </MemoryRouter>
    </QueryClientProvider>,
  )
}

describe('OptimizeResultPage', () => {
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

  it('renders optimize result metrics and safe code blocks', async () => {
    renderWithProviders()

    expect(await screen.findByRole('heading', { name: '优化结果' })).toBeInTheDocument()
    expect(await screen.findByText('Result Demo')).toBeInTheDocument()
    expect(screen.getByText('指标对比')).toBeInTheDocument()
    expect(screen.getByText('map-code-demo')).toBeInTheDocument()
  })
})
