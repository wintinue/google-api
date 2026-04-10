# OAuth 자격 증명 & 토큰 발급 가이드

이 문서는 로컬 서버가 Google API 호출에 필요한 OAuth 클라이언트 정보를 어떻게 준비하고, 토큰을 발급받고, Swagger UI 로 검증하는지 설명합니다.

## 1. Google Cloud Console 에서 OAuth 클라이언트 만들기

1. <https://console.cloud.google.com/> 접속 후 프로젝트 선택 (또는 새로 생성).
2. **APIs & Services → OAuth consent screen** 에서 동의 화면을 구성합니다. 내부 테스트라면 User Type 을 *External* 로 두고 본인 계정을 *Test users* 에 추가합니다.
3. **APIs & Services → Credentials → Create credentials → OAuth client ID** 를 선택합니다.
4. Application type 은 **Web application**.
5. **Authorized redirect URIs** 에 아래 두 URL 을 등록합니다 (포트는 서버 설정에 맞춰 조정).
   - `http://localhost:8088/api/v1/auth/google-api/redirection`
   - `http://localhost:8080/api/v1/auth/google-api/redirection` (다른 포트도 쓸 일이 있으면 함께 등록)
6. 사용할 API 를 **APIs & Services → Library** 에서 활성화합니다.
   - Google My Business / Business Profile (계정·카테고리·로케이션 호출 시)
   - Merchant API (Merchant 호출 시)
7. 생성된 client 의 **Download JSON** 으로 client_id / client_secret 을 받습니다.

## 2. `credentials/google-oauth-client.json` 포맷

서버는 단일 OAuth 클라이언트뿐 아니라 **여러 클라이언트를 키 단위로** 관리합니다. 최상위 객체의 각 키 이름이 `credentialKey` 로 사용됩니다.

`backend/credentials/google-oauth-client.json` 예시:

```json
{
  "default": {
    "client_id": "xxxxxxxx.apps.googleusercontent.com",
    "project_id": "your-project",
    "auth_uri": "https://accounts.google.com/o/oauth2/auth",
    "token_uri": "https://oauth2.googleapis.com/token",
    "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
    "client_secret": "GOCSPX-xxxxxxxxxxxxxxxxxxxxxxxx",
    "redirect_uris": [
      "http://localhost:8088/api/v1/auth/google-api/redirection",
      "http://localhost:8080/api/v1/auth/google-api/redirection"
    ]
  },
  "hq": {
    "client_id": "...",
    "client_secret": "GOCSPX-...",
    "auth_uri": "https://accounts.google.com/o/oauth2/auth",
    "token_uri": "https://oauth2.googleapis.com/token",
    "redirect_uris": ["http://localhost:8088/api/v1/auth/google-api/redirection"]
  }
}
```

규칙:

- 최상위 키(`default`, `hq`, `kge`, `ljj`, …) 는 자유롭게 명명. `credentialKey` 쿼리 파라미터로 어떤 키를 쓸지 선택합니다.
- 키를 명시하지 않으면 환경변수 `GOOGLE_OAUTH_CLIENT_KEY` 가 사용되고, 그것도 없으면 **`default`** 가 사용됩니다. 그래서 키 하나만 둘 거라면 그냥 `default` 라고 이름 짓는 게 가장 편합니다.
- 각 객체는 표준 Google `client_secret_*.json` 의 `web` 객체와 동일한 필드를 가집니다 (`client_id`, `client_secret`, `auth_uri`, `token_uri`, `redirect_uris`).
- 파일 자체는 `.gitignore` 로 커밋되지 않습니다. `credentials/` 디렉터리는 `.gitkeep` 으로만 추적됩니다.

서버는 시작 시 이 파일 전체를 파싱해 검증하므로, 잘못된 JSON 이거나 기본 키가 존재하지 않으면 부팅 자체가 실패합니다(에러 메시지에 가능한 키 목록이 출력됨).

## 3. 토큰 발급 흐름

