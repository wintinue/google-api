# Frontend Sites

React 19 + Vite 8 + TypeScript 6 기반의 멀티 사이트 프론트엔드입니다. 각 사이트는 `apps/` 아래에서 HTML, React 코드, 사이트별 자산을 독립적으로 관리합니다.

## Structure

- `apps/donggeurami/`
  동그라미분식 사이트
- `apps/testy-market/`
  문서형 스타일의 로컬 프로듀스 마켓 사이트. 독립 Vite 앱으로도 배포 가능
- `index.html`
  사이트 선택용 루트 페이지
- `vite.config.ts`
  멀티 페이지 엔트리 설정

## 실행

```bash
npm install
npm run dev
```

기본 개발 주소:

```text
http://localhost:5173
```

## 빌드

```bash
npm run build
```

산출물은 `dist/` 에 생성되며, 각 사이트는 아래처럼 출력됩니다.

- `dist/index.html`
- `dist/apps/donggeurami/index.html`
- `dist/apps/testy-market/index.html`

## Vercel 설정

- Framework Preset: `Vite`
- Root Directory: `frontend`
- Build Command: `npm run build`
- Output Directory: `dist`

멀티 페이지 사이트라서 루트 페이지에서 각 사이트로 이동하거나, 각 경로를 직접 열면 됩니다.

- `/apps/donggeurami/`
- `/apps/testy-market/`

## Independent Deploy

`testy-market`은 독립 앱 구조도 같이 갖추고 있어서 Vercel에서 아래처럼 단독 배포할 수 있습니다.

- Root Directory: `frontend/apps/testy-market`
- Framework Preset: `Vite`
- Build Command: `npm run build`
- Output Directory: `dist`
