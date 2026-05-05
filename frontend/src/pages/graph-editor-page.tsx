import PagePlaceholder from '../components/page-placeholder'

function GraphEditorPage() {
  return (
    <PagePlaceholder
      title="Graph Editor"
      description="React Flow based canvas editing, node/path/equipment/constraint CRUD, and detail mapping will be implemented here."
      notes={[
        'Use /api/graphs/{graphId}/detail as the aggregate load path.',
        'Map editor interactions back to resource-oriented CRUD endpoints.',
      ]}
    />
  )
}

export default GraphEditorPage
