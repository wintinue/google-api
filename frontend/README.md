# Frontend Workspace

이 폴더는 React 19 + Vite 8 + TypeScript 6 기반 프론트엔드 워크스페이스입니다. 실제 운영 단위는 `apps/` 아래의 개별 사이트이며, `frontend/` 루트는 로컬 허브와 멀티 페이지 개발용 엔트리 역할을 합니다.

## Apps

- `apps/donggeurami/`
  동그라미분식 랜딩 페이지. 독립 실행과 독립 배포 가능
- `apps/testy-market/`
  문서형 스타일의 로컬 마켓 랜딩 페이지. 독립 실행과 독립 배포 가능

## Workspace Files

- `index.html`
  로컬에서 두 사이트를 오가는 허브 페이지
- `package.json`
  루트 멀티 페이지 개발 및 빌드용 설정
- `vite.config.ts`
  `apps/*/index.html`을 묶는 멀티 페이지 엔트리 설정

## Run

루트 허브와 멀티 페이지 개발 서버:

```bash
cd frontend
npm install
npm run dev
```

기본 주소:

```text
http://localhost:5173
```

루트 개발 서버에서 접속 가능한 경로:

- `/`
- `/apps/donggeurami/`
- `/apps/testy-market/`

개별 앱 개발 서버:

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

## Build

루트 멀티 페이지 빌드:

```bash
cd frontend
npm run build
```

산출물:

- `dist/index.html`
- `dist/apps/donggeurami/index.html`
- `dist/apps/testy-market/index.html`

개별 앱도 각 폴더에서 별도로 빌드할 수 있습니다.

## Vercel

실제 배포는 앱 폴더 단위로 하는 것을 권장합니다.

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

`frontend/` 루트도 멀티 페이지 앱으로 빌드할 수 있지만, 현재 운영 기준으로는 개별 앱 배포가 더 자연스럽습니다.
