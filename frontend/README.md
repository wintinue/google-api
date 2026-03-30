# 동그라미 분식 프론트엔드

React 19 + Vite 8 + TypeScript 6 기반의 단일 페이지 웹사이트입니다. 이 폴더만 별도 프로젝트로 연결하면 Vercel에서 정적 사이트로 배포할 수 있습니다.

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

산출물은 `dist/` 에 생성됩니다.

## Vercel 설정

- Framework Preset: `Vite`
- Root Directory: `frontend`
- Build Command: `npm run build`
- Output Directory: `dist`

SPA 라우팅 대응을 위해 `vercel.json` 에 모든 경로를 `index.html` 로 rewrite 하도록 설정했습니다.
