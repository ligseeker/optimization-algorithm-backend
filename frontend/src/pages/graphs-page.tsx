import PagePlaceholder from '../components/page-placeholder'

function GraphsPage() {
  return (
    <PagePlaceholder
      title="Graphs"
      description="This route is reserved for workspace-scoped graph lists and graph management actions."
      notes={[
        'List graphs by workspace with pagination and empty handling.',
        'Provide entry points to the graph editor, results, import, and export flows.',
      ]}
    />
  )
}

export default GraphsPage
