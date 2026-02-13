import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Table, Button, Space, Tag, Popconfirm, message } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { productApi } from '../../api/product';
import type { Product, CreateProductParams } from '../../api/product';
import ProductFormModal from './product-form-modal';

export default function ProductPage() {
  const queryClient = useQueryClient();
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingProduct, setEditingProduct] = useState<Product | null>(null);

  const { data, isLoading } = useQuery({
    queryKey: ['products', page, pageSize],
    queryFn: () => productApi.getList(page, pageSize).then((r) => r.data.data),
  });

  const createMutation = useMutation({
    mutationFn: (params: CreateProductParams) => productApi.create(params),
    onSuccess: () => {
      message.success('상품이 등록되었습니다.');
      closeModal();
      queryClient.invalidateQueries({ queryKey: ['products'] });
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, params }: { id: number; params: CreateProductParams }) =>
      productApi.update(id, params),
    onSuccess: () => {
      message.success('상품이 수정되었습니다.');
      closeModal();
      queryClient.invalidateQueries({ queryKey: ['products'] });
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => productApi.delete(id),
    onSuccess: () => {
      message.success('상품이 삭제되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['products'] });
    },
  });

  const openCreateModal = () => {
    setEditingProduct(null);
    setModalOpen(true);
  };

  const openEditModal = (product: Product) => {
    setEditingProduct(product);
    setModalOpen(true);
  };

  const closeModal = () => {
    setModalOpen(false);
    setEditingProduct(null);
  };

  const handleSubmit = (values: CreateProductParams) => {
    if (editingProduct) {
      updateMutation.mutate({ id: editingProduct.id, params: values });
    } else {
      createMutation.mutate(values);
    }
  };

  const columns: ColumnsType<Product> = [
    { title: 'ID', dataIndex: 'id', width: 60 },
    { title: '상품명', dataIndex: 'name' },
    {
      title: '포인트 비용',
      dataIndex: 'pointCost',
      render: (v: number) => `${v.toLocaleString()} P`,
    },
    { title: '재고', dataIndex: 'stock' },
    {
      title: '상태',
      dataIndex: 'active',
      render: (active: boolean) => (
        <Tag color={active ? 'green' : 'default'}>
          {active ? '활성' : '비활성'}
        </Tag>
      ),
    },
    {
      title: '관리',
      render: (_, record) => (
        <Space>
          <Button size="small" onClick={() => openEditModal(record)}>
            수정
          </Button>
          <Popconfirm
            title="정말 삭제하시겠습니까?"
            onConfirm={() => deleteMutation.mutate(record.id)}
          >
            <Button size="small" danger>
              삭제
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
        <h2 style={{ margin: 0 }}>상품 관리</h2>
        <Button type="primary" icon={<PlusOutlined />} onClick={openCreateModal}>
          상품 등록
        </Button>
      </div>
      <Table
        rowKey="id"
        columns={columns}
        dataSource={data?.content}
        loading={isLoading}
        pagination={{
          current: page + 1,
          pageSize,
          total: data?.totalElements,
          onChange: (p, s) => {
            setPage(p - 1);
            setPageSize(s);
          },
        }}
      />
      <ProductFormModal
        open={modalOpen}
        product={editingProduct}
        loading={createMutation.isPending || updateMutation.isPending}
        onSubmit={handleSubmit}
        onCancel={closeModal}
      />
    </div>
  );
}
