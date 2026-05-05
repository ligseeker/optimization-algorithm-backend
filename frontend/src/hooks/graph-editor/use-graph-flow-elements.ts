import type { Edge, Node } from 'reactflow'
import type { GraphDetailVO } from '../../types/graph'
import type { NodeVO } from '../../types/node'
import type { PathVO } from '../../types/path'

export type GraphFlowNodeData = NodeVO
export type GraphFlowEdgeData = PathVO

function getNodePosition(index: number) {
  return {
    x: (index % 4) * 220,
    y: Math.floor(index / 4) * 150,
  }
}

export function useGraphFlowElements(detail?: GraphDetailVO) {
  const nodeIds = new Set(detail?.nodes.map((node) => String(node.id)) ?? [])

  const nodes: Array<Node<GraphFlowNodeData>> =
    detail?.nodes.map((node, index) => ({
      id: String(node.id),
      data: {
        ...node,
        label: node.nodeName || node.nodeCode,
      },
      position: getNodePosition(index),
    })) ?? []

  const edges: Array<Edge<GraphFlowEdgeData>> =
    detail?.paths
      .filter((path) => nodeIds.has(String(path.startNodeId)) && nodeIds.has(String(path.endNodeId)))
      .map((path) => ({
        id: String(path.id),
        source: String(path.startNodeId),
        target: String(path.endNodeId),
        label: path.relationType || undefined,
        data: path,
      })) ?? []

  return { edges, nodes }
}
