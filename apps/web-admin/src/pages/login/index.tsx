import { useMutation } from '@tanstack/react-query';
import { Button, Card, Form, Input, message } from 'antd';
import { useNavigate } from 'react-router-dom';
import { authApi } from '../../api/auth';
import type { LoginParams } from '../../api/auth';

export default function LoginPage() {
  const navigate = useNavigate();
  const [messageApi, contextHolder] = message.useMessage();

  const { mutate: login, isPending } = useMutation({
    mutationFn: authApi.login,
    onSuccess: (response) => {
      const { accessToken } = response.data.data;
      localStorage.setItem('accessToken', accessToken);
      navigate('/');
    },
    onError: () => {
      messageApi.error('이메일 또는 비밀번호가 올바르지 않습니다.');
    },
  });

  const handleSubmit = (values: LoginParams) => {
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
              label="이메일"
              name="email"
              rules={[
                { required: true, message: '이메일을 입력하세요.' },
                { type: 'email', message: '올바른 이메일 형식이 아닙니다.' },
              ]}
            >
              <Input placeholder="admin@example.com" size="large" />
            </Form.Item>
            <Form.Item
              label="비밀번호"
              name="password"
              rules={[{ required: true, message: '비밀번호를 입력하세요.' }]}
            >
              <Input.Password placeholder="비밀번호" size="large" />
            </Form.Item>
            <Form.Item style={{ marginBottom: 0 }}>
              <Button
                type="primary"
                htmlType="submit"
                size="large"
                block
                loading={isPending}
              >
                로그인
              </Button>
            </Form.Item>
          </Form>
        </Card>
      </div>
    </>
  );
}
