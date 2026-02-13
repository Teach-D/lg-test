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
} from 'antd';
import dayjs from 'dayjs';
import { budgetApi } from '../../api/budget';
import type { SetBudgetParams, PeriodType, Budget } from '../../api/budget';

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

  const { data: summary } = useQuery({
    queryKey: ['budget-summary'],
    queryFn: () => budgetApi.getSummary().then((r) => r.data.data),
  });

  const mutation = useMutation({
    mutationFn: (params: SetBudgetParams) => budgetApi.set(params),
    onSuccess: () => {
      message.success('예산이 설정되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['budget-summary'] });
    },
  });

  const handleSubmit = (values: { periodType: PeriodType; periodDate: dayjs.Dayjs; limitAmount: number }) => {
    const periodDate = values.periodType === 'DAILY'
      ? values.periodDate.format('YYYY-MM-DD')
      : values.periodDate.startOf('month').format('YYYY-MM-DD');

    mutation.mutate({
      periodType: values.periodType,
      periodDate,
      limitAmount: values.limitAmount,
    });
  };

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

      <Card title="예산 설정">
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
            <Button type="primary" htmlType="submit" loading={mutation.isPending}>
              설정
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
}
