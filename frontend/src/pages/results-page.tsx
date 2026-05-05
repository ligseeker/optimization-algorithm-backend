import PagePlaceholder from '../components/page-placeholder'

function ResultsPage() {
  return (
    <PagePlaceholder
      title="Results"
      description="This route will host optimization result detail views, diff output, mapCode, and ECharts based visual summaries."
      notes={[
        'Render before/after metrics and diff summaries.',
        'Support empty and failed result states.',
      ]}
    />
  )
}

export default ResultsPage
