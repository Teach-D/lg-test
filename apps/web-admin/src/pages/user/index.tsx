import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Input, Table, Tag } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import { userApi } from '../../api/user';
import type { AdminUserResponse } from '../../api/user';

const ROLE_COLORS: Record<AdminUserResponse['role'], string> = {
  ADMIN: 'red',
  USER: 'blue',
};

export default function UserPage() {
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [search, setSearch] = useState('');

  const { data, isLoading } = useQuery({
    queryKey: ['admin-users', page, pageSize, search],
    queryFn: () =>
      userApi.getList(page, pageSize, search || undefined).then((r) => r.data.data),
  });

  const columns: ColumnsType<AdminUserResponse> = [
    { title: 'ID', dataIndex: 'id', width: 70 },
    { title: '이메일', dataIndex: 'email' },
    { title: '닉네임', dataIndex: 'nickname', width: 130 },
    {
      title: '보유 포인트',
      dataIndex: 'point',
      width: 130,
      render: (v: number) => `${v.toLocaleString()} P`,
    },
    {
      title: '역할',
      dataIndex: 'role',
      width: 90,
      render: (role: AdminUserResponse['role']) => (
        <Tag color={ROLE_COLORS[role]}>{role}</Tag>
      ),
    },
    {
      title: '가입일',
      dataIndex: 'createdAt',
      width: 170,
      render: (v: string) => dayjs(v).format('YYYY-MM-DD HH:mm'),
    },
  ];

  return (
    <div>
      <div
        style={{
          marginBottom: 16,
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
        }}
      >
        <h2 style={{ margin: 0 }}>사용자 관리</h2>
        <Input.Search
          placeholder="이메일 또는 닉네임 검색"
          allowClear
          style={{ width: 280 }}
          onSearch={(value) => {
            setSearch(value);
            setPage(0);
          }}
        />
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
    </div>
  );
}
