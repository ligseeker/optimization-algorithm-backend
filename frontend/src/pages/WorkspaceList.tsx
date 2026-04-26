import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Card,
  List,
  Button,
  Typography,
  Empty,
  message,
  Modal,
  Form,
  Input,
  Popconfirm,
} from 'antd';
import { PlusOutlined, ArrowRightOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { getWorkspaces, createWorkspace, updateWorkspace, deleteWorkspace } from '../api/workspace';
import type { Workspace } from '../api/workspace';

const { Title, Text } = Typography;

const WorkspaceList: React.FC = () => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingWorkspace, setEditingWorkspace] = useState<Workspace | null>(null);
  const [form] = Form.useForm();

  // 查询列表
  const { data: workspaces, isLoading } = useQuery({
    queryKey: ['workspaces'],
    queryFn: getWorkspaces,
  });

  // 创建/更新 Mutation
  const saveMutation = useMutation({
    mutationFn: (values: { name: string; description?: string }) => {
      if (editingWorkspace) {
        return updateWorkspace(editingWorkspace.id, values);
      }
      return createWorkspace(values);
    },
    onSuccess: () => {
      message.success(`${editingWorkspace ? '修改' : '创建'}成功`);
      setIsModalOpen(false);
      queryClient.invalidateQueries({ queryKey: ['workspaces'] });
    },
  });

  // 删除 Mutation
  const deleteMutation = useMutation({
    mutationFn: deleteWorkspace,
    onSuccess: () => {
      message.success('删除成功');
      queryClient.invalidateQueries({ queryKey: ['workspaces'] });
    },
  });

  const handleEnterWorkspace = (id: number) => {
    navigate(`/workspace/${id}/dashboard`);
  };

  const showModal = (workspace?: Workspace) => {
    if (workspace) {
      setEditingWorkspace(workspace);
      form.setFieldsValue(workspace);
    } else {
      setEditingWorkspace(null);
      form.resetFields();
    }
    setIsModalOpen(true);
  };

  const handleOk = () => {
    form.validateFields().then((values) => {
      saveMutation.mutate(values);
    });
  };

  return (
    <div style={{ maxWidth: 1200, margin: '0 auto', padding: '24px' }}>
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginBottom: 24,
        }}
      >
        <Title level={2}>工作空间管理</Title>
        <Button type="primary" icon={<PlusOutlined />} size="large" onClick={() => showModal()}>
          新建工作空间
        </Button>
      </div>

      <List
        grid={{ gutter: 24, xs: 1, sm: 2, md: 2, lg: 3, xl: 3, xxl: 3 }}
        dataSource={workspaces?.records || []}
        loading={isLoading}
        renderItem={(item) => (
          <List.Item>
            <Card
              hoverable
              actions={[
                <EditOutlined key="edit" title="编辑" onClick={() => showModal(item)} />,
                <Popconfirm
                  key="delete"
                  title="确定要删除这个工作空间吗？"
                  description="删除后，空间下的所有流程图和任务数据都将丢失。"
                  onConfirm={() => deleteMutation.mutate(item.id)}
                  okText="确定"
                  cancelText="取消"
                  okButtonProps={{ danger: true, loading: deleteMutation.isPending }}
                >
                  <DeleteOutlined title="删除" />
                </Popconfirm>,
                <ArrowRightOutlined
                  key="enter"
                  title="进入"
                  onClick={() => handleEnterWorkspace(item.id)}
                />,
              ]}
            >
              <Card.Meta
                title={item.name}
                description={
                  <div style={{ height: 60, overflow: 'hidden' }}>
                    <Text type="secondary">{item.description || '暂无描述'}</Text>
                  </div>
                }
              />
              <div style={{ marginTop: 16 }}>
                <Text type="secondary" style={{ fontSize: '12px' }}>
                  创建时间: {item.createdAt}
                </Text>
              </div>
            </Card>
          </List.Item>
        )}
        locale={{
          emptyText: <Empty description="暂无工作空间，请创建一个开始吧" />,
        }}
      />

      <Modal
        title={editingWorkspace ? '编辑工作空间' : '新建工作空间'}
        open={isModalOpen}
        onOk={handleOk}
        onCancel={() => setIsModalOpen(false)}
        confirmLoading={saveMutation.isPending}
        destroyOnClose
      >
        <Form form={form} layout="vertical" name="workspaceForm">
          <Form.Item
            name="name"
            label="空间名称"
            rules={[
              { required: true, message: '请输入工作空间名称' },
              { max: 50, message: '名称最多50个字符' },
            ]}
          >
            <Input placeholder="例如：研发一部项目" />
          </Form.Item>
          <Form.Item
            name="description"
            label="描述"
            rules={[{ max: 200, message: '描述最多200个字符' }]}
          >
            <Input.TextArea rows={4} placeholder="请输入关于该工作空间的简单描述" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default WorkspaceList;
