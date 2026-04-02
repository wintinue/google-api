# GoogleApi Workspace

이 저장소는 두 영역을 함께 관리합니다.

1. [`frontend/`](frontend/): React 19 + Vite 8 + TypeScript 6 기반 웹 프론트엔드 모음
2. [`backend/`](backend/): Google OAuth 및 Google API 연동용 Kotlin/Ktor 백엔드

원래는 Google API 연동 서버 중심 저장소였고, 이후 정적 웹사이트들을 같은 레포 안에서 함께 관리하는 구조로 확장되었습니다. 현재는 프론트와 백엔드가 분리된 워크스페이스 형태입니다.

## Current Status

프론트엔드는 단일 사이트가 아니라 `apps/` 아래 여러 독립 사이트를 두는 구조입니다.

- [`frontend/apps/donggeurami/`](frontend/apps/donggeurami/): 동그라미분식 랜딩 페이지
- [`frontend/apps/testy-market/`](frontend/apps/testy-market/): 문서형 로컬 마켓 랜딩 페이지

각 사이트는 자체 `package.json`, `vite.config.ts`, `vercel.json`을 갖고 있어서 Vercel에서 앱 폴더 단위로 바로 배포할 수 있습니다.

백엔드는 Google OAuth 인증, 토큰 저장, Google REST API 프록시 호출, Business Profile category 조회, Merchant product 관련 API를 제공하는 로컬 Ktor 서버입니다.

## Workspace Structure

- [`frontend/`](frontend/): 프론트엔드 워크스페이스와 루트 허브 페이지
- [`frontend/apps/donggeurami/`](frontend/apps/donggeurami/): 동그라미분식 독립 Vite 앱
- [`frontend/apps/testy-market/`](frontend/apps/testy-market/): testy market 독립 Vite 앱
- [`backend/`](backend/): Kotlin/Ktor 기반 Google API 서버

## Quick Start

프론트 전체 허브 실행:

```bash
cd frontend
npm install
npm run dev
```

개별 프론트 앱 실행:

```bash
cd frontend/apps/donggeurami
npm install
npm run dev
```

```bash
cd frontend/apps/testy-market
npm install
npm run dev
```

백엔드 실행:

```bash
cd backend
GRADLE_USER_HOME=../.gradle-home ./gradlew run
```

## Deployment

현재 프론트 배포 기준은 `frontend/` 루트가 아니라 앱 폴더 단위입니다.

동그라미분식:

- Root Directory: `frontend/apps/donggeurami`
- Framework Preset: `Vite`
- Build Command: `npm run build`
- Output Directory: `dist`

testy market:

- Root Directory: `frontend/apps/testy-market`
- Framework Preset: `Vite`
- Build Command: `npm run build`
- Output Directory: `dist`

[`backend/`](backend/)는 Vercel 배포 대상이 아니며, 별도 서버 환경에서 실행하는 구조입니다.

## Docs

- 프론트엔드 상세: [`frontend/README.md`](frontend/README.md)
- 백엔드 상세: [`backend/README.md`](backend/README.md)
