import { useEffect, useRef } from 'react'
import * as echarts from 'echarts'
import type { OptimizeResultVO } from '../../types/optimize-task'

type MetricComparisonChartProps = {
  result: Pick<
    OptimizeResultVO,
    | 'totalTimeBefore'
    | 'totalTimeAfter'
    | 'totalPrecisionBefore'
    | 'totalPrecisionAfter'
    | 'totalCostBefore'
    | 'totalCostAfter'
  >
}

function MetricComparisonChart({ result }: MetricComparisonChartProps) {
  const chartRef = useRef<HTMLDivElement | null>(null)

  useEffect(() => {
    if (!chartRef.current) {
      return undefined
    }

    const chart = echarts.init(chartRef.current)
    const option: echarts.EChartsOption = {
      color: ['#2563eb', '#16a34a'],
      grid: { left: 48, right: 24, top: 40, bottom: 32 },
      legend: { data: ['优化前', '优化后'] },
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: ['总耗时', '总精度', '总成本'],
      },
      yAxis: { type: 'value' },
      series: [
        {
          name: '优化前',
          type: 'bar',
          data: [
            result.totalTimeBefore,
            result.totalPrecisionBefore,
            result.totalCostBefore,
          ],
        },
        {
          name: '优化后',
          type: 'bar',
          data: [
            result.totalTimeAfter,
            result.totalPrecisionAfter,
            result.totalCostAfter,
          ],
        },
      ],
    }

    chart.setOption(option)

    const handleResize = () => chart.resize()
    window.addEventListener('resize', handleResize)

    return () => {
      window.removeEventListener('resize', handleResize)
      chart.dispose()
    }
  }, [result])

  return <div ref={chartRef} style={{ height: 320, width: '100%' }} />
}

export default MetricComparisonChart
