# GoogleApi Agent Guide

이 문서는 이 저장소에서 작업하는 모든 에이전트(Claude Code 포함) 가 참고하는 단일 가이드입니다. CLAUDE.md 는 이 파일을 `@AGENTS.md` 로 import 합니다.

문서 흐름:
1. 프로젝트 컨텍스트 — Purpose, Tech Stack, Runtime Assumptions, Architecture, Endpoints
2. 도메인 규칙 — Development Rules, OAuth Rules, Business Profile Notes
3. 빌드/실행 — Build And Verification
4. 문서 레이아웃과 동기화 규칙
5. 작업 방식 — Communication, Confirmation policy, Tooling, Editing rules
6. Commit Convention

---

## Purpose
- 로컬 Kotlin/Ktor 서버로 Google OAuth 인증과 Google REST API 호출(프록시), Google Business Profile, Google Merchant API 를 다룹니다.
- 외부 노출용이 아니라 개발자의 로컬 호출/검증용입니다.

## Tech Stack
- Kotlin JVM (`2.1.21`)
- Ktor `2.3.12` (server + client)
- Gradle (Wrapper 동봉, Kotlin toolchain `21`)
- kotlinx.serialization

## Runtime Assumptions
- 기본 로컬 서버 URL: `http://localhost:8088`
- OAuth client credentials: `credentials/google-oauth-client.json` (환경변수 `GOOGLE_OAUTH_CLIENT_SECRET_PATH` 로 덮어쓸 수 있음)
- OAuth redirect URI: 기본은 credential JSON 의 첫 redirect URI. `GOOGLE_OAUTH_REDIRECT_URI` 로 강제 지정 가능.
- 토큰과 OAuth state 는 로컬 `data/` 하위에 영속화됩니다.
- 기본 credential 키 fallback 은 `default` (환경변수 `GOOGLE_OAUTH_CLIENT_KEY` 로 변경).

## Current Architecture
- `backend/src/main/kotlin/org/example/Main.kt` — 진입점, DI 와이어업.
- `backend/src/main/kotlin/org/example/app` — App 모듈, 루트 라우트, 도메인 횡단 서버 와이어업.
- `backend/src/main/kotlin/org/example/oauth` — OAuth 라우트, 모델, 토큰 저장소.
- `backend/src/main/kotlin/org/example/googleapi` — 일반 Google REST 프록시 요청/라우트 모델.
- `backend/src/main/kotlin/org/example/businessprofile/...` — Business Profile 도메인 라우트와 모델.
- `backend/src/main/kotlin/org/example/merchant/...` — Merchant 도메인 라우트와 모델.
- `backend/src/main/kotlin/org/example/service` — Google API 호출 및 흐름 조율 서비스.
- `backend/src/main/kotlin/org/example/config` — 공용 JSON, OAuth config 로딩.
- `backend/src/main/resources/openapi` — 로컬에서 서빙되는 Swagger/OpenAPI 문서.

## Implemented Endpoints
- `GET /`
- `GET /api/v1/auth/google-api/authorize`
- `GET /api/v1/auth/google-api/redirection`
- `GET /api/v1/auth/google-api/token`
- `POST /api/v1/google-api/call`
- `GET /api/v1/business-profile/accounts`
- `GET /api/v1/business-profile/categories`
- `GET /api/v1/business-profile/categories/batch-get`
- `GET|PATCH|DELETE /api/v1/business-profile/locations`
- `GET /api/v1/merchant/accounts`
- `GET /api/v1/merchant/products`
- `POST|PATCH|DELETE /api/v1/merchant/product-inputs`

---

## Development Rules
- 라우트 핸들러는 얇게, 도메인 단위로. OAuth 로직, Google API 호출, Business Profile 로직은 서비스 계층에 둡니다.
- 새 라우트는 해당 도메인 패키지에 먼저 둡니다. 무관한 API 를 한 컨트롤러 파일에 모으지 않습니다.
- 새 Google API 통합 추가 절차:
  1. 집중된 service 메서드 추가
  2. 라우트 핸들러 추가/확장
  3. 인증 로직을 복제하지 말고 기존 OAuth/토큰 흐름 재사용
- 인증된 Google REST 호출은 특별한 이유가 없는 한 `GoogleApiProxyService` 를 재사용합니다.
- 설정값/시크릿은 코드에 박지 말고 파일/환경변수에서 읽습니다.
- 요청 파라미터는 raw map 대신 명시적·최소형 모델을 사용합니다.

## OAuth Rules
- Google API 호출은 OAuth access token 기반입니다(직접 API 키 사용 아님).
- `client_id`, `client_secret` 는 다음에 여전히 필요:
  - authorization code 교환
  - refresh token 교환
