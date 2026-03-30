# GoogleApi Workspace

이 저장소는 두 가지를 함께 관리합니다.

1. GBP를 위한 [동그라미 분식 웹 프론트엔드](frontend/)
2. [Google OAuth 및 Google API 연동용 Kotlin/Ktor 백엔드](backend/)

원래는 Google API 연동 서버 중심의 저장소였고, 여기에 별도 배포 가능한 웹 프론트엔드를 같은 레포 안에 추가해 워크스페이스 형태로 정리한 상태입니다. 프론트엔드는 [Vercel 배포 대상인 `frontend/`](frontend/)로 관리하고, 백엔드는 필요할 때 [별도 서버에서 실행할 `backend/`](backend/) 구조를 전제로 합니다.

## Project Overview

현재 프론트엔드는 동그라미 분식 소개용 랜딩 페이지로 구성되어 있습니다. React 19, Vite 8, TypeScript 6 조합으로 작성되어 있으며, [`frontend/`](frontend/)만 독립적으로 빌드하고 배포할 수 있습니다.

백엔드는 Google OAuth 인증을 처리하고, 저장된 토큰으로 Google REST API를 호출하기 위한 로컬 Ktor 서버입니다. Google Business Profile 카테고리 조회와 Merchant 상품 관련 엔드포인트가 포함되어 있으며, 코드는 [`backend/`](backend/) 아래에 정리되어 있습니다.

즉, 이 저장소는 "정적 웹 프론트"와 "Google API용 Kotlin 백엔드"를 한 레포 안에서 나란히 관리하는 구조입니다.

## Workspace Structure

- [`frontend/`](frontend/): React 19 + Vite 8 + TypeScript 6 기반 동그라미 분식 웹페이지
- [`backend/`](backend/): Kotlin/Ktor 기반 Google OAuth 및 Google API 서버

## Quick Start

프론트엔드 실행:

```bash
cd frontend
npm install
npm run dev
```

백엔드 실행:

```bash
cd backend
GRADLE_USER_HOME=../.gradle-home ./gradlew run
```

## Deployment

Vercel에는 [`frontend/`](frontend/)만 배포하면 됩니다. 현재 구조상 [`backend/`](backend/)는 Vercel 배포 대상이 아니며, 프론트와 독립적으로 운영하는 것이 맞습니다.

- Root Directory: `frontend`
- Framework Preset: `Vite`
- Build Command: `npm run build`
- Output Directory: `dist`

백엔드는 별도 서버 환경에서 실행하면 됩니다.

## Docs

- 프론트엔드 상세: [`frontend/README.md`](frontend/README.md)
- 백엔드 상세: [`backend/README.md`](backend/README.md)
