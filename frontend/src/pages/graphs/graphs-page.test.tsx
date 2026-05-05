import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { render, screen } from '@testing-library/react'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import GraphsPage from './graphs-page'

vi.mock('../../api/graph', () => ({
  createGraph: vi.fn(),
  deleteGraph: vi.fn(),
  getGraphPage: vi.fn(() =>
    Promise.resolve({
      records: [],
      pageNo: 1,
      pageSize: 10,
      total: 0,
    }),
  ),
  updateGraph: vi.fn(),
}))

vi.mock('../../api/workspace', () => ({
  getWorkspaceDetail: vi.fn(() =>
    Promise.resolve({
      id: 1,
      ownerUserId: 1,
      name: '默认工作空间',
      description: null,
      status: 1,
      createdAt: '2026-05-05T00:00:00',
      updatedAt: '2026-05-05T00:00:00',
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
      <MemoryRouter initialEntries={['/workspaces/1/graphs']}>
        <Routes>
          <Route path="/workspaces/:workspaceId/graphs" element={<GraphsPage />} />
        </Routes>
      </MemoryRouter>
    </QueryClientProvider>,
  )
}

describe('GraphsPage', () => {
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

  it('renders graph management controls', async () => {
    renderWithProviders()

    expect(screen.getByRole('heading', { name: '流程图' })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /新建流程图/ })).toBeInTheDocument()
    expect(await screen.findByText('暂无流程图')).toBeInTheDocument()
  })
})
