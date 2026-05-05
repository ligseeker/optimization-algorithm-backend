import PagePlaceholder from '../components/page-placeholder'

function SettingsPage() {
  return (
    <PagePlaceholder
      title="Settings"
      description="Environment diagnostics, profile conveniences, and future app preferences can live here."
      notes={[
        'Expose API base URL and runtime diagnostics when helpful.',
        'Keep this route lightweight until real settings requirements appear.',
      ]}
    />
  )
}

export default SettingsPage
