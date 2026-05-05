import PagePlaceholder from '../components/page-placeholder'

function WorkspacesPage() {
  return (
    <PagePlaceholder
      title="Workspaces"
      description="Workspace CRUD, loading states, empty states, and delete confirmations will land here."
      notes={[
        'Connect to /api/workspaces via the dedicated API layer.',
        'Add filters, create/edit forms, and deletion confirmation flow.',
      ]}
    />
  )
}

export default WorkspacesPage
