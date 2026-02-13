---
name: coding
description: 코드 작성 시 품질을 유지하기 위한 규칙 스킬. 코드를 작성, 수정, 리뷰할 때 자동으로 적용한다. 함수 규칙, 타입 안전성, 에러 처리, 테스트, 커밋 단위, 코드 리뷰, 임포트 정렬, 보안 규칙을 포함한다.
---

# Coding Rules

코드 작성 시 아래 규칙을 적용한다.

## 코드 작성 원칙

- **KISS**: 단순하게 작성한다. 복잡한 추상화보다 명확한 코드를 우선한다.
- **YAGNI**: 현재 필요한 것만 구현한다. 미래 요구사항을 예측하지 않는다.

## 함수 규칙

| 규칙 | 기준 |
|---|---|
| 함수 길이 | 30줄 이하 |
| 파라미터 수 | 3개 이하 (초과 시 객체로 묶기) |
| 중첩 깊이 | 2단계 이하 (early return으로 줄이기) |

```typescript
// Bad
function process(a: string, b: number, c: boolean, d: string) {
  if (a) {
    if (b > 0) {
      if (c) { /* ... */ }
    }
  }
}

// Good
interface ProcessInput {
  name: string;
  count: number;
  isActive: boolean;
  category: string;
}

function process(input: ProcessInput) {
  if (!input.name) return;
  if (input.count <= 0) return;
  if (!input.isActive) return;
  // ...
}
```

## 타입 안전성

- TypeScript strict 모드 사용
- `any` 사용 금지 — `unknown`으로 대체 후 타입 좁히기
- 타입 추론이 명확하면 명시적 타입 생략 가능

## 에러 처리

- try-catch는 최소 범위로 감싼다
- 빈 catch 블록 금지 — 반드시 로깅 또는 재throw
- 로깅은 구조화(JSON) 형태로 한다

```typescript
// Bad
try {
  await saveUser(user);
  await sendEmail(user);
  await updateLog(user);
} catch (e) {}

// Good
try {
  await saveUser(user);
} catch (error) {
  logger.error({ error, userId: user.id, action: 'saveUser' });
  throw error;
}
```

## 테스트 규칙

- 프레임워크: **Vitest**
- 필수 범위: **서비스 계층**
- 패턴: **AAA** (Arrange-Act-Assert)
- 네이밍: **한글 서술형**
- 커버리지 목표: **70%**

```typescript
describe('RouletteService', () => {
  it('포인트가 부족하면 에러를 던진다', () => {
    // Arrange
    const user = createUser({ point: 0 });

    // Act & Assert
    expect(() => service.spin(user)).toThrow(BusinessException);
  });

  it('당첨 확률에 따라 포인트를 지급한다', () => {
    // Arrange
    const user = createUser({ point: 100 });

    // Act
    const result = service.spin(user);

    // Assert
    expect(result.reward).toBeGreaterThanOrEqual(0);
  });
});
```

## 커밋 단위

- 하나의 커밋 = 하나의 동작하는 기능 또는 수정
- 관련 없는 변경은 분리한다
- 테스트와 구현은 같은 커밋에 포함한다

## 코드 리뷰 체크리스트

코드 작성 후 아래 항목을 확인한다.

- [ ] 네이밍이 의도를 표현하는가
- [ ] 사이드 이펙트가 없는가
- [ ] 에러 케이스를 처리했는가
- [ ] 보안 취약점이 없는가 (인젝션, XSS 등)
- [ ] 테스트가 작성되었는가 (서비스 계층)

## 임포트 규칙

그룹별 정렬, 그룹 사이 빈 줄:

```typescript
// 1. 외부 패키지
import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';

// 2. 내부 모듈 (절대경로)
import { BusinessException } from '@/common/exceptions';

// 3. 상대경로
import { RouletteRepository } from './roulette.repository';
```

## 보안 규칙

- 입력 검증: 컨트롤러 진입점에서 `class-validator`로 수행
- 시크릿 관리: `.env` + `.gitignore`
- DB 쿼리: ORM 필수, raw query 금지