- OAuth client credential 로딩 경로는 다른 안전한 시크릿 소스로 대체하지 않는 한 제거 금지.
- Business Profile API: `https://www.googleapis.com/auth/business.manage` scope 포함 필수.
- Merchant API 상품 작업: `https://www.googleapis.com/auth/content` scope 포함 필수.

## Business Profile Notes
- `categories.list`, `categories:batchGet`, `accounts.list`, `locations.list/get/patch/delete` 가 로컬 서버를 통해 구현되어 있습니다.
- 쿼리 파라미터 검증은 라우트에서, 원격 호출 구성은 서비스에서.
- Google API 로 전송되는 URL 쿼리 값은 명시적으로 인코딩합니다.

---

## Build And Verification
- `GRADLE_USER_HOME=.gradle-home ./gradlew test` — 로컬 검증
- `GRADLE_USER_HOME=.gradle-home ./gradlew run` — 로컬 서버 실행
- `GRADLE_USER_HOME=.gradle-home ./gradlew runDev` — Ktor development mode 실행
- `GRADLE_USER_HOME=.gradle-home ./gradlew runDev --continuous` — 개발 중 자동 재시작
- 서버 기동 같은 장시간 실행은 백그라운드로 띄우고 로그·포트로 결과를 확인합니다(아래 Tooling 참고).

---

## Documentation Layout
- `backend/README.md` — 빠른 시작, 실행 명령, API 호출 예시.
- `backend/docs/credentials.md` — OAuth client JSON 포맷, 멀티 credential 키, 토큰 발급 흐름, Swagger UI 검증.
- `backend/src/main/resources/openapi/documentation.yaml` — `/swagger` 에 서빙되는 OpenAPI 원본.
- `backend/postman/GoogleApi.postman_collection.json` — Postman 컬렉션.
- `AGENTS.md` (이 문서) — 아키텍처·운영·커밋 가이드.

### 동기화 규칙
| 변경 종류 | 함께 갱신할 문서 |
|---|---|
| 라우트/요청·응답 스키마 | `documentation.yaml` + `postman/GoogleApi.postman_collection.json` |
| 사용자에게 보이는 동작/예시 | `backend/README.md` |
| OAuth credential / 토큰 흐름 | `backend/docs/credentials.md` |
| 아키텍처/엔드포인트 목록/도메인 규칙 | `AGENTS.md` |

---

## Working with this repo

### Communication
- 사용자와의 모든 대화는 **한국어** 로 응답합니다. 코드/명령어/식별자는 그대로 둡니다.
- 응답은 짧고 사실 위주로. 불필요한 인삿말, 자기소개, "물론입니다" 같은 추임새 금지.
- 작업 시작 전 1문장으로 무엇을 할지 알리고, 큰 변경/방향 전환/막힘 발생 시에만 중간 보고합니다.
- 끝맺음 요약은 "무엇을 바꿨고, 다음에 무엇이 필요한가" 두 가지에 집중합니다.

### Confirmation policy (사용자 명시 선호)
다음 작업은 **수행 전 사용자 확인** 을 받습니다.

- 서버 재시작, 프로세스 종료(`kill`), 포트 점유 해제
- `git commit`, `git push`, `git reset --hard`, `git rebase`, force-push, 브랜치 삭제
- 파일/폴더 삭제(`rm`, `rm -rf`), 폴더 구조 변경
- 외부에 영향을 주는 액션(원격 호출, 메시지 전송, 외부 서비스 토글)
- 의존성 추가/제거/업그레이드, 빌드 설정 큰 변경

오류가 발생했을 때는 **원인 분석 + 해결 방안** 을 먼저 제시하고, 사용자가 승인한 뒤에 수정에 들어갑니다.

자동으로 진행해도 되는 작업: 파일 읽기, 코드 검색, 로컬 빌드/테스트/실행 결과 확인, 가역적인 편집(되돌리기 쉬운 코드 수정·문서 수정).

### Tooling
- 검색은 `Grep`/`Glob`, 파일 작업은 `Read`/`Edit`/`Write` 우선. `cat`, `find`, `grep`, `sed`, `awk` 를 Bash 로 호출하지 않습니다.
- 장시간 실행되는 명령(서버 기동, 빌드 watch 등)은 `run_in_background: true` 로 띄우고, 결과는 `Read` 또는 `Monitor` 로 확인합니다. `sleep N`(N≥2) 폴링은 금지.
- 코드베이스 광역 탐색·다단계 조사가 필요하면 Explore 서브에이전트를 사용해 컨텍스트 사용을 줄입니다.
- 의존성 없는 도구 호출은 한 메시지에 묶어 병렬로 실행합니다.

