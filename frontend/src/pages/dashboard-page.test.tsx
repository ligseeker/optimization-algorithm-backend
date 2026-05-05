import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import DashboardPage from './dashboard-page'

describe('DashboardPage', () => {
  it('renders the dashboard placeholder heading', () => {
    render(<DashboardPage />)

    expect(
      screen.getByRole('heading', { name: 'Dashboard' }),
    ).toBeInTheDocument()
  })
})
