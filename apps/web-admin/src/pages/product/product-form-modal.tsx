import { useEffect } from 'react';
import { Modal, Form, Input, InputNumber, Switch } from 'antd';
import type { Product, CreateProductParams } from '../../api/product';

interface Props {
  open: boolean;
  product: Product | null;
  loading: boolean;
  onSubmit: (values: CreateProductParams) => void;
  onCancel: () => void;
}

export default function ProductFormModal({
  open,
  product,
  loading,
  onSubmit,
  onCancel,
}: Props) {
  const [form] = Form.useForm<CreateProductParams>();
  const isEdit = product !== null;

  useEffect(() => {
    if (open) {
      form.setFieldsValue(
        product ?? { name: '', imageUrl: '', pointCost: 0, stock: 0, active: true },
      );
    }
  }, [open, product, form]);

  const handleOk = async () => {
    const values = await form.validateFields();
    onSubmit(values);
  };

  return (
    <Modal
      title={isEdit ? '상품 수정' : '상품 등록'}
      open={open}
      onOk={handleOk}
      onCancel={onCancel}
      confirmLoading={loading}
      destroyOnClose
    >
      <Form form={form} layout="vertical">
        <Form.Item
          name="name"
          label="상품명"
          rules={[
            { required: true, message: '상품명을 입력해주세요' },
            { max: 100, message: '100자 이하로 입력해주세요' },
          ]}
        >
          <Input />
        </Form.Item>
        <Form.Item name="imageUrl" label="이미지 URL">
          <Input />
        </Form.Item>
        <Form.Item
          name="pointCost"
          label="포인트 비용"
          rules={[{ required: true, message: '포인트 비용을 입력해주세요' }]}
        >
          <InputNumber min={0} style={{ width: '100%' }} />
        </Form.Item>
        <Form.Item
          name="stock"
          label="재고"
          rules={[{ required: true, message: '재고를 입력해주세요' }]}
        >
          <InputNumber min={0} style={{ width: '100%' }} />
        </Form.Item>
        <Form.Item name="active" label="활성 상태" valuePropName="checked">
          <Switch />
        </Form.Item>
      </Form>
    </Modal>
  );
}
