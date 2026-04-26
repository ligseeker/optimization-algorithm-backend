import React, { useCallback, useMemo, useEffect } from 'react';
import ReactFlow, {
  Background,
  Controls,
  addEdge,
  useNodesState,
  useEdgesState,
  MarkerType,
} from 'reactflow';
import type { Connection, Edge } from 'reactflow';
import 'reactflow/dist/style.css';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import type { ProcessNode } from '../../api/node';
import { createPath, deletePath } from '../../api/path';
import type { ProcessPath } from '../../api/path';

interface FlowCanvasProps {
  graphId: number;
  initialNodes: ProcessNode[];
  initialPaths: ProcessPath[];
}

const FlowCanvas: React.FC<FlowCanvasProps> = ({ graphId, initialNodes, initialPaths }) => {
  const queryClient = useQueryClient();

  // 将后端数据转换为 React Flow 格式
  const formattedNodes = useMemo(() => {
    return initialNodes.map((node, index) => ({
      id: node.id.toString(),
      type: 'default',
      data: {
        label: node.nodeName,
        ...node,
      },
      // 解决 Math.random 非纯函数问题：使用索引或固定算法分配初始位置
      position: {
        x: node.positionX ?? (index % 5) * 200,
        y: node.positionY ?? Math.floor(index / 5) * 150,
      },
    }));
  }, [initialNodes]);

  const formattedEdges = useMemo(() => {
    return initialPaths.map((path) => ({
      id: path.id.toString(),
      source: path.startNodeId.toString(),
      target: path.endNodeId.toString(),
      label: path.relationType,
      markerEnd: { type: MarkerType.ArrowClosed },
    }));
  }, [initialPaths]);

  const [nodes, setNodes, onNodesChange] = useNodesState(formattedNodes);
  const [edges, setEdges, onEdgesChange] = useEdgesState(formattedEdges);

  // 同步初始数据变化
  useEffect(() => {
    setNodes(formattedNodes);
    setEdges(formattedEdges);
  }, [formattedNodes, formattedEdges, setNodes, setEdges]);

  // API Mutations
  const addEdgeMutation = useMutation({
    mutationFn: (connection: Connection) =>
      createPath(graphId, {
        startNodeId: Number(connection.source),
        endNodeId: Number(connection.target),
        relationType: 'NORMAL',
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['graphDetail', graphId.toString()] });
    },
  });

  const deleteEdgeMutation = useMutation({
    mutationFn: (edgeId: number) => deletePath(graphId, edgeId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['graphDetail', graphId.toString()] });
    },
  });

  const onConnect = useCallback(
    (params: Connection) => {
      setEdges((eds) => addEdge(params, eds));
      addEdgeMutation.mutate(params);
    },
    [setEdges, addEdgeMutation],
  );

  const onEdgesDelete = useCallback(
    (edgesToDelete: Edge[]) => {
      edgesToDelete.forEach((edge) => {
        deleteEdgeMutation.mutate(Number(edge.id));
      });
    },
    [deleteEdgeMutation],
  );

  return (
    <div
      style={{
        width: '100%',
        height: 'calc(100vh - 200px)',
        border: '1px solid #d9d9d9',
        borderRadius: 8,
      }}
    >
      <ReactFlow
        nodes={nodes}
        edges={edges}
        onNodesChange={onNodesChange}
        onEdgesChange={onEdgesChange}
        onConnect={onConnect}
        onEdgesDelete={onEdgesDelete}
        fitView
      >
        <Background />
        <Controls />
      </ReactFlow>
    </div>
  );
};

export default FlowCanvas;
