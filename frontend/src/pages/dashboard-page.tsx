import PagePlaceholder from '../components/page-placeholder'

function DashboardPage() {
  return (
    <PagePlaceholder
      title="Dashboard"
      description="This page will become the entry point for workspace summaries, task activity, and navigation shortcuts."
      notes={[
        'Integrate query client driven summary cards.',
        'Add quick links for workspace, graph, task, and result modules.',
      ]}
    />
  )
}

export default DashboardPage
