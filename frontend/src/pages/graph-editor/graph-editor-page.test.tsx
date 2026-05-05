import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { render, screen } from '@testing-library/react'
import type { ReactNode } from 'react'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import GraphEditorPage from './graph-editor-page'

vi.mock('reactflow', () => ({
  Background: () => <div data-testid="reactflow-background" />,
  Controls: () => <div data-testid="reactflow-controls" />,
  MiniMap: () => <div data-testid="reactflow-minimap" />,
  default: ({ children }: { children: ReactNode }) => (
    <div data-testid="reactflow-canvas">{children}</div>
  ),
}))

vi.mock('../../api/graph', () => ({
  getGraphDetail: vi.fn(() =>
    Promise.resolve({
      graph: {
        id: 1,
        workspaceId: 1,
        name: '测试流程图',
        description: null,
        sourceType: 'MANUAL',
        graphStatus: 'DRAFT',
        graphVersion: 1,
        totalTime: 10,
        totalPrecision: 20,
        totalCost: 30,
        createdAt: '2026-05-05T00:00:00',
        updatedAt: '2026-05-05T00:00:00',
      },
      nodes: [
        {
          id: 1,
          graphId: 1,
          nodeCode: 'N1',
          nodeName: '开始',
          nodeDescription: null,
          equipmentId: null,
          timeCost: 1,
          precisionValue: 2,
          costValue: 3,
          sortNo: 1,
          createdAt: '2026-05-05T00:00:00',
          updatedAt: '2026-05-05T00:00:00',
        },
      ],
      paths: [],
      equipments: [],
      constraints: [],
    }),
  ),
}))

vi.mock('../../api/node', () => ({
  createNode: vi.fn(),
  deleteNode: vi.fn(),
  updateNode: vi.fn(),
}))

vi.mock('../../api/path', () => ({
  createPath: vi.fn(),
  deletePath: vi.fn(),
  updatePath: vi.fn(),
}))

vi.mock('../../api/equipment', () => ({
  createEquipment: vi.fn(),
  deleteEquipment: vi.fn(),
  updateEquipment: vi.fn(),
}))

vi.mock('../../api/constraint', () => ({
  createConstraint: vi.fn(),
  deleteConstraint: vi.fn(),
  updateConstraint: vi.fn(),
}))

function renderWithProviders() {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
    },
  })

  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter initialEntries={['/graphs/1/editor']}>
        <Routes>
          <Route path="/graphs/:graphId/editor" element={<GraphEditorPage />} />
        </Routes>
      </MemoryRouter>
    </QueryClientProvider>,
  )
}

describe('GraphEditorPage', () => {
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

  it('renders graph editor scaffold and canvas', async () => {
    renderWithProviders()

    expect(await screen.findByRole('heading', { name: '测试流程图' })).toBeInTheDocument()
    expect(screen.getByText('图资源')).toBeInTheDocument()
    expect(screen.getByText('图元管理')).toBeInTheDocument()
    expect(screen.getByTestId('reactflow-canvas')).toBeInTheDocument()
    expect(screen.getByText('属性面板')).toBeInTheDocument()
  })
})
