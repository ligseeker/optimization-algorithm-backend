import PagePlaceholder from '../components/page-placeholder'

function TasksPage() {
  return (
    <PagePlaceholder
      title="Tasks"
      description="Optimization task submission, polling, retry, and status tracking will be built on this route."
      notes={[
        'Reserve polling lifecycle cleanup for unmount.',
        'Expose task filters for workspace, graph, and status.',
      ]}
    />
  )
}

export default TasksPage
