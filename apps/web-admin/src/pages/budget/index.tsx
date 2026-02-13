import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Card,
  Row,
  Col,
  Statistic,
  Form,
  InputNumber,
  DatePicker,
  Select,
  Button,
  message,
  Progress,
  Table,
  Tag,
  Popconfirm,
} from 'antd';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import { budgetApi } from '../../api/budget';
import type { SetBudgetParams, PeriodType, Budget } from '../../api/budget';
import { rouletteApi } from '../../api/roulette';
import type { AdminSpin } from '../../api/roulette';

function BudgetCard({ title, budget }: { title: string; budget: Budget | null }) {
  if (!budget) {
    return (
      <Card title={title}>
        <Statistic title="설정됨" value="미설정" />
      </Card>
    );
  }

  const usagePercent = budget.limitAmount > 0
    ? Math.round((budget.spentAmount / budget.limitAmount) * 100)
    : 0;

  return (
    <Card title={title}>
      <Row gutter={16}>
        <Col span={8}>
          <Statistic title="예산 한도" value={budget.limitAmount} suffix="P" />
        </Col>
        <Col span={8}>
          <Statistic title="소진액" value={budget.spentAmount} suffix="P" />
        </Col>
        <Col span={8}>
          <Statistic title="잔여" value={budget.remainingAmount} suffix="P" />
        </Col>
      </Row>
      <Progress percent={usagePercent} style={{ marginTop: 16 }} />
    </Card>
  );
}

export default function BudgetPage() {
  const queryClient = useQueryClient();
  const [form] = Form.useForm<{ periodType: PeriodType; periodDate: dayjs.Dayjs; limitAmount: number }>();
  const [spinPage, setSpinPage] = useState(0);
  const [spinPageSize, setSpinPageSize] = useState(10);

  const { data: summary } = useQuery({
    queryKey: ['budget-summary'],
    queryFn: () => budgetApi.getSummary().then((r) => r.data.data),
  });

  const { data: spins, isLoading: spinsLoading } = useQuery({
    queryKey: ['admin-spins', spinPage, spinPageSize],
    queryFn: () =>
      rouletteApi.getSpins(spinPage, spinPageSize).then((r) => r.data.data),
  });

  const budgetMutation = useMutation({
    mutationFn: (params: SetBudgetParams) => budgetApi.set(params),
    onSuccess: () => {
      message.success('예산이 설정되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['budget-summary'] });
    },
  });

  const cancelMutation = useMutation({
    mutationFn: (id: number) => rouletteApi.cancelSpin(id),
    onSuccess: () => {
      message.success('스핀이 취소되었습니다. 포인트가 회수되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['admin-spins'] });
      queryClient.invalidateQueries({ queryKey: ['budget-summary'] });
    },
    onError: () => {
      message.error('스핀 취소에 실패했습니다.');
    },
  });

  const handleSubmit = (values: { periodType: PeriodType; periodDate: dayjs.Dayjs; limitAmount: number }) => {
    const periodDate = values.periodType === 'DAILY'
      ? values.periodDate.format('YYYY-MM-DD')
      : values.periodDate.startOf('month').format('YYYY-MM-DD');

    budgetMutation.mutate({
      periodType: values.periodType,
      periodDate,
      limitAmount: values.limitAmount,
    });
  };

  const spinColumns: ColumnsType<AdminSpin> = [
    { title: 'ID', dataIndex: 'id', width: 60 },
    { title: '사용자 ID', dataIndex: 'userId', width: 90 },
    {
      title: '보상',
      dataIndex: 'rewardPoint',
      width: 100,
      render: (v: number) => `${v.toLocaleString()} P`,
    },
    {
      title: '상태',
      dataIndex: 'cancelled',
      width: 80,
      render: (cancelled: boolean) =>
        cancelled
          ? <Tag color="default">취소됨</Tag>
          : <Tag color="green">정상</Tag>,
    },
    {
      title: '일시',
      dataIndex: 'createdAt',
      width: 170,
      render: (v: string) => new Date(v).toLocaleString('ko-KR'),
    },
    {
      title: '관리',
      width: 100,
      render: (_, record) =>
        record.cancelled ? (
          <span style={{ color: '#999' }}>취소됨</span>
        ) : (
          <Popconfirm
            title="스핀 취소"
            description={`${record.rewardPoint}P가 회수됩니다. 취소하시겠습니까?`}
            onConfirm={() => cancelMutation.mutate(record.id)}
            okText="취소 실행"
            cancelText="닫기"
          >
            <Button type="link" danger size="small">
              취소
            </Button>
          </Popconfirm>
        ),
    },
  ];

  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>예산 관리</h2>

      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={12}>
          <BudgetCard title="오늘 예산" budget={summary?.dailyBudget ?? null} />
        </Col>
        <Col span={12}>
          <BudgetCard title="이번달 예산" budget={summary?.monthlyBudget ?? null} />
        </Col>
      </Row>

      <Card title="예산 설정" style={{ marginBottom: 24 }}>
        <Form
          form={form}
          layout="inline"
          onFinish={handleSubmit}
          initialValues={{ periodType: 'DAILY', periodDate: dayjs(), limitAmount: 0 }}
        >
          <Form.Item name="periodType" label="기간 유형">
            <Select style={{ width: 120 }}>
              <Select.Option value="DAILY">일별</Select.Option>
              <Select.Option value="MONTHLY">월별</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="periodDate" label="날짜">
            <DatePicker />
          </Form.Item>
          <Form.Item
            name="limitAmount"
            label="예산 한도 (P)"
            rules={[{ required: true, message: '예산 한도를 입력해주세요' }]}
          >
            <InputNumber min={0} style={{ width: 160 }} />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={budgetMutation.isPending}>
              설정
            </Button>
          </Form.Item>
        </Form>
      </Card>

      <Card title="룰렛 참여 이력">
        <Table
          rowKey="id"
          columns={spinColumns}
          dataSource={spins?.content}
          loading={spinsLoading}
          pagination={{
            current: spinPage + 1,
            pageSize: spinPageSize,
            total: spins?.totalElements,
            onChange: (p, s) => {
              setSpinPage(p - 1);
              setSpinPageSize(s);
            },
          }}
        />
      </Card>
    </div>
  );
}
