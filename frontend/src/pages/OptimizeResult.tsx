import React, { useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import {
  Card,
  Row,
  Col,
  Statistic,
  Typography,
  Space,
  Button,
  Divider,
  List,
  Tag,
  Empty,
  Spin,
} from 'antd';
import {
  ArrowUpOutlined,
  ArrowDownOutlined,
  LeftOutlined,
  DownloadOutlined,
} from '@ant-design/icons';
import { getOptimizeResult } from '../api/task';
import * as echarts from 'echarts';
import mermaid from 'mermaid';

const { Title, Text } = Typography;

// 初始化 Mermaid
mermaid.initialize({ startOnLoad: true, theme: 'default' });

const OptimizeResult: React.FC = () => {
  const { taskId } = useParams<{ workspaceId: string; taskId: string }>();
  const navigate = useNavigate();
  const chartRef = useRef<HTMLDivElement>(null);
  const mermaidRef = useRef<HTMLDivElement>(null);

  const { data: result, isLoading } = useQuery({
    queryKey: ['optimizeResult', taskId],
    queryFn: () => getOptimizeResult(Number(taskId)) as Promise<any>,
    enabled: !!taskId,
  });

  // 渲染 ECharts 柱状图对比
  useEffect(() => {
    if (result && chartRef.current) {
      const myChart = echarts.init(chartRef.current);
      const { metrics } = result;

      const option = {
        title: { text: '优化前后核心指标对比' },
        tooltip: { trigger: 'axis' },
        legend: { data: ['优化前', '优化后'] },
        xAxis: { type: 'category', data: ['耗时', '精度 (x100)', '成本'] },
        yAxis: { type: 'value' },
        series: [
          {
            name: '优化前',
            type: 'bar',
            data: [metrics.time.before, metrics.precision.before * 100, metrics.cost.before],
            itemStyle: { color: '#bfbfbf' },
          },
          {
            name: '优化后',
            type: 'bar',
            data: [metrics.time.after, metrics.precision.after * 100, metrics.cost.after],
            itemStyle: { color: '#1677ff' },
          },
        ],
      };
      myChart.setOption(option);
      return () => myChart.dispose();
    }
  }, [result]);

  // 渲染 Mermaid mapCode
  useEffect(() => {
    if (result?.mapCode && mermaidRef.current) {
      mermaidRef.current.removeAttribute('data-processed');
      mermaid.contentLoaded();
    }
  }, [result]);

  if (isLoading) return <Spin size="large" style={{ display: 'block', margin: '100px auto' }} />;
  if (!result) return <Empty description="未找到优化结果" />;

  const { metrics, diff, mapCode } = result;

  const renderTrend = (change: number, isInverse = false) => {
    if (change === 0) return null;
    const isGood = isInverse ? change < 0 : change > 0;
    const color = isGood ? '#3f8600' : '#cf1322';
    const icon = isGood ? <ArrowDownOutlined /> : <ArrowUpOutlined />;
    return (
      <span style={{ color, marginLeft: 8 }}>
        {icon} {Math.abs(change).toFixed(2)}
      </span>
    );
  };

  return (
    <div style={{ padding: '0 0 24px 0' }}>
      <Card
        title={
          <Space>
            <Button icon={<LeftOutlined />} onClick={() => navigate(-1)} />
            <Title level={4} style={{ margin: 0 }}>
              优化任务结果分析
            </Title>
          </Space>
        }
        extra={<Button icon={<DownloadOutlined />}>导出YAML结果</Button>}
      >
        <Row gutter={16}>
          <Col span={8}>
            <Card variant="outlined">
              <Statistic
                title="总耗时"
                value={metrics.time.after}
                suffix={renderTrend(metrics.time.change, true)}
              />
              <Text type="secondary">原始值: {metrics.time.before}</Text>
            </Card>
          </Col>
          <Col span={8}>
            <Card variant="outlined">
              <Statistic
                title="总精度"
                value={metrics.precision.after}
                precision={4}
                suffix={renderTrend(metrics.precision.change, false)}
              />
              <Text type="secondary">原始值: {metrics.precision.before}</Text>
            </Card>
          </Col>
          <Col span={8}>
            <Card variant="outlined">
              <Statistic
                title="总成本"
                value={metrics.cost.after}
                suffix={renderTrend(metrics.cost.change, true)}
              />
              <Text type="secondary">原始值: {metrics.cost.before}</Text>
            </Card>
          </Col>
        </Row>

        <Divider />

        <Row gutter={24}>
          <Col span={12}>
            <div ref={chartRef} style={{ height: 350 }} />
          </Col>
          <Col span={12}>
            <Title level={5}>结构差异对比 (Diff)</Title>
            <Space direction="vertical" style={{ width: '100%' }}>
              <Card size="small" title="新增路径" variant="outlined">
                <List
                  size="small"
                  dataSource={diff.addedPaths}
                  renderItem={(item: any) => (
                    <List.Item>
                      <Tag color="green">ADD</Tag> {item.fromNodeCode} → {item.toNodeCode}
                    </List.Item>
                  )}
                />
              </Card>
              <Card size="small" title="移除路径" variant="outlined" style={{ marginTop: 16 }}>
                <List
                  size="small"
                  dataSource={diff.removedPaths}
                  renderItem={(item: any) => (
                    <List.Item>
                      <Tag color="red">REMOVE</Tag> {item.fromNodeCode} → {item.toNodeCode}
                    </List.Item>
                  )}
                />
              </Card>
            </Space>
          </Col>
        </Row>

        <Divider />

        <Title level={5}>优化目标拓扑预览</Title>
        <div style={{ background: '#f8f9fa', padding: 24, borderRadius: 8, textAlign: 'center' }}>
          <div key={mapCode} className="mermaid" ref={mermaidRef}>
            {mapCode}
          </div>
        </div>
      </Card>
    </div>
  );
};

export default OptimizeResult;
