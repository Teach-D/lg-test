import { useMutation } from '@tanstack/react-query';
import { Button, Card, Form, Input, message } from 'antd';
import { useNavigate } from 'react-router-dom';
import { authApi } from '../../api/auth';
import type { MockLoginParams } from '../../api/auth';

export default function LoginPage() {
  const navigate = useNavigate();
  const [messageApi, contextHolder] = message.useMessage();

  const { mutate: login, isPending } = useMutation({
    mutationFn: authApi.mockAdminLogin,
    onSuccess: (response) => {
      const { accessToken } = response.data.data;
      localStorage.setItem('accessToken', accessToken);
      navigate('/');
    },
    onError: () => {
      messageApi.error('로그인에 실패했습니다.');
    },
  });

  const handleSubmit = (values: MockLoginParams) => {
    login(values);
  };

  return (
    <>
      {contextHolder}
      <div
        style={{
          minHeight: '100vh',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          background: '#f0f2f5',
        }}
      >
        <Card style={{ width: 400 }}>
          <div style={{ textAlign: 'center', marginBottom: 32 }}>
            <h1 style={{ fontSize: 24, fontWeight: 700, margin: 0 }}>Point Roulette</h1>
            <p style={{ color: '#888', marginTop: 8 }}>관리자 로그인</p>
          </div>
          <Form layout="vertical" onFinish={handleSubmit} autoComplete="off">
            <Form.Item
              label="닉네임"
              name="nickname"
              rules={[{ required: true, message: '닉네임을 입력하세요.' }]}
            >
              <Input placeholder="관리자 닉네임" size="large" />
            </Form.Item>
            <Form.Item style={{ marginBottom: 0 }}>
              <Button
                type="primary"
                htmlType="submit"
                size="large"
                block
                loading={isPending}
              >
                관리자 로그인
              </Button>
            </Form.Item>
          </Form>
        </Card>
      </div>
    </>
  );
}