### Editing rules
- 새 파일 생성보다 기존 파일 편집을 우선합니다.
- 문서 파일(`*.md`) 신규 생성은 사용자가 명시적으로 요청한 경우에만.
- 수정 사유가 비자명할 때만 짧은 코멘트를 답니다. "무엇을 한다" 는 코드가 이미 보여주므로 적지 않습니다.
- 작업 무관한 리팩터·포맷팅 변경은 같은 커밋·같은 변경에 섞지 않습니다.

---

## Commit Convention

### Subject format
```
<type> <scope>: <subject>
```

- **type** — `feat`, `fix`, `docs`, `chore`, `refactor`, `test`, `style`, `perf`, `ci`, `build` 중 하나.
- **scope** — `backend`, `frontend`, `repo` (저장소 횡단/툴링) 중 하나. 기존 히스토리(`feat backend: ...`, `chore repo: ...`) 를 따릅니다.
- **subject** — 짧은 영문 imperative, 소문자 시작, 마침표 없음.

### Rules
- **[MUST] Commit description in Korean.** 커밋 본문은 반드시 한국어로 작성. 제목은 영문 imperative, 본문에서 한국어로 변경 의도/배경/영향 범위를 설명합니다.
- **[SHOULD] Subject line under 72 characters.** 제목 한 줄은 72 자 이내로 유지. 길어지면 본문으로 옮깁니다.
- **[SHOULD] One logical change per commit.** 한 커밋에는 하나의 논리적 변경만 담습니다. 무관한 수정은 별도 커밋으로 분리합니다.

### Type cheatsheet
| Type       | When                                                       |
| ---------- | ---------------------------------------------------------- |
| `feat`     | 사용자/호출자에게 보이는 새 기능, 엔드포인트, 필드 추가    |
| `fix`      | 버그 수정                                                  |
| `docs`     | README, AGENTS.md, OpenAPI 등 문서만 수정                  |
| `chore`    | 빌드 설정, 의존성, .gitignore, 디렉터리 메타파일 등 잡일   |
| `refactor` | 동작 변경 없는 코드 정리/구조 변경                         |
| `test`     | 테스트 추가/수정                                           |
| `style`    | 포맷팅, 세미콜론, import 정리 등                           |
| `perf`     | 성능 개선                                                  |
| `ci`       | CI 설정 변경                                               |
| `build`    | Gradle, 패키징, 도구 체인 변경                             |

### Body format
- 제목 다음 한 줄을 비우고 한국어 본문을 작성. 항목이 여러 개면 `-` 불릿으로 나열.
- 무엇/왜/영향 순으로. "어떻게" 는 diff 가 보여주므로 생략.
- 라인 길이 가급적 100 자 이내.
- 본문 마지막에는 항상 다음 한 줄을 붙입니다:
  `Co-Authored-By: Claude Opus 4.6 (1M context) <noreply@anthropic.com>`

### 한 파일이 여러 논리에 걸칠 때
- 같은 파일에 무관한 두 변경(예: 포트 번호 정정 + 커밋 컨벤션 추가) 이 모두 있으면 `git add -p` 로 hunk 단위로 분리해 두 커밋에 나눠 담습니다.
- 분리가 어렵거나 의미 단위가 같다면 한 커밋으로 합치되, 본문에서 두 변경을 별도 불릿으로 명확히 구분합니다.

### 예시
```
feat backend: support multi-credential oauth client

- credential JSON 의 최상위 키를 credentialKey 로 선택할 수 있도록 변경
- 기본 키 fallback 을 web → default 로 교체 (실제 파일 키와 일치)
- /token 응답에 accessToken 필드 추가 → Swagger UI 에서 토큰 즉시 확인 가능

Co-Authored-By: Claude Opus 4.6 (1M context) <noreply@anthropic.com>
```

### 커밋 금지 / 기본 제외
- `.idea/` 변경분 (의도적 결정이 없는 한). `.idea/gradle.xml`, `.idea/misc.xml` 은 IDE 메타데이터로 취급.
- `backend/gradle.properties` 의 `org.gradle.java.home` 같은 머신 특정 절대 경로.
- `backend/credentials/`, `backend/data/` 내부 파일 (`.gitkeep` 만 추적).
- `BACKEND_ANALYSIS.md`, `FRONTEND_ANALYSIS.md`, `WORKSPACE_OVERVIEW.md`, `test.json` 같은 워크스페이스 임시 산출물 — 사용자가 명시적으로 커밋을 지시하지 않는 한 staging 하지 않습니다.
