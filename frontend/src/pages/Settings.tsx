import React from 'react';
import { Card, Typography, Divider, Descriptions, Button, Switch, Space, Alert } from 'antd';
import { useAuthStore } from '../store/useAuthStore';
import { SafetyCertificateOutlined, UserOutlined, SettingOutlined } from '@ant-design/icons';

const { Title, Text } = Typography;

const SettingsPage: React.FC = () => {
  const { userInfo } = useAuthStore();

  return (
    <div style={{ padding: '0 0 24px 0' }}>
      <Title level={3} style={{ marginBottom: 24 }}>
        系统设置
      </Title>

      <Card
        title={
          <span>
            <UserOutlined /> 个人资料
          </span>
        }
        variant="outlined"
      >
        <Descriptions column={2} bordered size="small">
          <Descriptions.Item label="用户名">{userInfo?.username}</Descriptions.Item>
          <Descriptions.Item label="昵称">{userInfo?.nickname}</Descriptions.Item>
          <Descriptions.Item label="角色">
            <Text code>{userInfo?.roleCode}</Text>
          </Descriptions.Item>
          <Descriptions.Item label="用户ID">{userInfo?.userId}</Descriptions.Item>
        </Descriptions>
        <div style={{ marginTop: 16 }}>
          <Button type="primary">修改资料 (Sprint 6 计划)</Button>
        </div>
      </Card>

      <Card
        title={
          <span>
            <SettingOutlined /> 偏好设置
          </span>
        }
        variant="outlined"
        style={{ marginTop: 24 }}
      >
        <Space direction="vertical" style={{ width: '100%' }} size="middle">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <Text strong>自动保存</Text>
              <br />
              <Text type="secondary">在编辑器中修改后自动保存到云端</Text>
            </div>
            <Switch defaultChecked />
          </div>
          <Divider style={{ margin: '8px 0' }} />
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <Text strong>夜间模式</Text>
              <br />
              <Text type="secondary">切换系统视觉主题</Text>
            </div>
            <Switch disabled />
          </div>
        </Space>
      </Card>

      <Card
        title={
          <span>
            <SafetyCertificateOutlined /> 系统审计
          </span>
        }
        variant="outlined"
        style={{ marginTop: 24 }}
      >
        <Alert
          message="操作日志模块说明"
          description="后端已启用 AOP 日志记录（记录至 operation_log 表），当前前端版本暂未对接查询接口，审计详情请联系系统管理员通过数据库查询。"
          type="info"
          showIcon
        />
        <div style={{ marginTop: 16 }}>
          <Button disabled>查看操作日志 (待接口对接)</Button>
        </div>
      </Card>
    </div>
  );
};

export default SettingsPage;
