import { DownloadOutlined } from '@ant-design/icons'
import { useMutation } from '@tanstack/react-query'
import { Button, message } from 'antd'
import type { MouseEvent } from 'react'
import { exportGraphYaml } from '../../api/yaml'
import type { ID } from '../../types/common'
import { downloadTextFile } from '../../utils/download-text-file'

type GraphYamlExportButtonProps = {
  graphId: ID
  graphName?: string
  onClick?: (event: MouseEvent<HTMLElement>) => void
}

function getErrorMessage(error: unknown) {
  return error instanceof Error ? error.message : '导出失败，请稍后重试'
}

function getDefaultFileName(graphId: ID, graphName?: string) {
  const safeGraphName = graphName?.trim()
  return safeGraphName ? `${safeGraphName}.yaml` : `graph-${graphId}.yaml`
}

function GraphYamlExportButton({ graphId, graphName, onClick }: GraphYamlExportButtonProps) {
  const [messageApi, messageContextHolder] = message.useMessage()

  const exportMutation = useMutation({
    mutationFn: exportGraphYaml,
    onSuccess: (result) => {
      const fileName = result.fileName || getDefaultFileName(graphId, graphName)
      downloadTextFile(fileName, result.yamlContent || '', 'application/x-yaml')
      void messageApi.success('YAML 已导出')
    },
    onError: (error) => {
      void messageApi.error(getErrorMessage(error))
    },
  })

  return (
    <>
      {messageContextHolder}
      <Button
        icon={<DownloadOutlined />}
        loading={exportMutation.isPending}
        onClick={(event) => {
          onClick?.(event)
          exportMutation.mutate(graphId)
        }}
      >
        导出
      </Button>
    </>
  )
}

export default GraphYamlExportButton