1. 서버 실행: 서버 실행 방법은 [README — Run](../README.md#run) 참고.
2. 브라우저로 authorize 엔드포인트 호출. 기본 키(`default`) 사용 시:

   ```
   http://localhost:8088/api/v1/auth/google-api/authorize
   ```

   다른 키 사용 시:

   ```
   http://localhost:8088/api/v1/auth/google-api/authorize?credentialKey=hq
   ```

   특정 scope 만 쓰고 싶다면 쉼표 구분으로:

   ```
   http://localhost:8088/api/v1/auth/google-api/authorize?scopes=https://www.googleapis.com/auth/business.manage,https://www.googleapis.com/auth/content
   ```

3. Google 동의 화면에서 로그인/허용 → 서버의 `/redirection` 콜백이 authorization code 를 받아 토큰으로 교환합니다.
4. 발급된 access token / refresh token 은 `backend/data/google-oauth-token.json` 에 저장됩니다. CSRF state 는 `backend/data/oauth-state.json` 에 임시 저장됩니다. 두 파일 모두 `.gitignore` 로 커밋되지 않습니다.
5. 토큰이 만료되면 다음 API 호출 시 서버가 자동으로 refresh token 으로 재발급을 시도합니다.

기본 scope 는 코드(`GoogleOAuthService.defaultScopes`) 에 정의되어 있습니다.

- `openid`
- `https://www.googleapis.com/auth/userinfo.email`
- `https://www.googleapis.com/auth/userinfo.profile`
- `https://www.googleapis.com/auth/business.manage`
- `https://www.googleapis.com/auth/content`

## 4. Swagger UI 로 검증하기

Swagger UI: <http://localhost:8088/swagger>

토큰 상태 확인:

1. Swagger UI 에서 **OAuth → `GET /api/v1/auth/google-api/token`** 펼치기.
2. *Try it out → Execute*.
3. 응답 두 가지 형태:
   - **이미 토큰이 캐시된 상태**

     ```json
     {
       "authorized": true,
       "accessToken": "ya29....",
       "credentialKey": "default",
       "expiresAt": 1775814093,
       "hasRefreshToken": true,
       "scopes": ["openid", "...", "https://www.googleapis.com/auth/business.manage"]
     }
     ```

   - **아직 토큰이 없는 상태**

     ```json
     {
       "authorized": false,
       "defaultCredentialKey": "default",
       "authorizeUrl": "https://accounts.google.com/o/oauth2/auth?...",
       "availableCredentialKeys": ["default", "hq", "kge", "ljj"]
     }
     ```

     `authorizeUrl` 을 브라우저에서 열어 1~3 단계를 진행하면 됩니다. `credentialKey` 쿼리 파라미터를 바꿔서 호출하면 그 키에 맞는 authorize URL 이 만들어집니다.

토큰을 갈아끼우고 싶다면 `backend/data/google-oauth-token.json` 을 지운 뒤 authorize 를 다시 호출하면 됩니다.

토큰이 정상이면 Swagger 에서 다른 API (예: **Business Profile → `GET /api/v1/business-profile/accounts`**) 를 *Try it out* 으로 호출해 실제 Google API 까지 잘 닿는지 확인할 수 있습니다.

## 5. 관련 환경변수

| 변수 | 기본값 | 설명 |
|---|---|---|
| `GOOGLE_OAUTH_CLIENT_SECRET_PATH` | `credentials/google-oauth-client.json` | OAuth client JSON 경로 |
| `GOOGLE_OAUTH_CLIENT_KEY` | `default` | 명시되지 않은 경우 사용할 기본 credential 키 |
| `GOOGLE_OAUTH_REDIRECT_URI` | `${APP_BASE_URL}/api/v1/auth/google-api/redirection` | 콜백 URL 강제 지정 |
| `APP_BASE_URL` | `http://localhost:8088` | authorize/redirect URL 생성에 쓰이는 베이스 URL |
| `PORT` | `8088` | 서버 리스닝 포트 |
