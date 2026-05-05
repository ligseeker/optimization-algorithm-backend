import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import WorkspacesPage from './workspaces-page'

vi.mock('../../api/workspace', () => ({
  createWorkspace: vi.fn(),
  deleteWorkspace: vi.fn(),
  getWorkspacePage: vi.fn(() =>
    Promise.resolve({
      records: [],
      pageNo: 1,
      pageSize: 10,
      total: 0,
    }),
  ),
  updateWorkspace: vi.fn(),
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
        <WorkspacesPage />
      </MemoryRouter>
    </QueryClientProvider>,
  )
}

describe('WorkspacesPage', () => {
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

  it('renders workspace management controls', async () => {
    renderWithProviders()

    expect(screen.getByRole('heading', { name: '工作空间舰队' })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /新建工作空间/ })).toBeInTheDocument()
    expect(await screen.findByText('暂无工作空间')).toBeInTheDocument()
  })
})
