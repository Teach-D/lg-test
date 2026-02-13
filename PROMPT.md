# AI 대화 기록

이 문서는 AI와의 주요 대화 내용과 결정 사항을 기록합니다.

---

## 2026-02-12 | 프로젝트 초기 설정

- 결정: git init, .gitignore, PROMPT.md, CLAUDE.md 생성
- 관련 파일: .gitignore, PROMPT.md, CLAUDE.md

## 2026-02-12 | Git 전략 확정

- 결정:
  - 브랜치 전략: GitHub Flow + dev (main/dev/feature/*)
  - 브랜치 네이밍: type/설명 (feat/, fix/, refactor/, chore/)
  - 커밋 메시지: Conventional Commits + 한글 본문
  - 머지 전략: Squash Merge
  - 버전 관리: 통합 SemVer (v0.1.0)
- 이유: 1인 개발 모노레포에 적합한 단순함과 체계성의 균형
- 관련 파일: CLAUDE.md

## 2026-02-12 | 문서 규칙 확정

- 결정:
  - 필수 문서: README.md, PROMPT.md, Swagger, CLAUDE.md, CHANGELOG.md
  - 작성 언어: 모든 문서 한국어
  - README 구조: 소개 → 기술 스택 → 프로젝트 구조 → 시작하기 → 환경변수 → 배포
  - PROMPT.md: 날짜 | 제목 + 결정/이유/관련파일 형식
  - Swagger: 코드 기반 자동 생성
  - 코드 주석: 공개 API는 Doc 주석, 내부는 Why만
  - 문서 업데이트: 코드 변경과 동시에 (같은 커밋)
- 이유: 1인 개발에서 문서가 뒤처지지 않도록 코드와 동시 업데이트, 한국어 통일로 일관성 확보
- 관련 파일: CLAUDE.md

## 2026-02-12 | 코드 컨벤션 확정

- 결정:
  - 네이밍: 변수 camelCase / 클래스 PascalCase / 상수 UPPER_SNAKE / 파일 kebab-case / DB snake_case / API kebab-case
  - 디렉토리 구조: 기능별 (Feature/Domain)
  - 포맷팅: EditorConfig + Prettier (스페이스 2칸, 세미콜론, 작은따옴표, 100자)
  - 린터: ESLint + Prettier 통합 (strict 모드)
  - 코드 품질 자동화: husky + lint-staged + commitlint
  - 에러 처리: 커스텀 에러 클래스 + 에러 코드, 통일된 API 응답 형식
- 이유: 업계 표준(Airbnb, Google Style Guide) 기반, 1인 개발이라도 자동화로 품질 유지
- 관련 파일: CLAUDE.md