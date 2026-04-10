# Google API OAuth Server

로컬에서 Google OAuth 인증을 받고, 저장된 토큰으로 Google REST API를 호출하는 Kotlin/Ktor 서버입니다. Google Business Profile `accounts.list`, `categories.list`, `categories:batchGet`, `locations.list/get/patch/delete` 와 Google Merchant `accounts.list`, 제품 조회/등록/수정/삭제용 엔드포인트를 포함합니다.

Swagger UI는 `http://localhost:8088/swagger` 에서 확인할 수 있습니다.

## Requirements

- JDK 21 — Kotlin Gradle toolchain 이 21 로 고정되어 있어 21 미만에서는 빌드가 실패합니다.
- Gradle Wrapper 가 동봉되어 있어 별도 Gradle 설치 없이 `./gradlew` 로 실행합니다.
- Kotlin `2.1.21`, Ktor `2.3.12` (자세한 버전은 `backend/build.gradle.kts`).
- macOS / Linux / Windows 어디서든 동작하지만 본 README 의 명령은 zsh/bash 기준입니다.

## Run

```bash
cd backend
GRADLE_USER_HOME=../.gradle-home ./gradlew run
```

기본 주소는 `http://localhost:8088` 입니다.

개발 모드 실행:

```bash
cd backend
GRADLE_USER_HOME=../.gradle-home ./gradlew runDev
```

코드 저장 시 자동 재시작까지 원하면:

```bash
cd backend
GRADLE_USER_HOME=../.gradle-home ./gradlew runDev --continuous
```

API 문서:

```text
http://localhost:8088/swagger
```

## OAuth flow

상세 가이드(Google Cloud Console 에서 OAuth 클라이언트 발급, JSON 포맷, 멀티 credential 키, Swagger UI 검증) 는 [docs/credentials.md](docs/credentials.md) 를 참고하세요. 요약 흐름:

1. 브라우저에서 아래 URL 을 엽니다.

```text
http://localhost:8088/api/v1/auth/google-api/authorize
```

특정 OAuth 설정 키를 선택하려면 `credentialKey` 쿼리 파라미터를 붙입니다.

```text
http://localhost:8088/api/v1/auth/google-api/authorize?credentialKey=hq
```

- 기본 scope
  - `openid`, `userinfo.email`, `userinfo.profile`
  - `https://www.googleapis.com/auth/business.manage` (Business Profile API)
  - `https://www.googleapis.com/auth/content` (Merchant API)

2. 로그인과 동의를 마치면 액세스 토큰과 리프레시 토큰이 `data/google-oauth-token.json` 에 저장됩니다.

3. 현재 토큰 상태 확인 (Swagger UI 의 `GET /api/v1/auth/google-api/token` 또는 curl):

```bash
curl http://localhost:8088/api/v1/auth/google-api/token
```

응답에는 `accessToken`, `credentialKey`, `expiresAt`, `hasRefreshToken`, `scopes` 가 포함되며, 아직 토큰이 없으면 `authorized: false` 와 함께 `authorizeUrl` 이 반환됩니다.

## Google API call example

기본 사용자 정보 호출:

```bash
curl -X POST http://localhost:8088/api/v1/google-api/call \
  -H 'Content-Type: application/json' \
  -d '{
    "url": "https://www.googleapis.com/oauth2/v2/userinfo",
    "method": "GET"
  }'
```

비즈니스 프로필 API처럼 다른 Google API도 같은 방식으로 호출할 수 있습니다. 필요한 scope는 authorize URL에 쉼표 구분으로 넣으면 됩니다.

```text
http://localhost:8088/api/v1/auth/google-api/authorize?scopes=https://www.googleapis.com/auth/business.manage
```

credential JSON 의 최상위 키(`default`, `hq`, … 임의로 명명) 가 곧 `credentialKey` 입니다. 자세한 포맷과 멀티 클라이언트 설정 방법은 [docs/credentials.md](docs/credentials.md) 참고.

## Business Profile account examples

계정 목록 조회:

```bash
curl "http://localhost:8088/api/v1/business-profile/accounts?pageSize=20"
```

## Business Profile category examples

카테고리 목록 조회:

```bash
curl "http://localhost:8088/api/v1/business-profile/categories?regionCode=KR&languageCode=ko&view=BASIC&searchTerm=%EC%B9%B4%ED%8E%98"
```

카테고리 상세 일괄 조회:

```bash
curl "http://localhost:8088/api/v1/business-profile/categories/batch-get?languageCode=ko&view=FULL&names=categories/gcid:restaurant&names=categories/gcid:cafe"
```

## Business Profile location examples

위치 목록 조회:

```bash
curl "http://localhost:8088/api/v1/business-profile/locations?accountId=123456789&readMask=name,title,storeCode,websiteUri&pageSize=20"
```

위치 단건 조회:

```bash
curl "http://localhost:8088/api/v1/business-profile/locations?locationId=12345678901234567890&readMask=name,title,storeCode,websiteUri,phoneNumbers"
```

위치 수정:

```bash
curl -X PATCH http://localhost:8088/api/v1/business-profile/locations \
  -H 'Content-Type: application/json' \
  -d '{
    "locationId": "12345678901234567890",
    "updateMask": "title,websiteUri",
    "location": {
      "name": "locations/12345678901234567890",
      "title": "Updated Store Name",
      "websiteUri": "https://example.com/store"
    }
  }'
```

위치 삭제:

```bash
curl -X DELETE http://localhost:8088/api/v1/business-profile/locations \
  -H 'Content-Type: application/json' \
  -d '{
    "locationId": "12345678901234567890"
  }'
```

## Merchant product examples

Merchant Center 계정 목록 조회:

```bash
curl "http://localhost:8088/api/v1/merchant/accounts?pageSize=50"
```

처리된 상품 목록 조회:

```bash
curl "http://localhost:8088/api/v1/merchant/products?accountId=123456789&pageSize=20"
```

처리된 상품 단건 조회:

```bash
curl "http://localhost:8088/api/v1/merchant/products?accountId=123456789&productId=online~en~US~sku123"
```

상품 입력 생성:

```bash
curl -X POST http://localhost:8088/api/v1/merchant/product-inputs \
  -H 'Content-Type: application/json' \
  -d '{
    "accountId": "123456789",
    "dataSourceId": "987654321",
    "productInput": {
      "channel": "ONLINE",
      "offerId": "sku123",
      "contentLanguage": "en",
      "feedLabel": "US",
      "attributes": {
        "title": "Sample Product",
        "description": "Sample product from local API",
        "link": "https://example.com/products/sku123",
        "imageLink": "https://example.com/products/sku123.jpg",
        "availability": "in stock",
        "condition": "new",
        "price": {
          "amountMicros": "19990000",
          "currencyCode": "USD"
        }
      }
    }
  }'
```

상품 입력 수정:

```bash
curl -X PATCH http://localhost:8088/api/v1/merchant/product-inputs \
  -H 'Content-Type: application/json' \
  -d '{
    "accountId": "123456789",
    "dataSourceId": "987654321",
    "productInputId": "online~en~US~sku123",
    "updateMask": "attributes.title,attributes.description",
    "productInput": {
      "name": "accounts/123456789/productInputs/online~en~US~sku123",
      "channel": "ONLINE",
      "offerId": "sku123",
      "contentLanguage": "en",
      "feedLabel": "US",
      "attributes": {
        "title": "Updated Product",
        "description": "Updated description"
      }
    }
  }'
```

상품 입력 삭제:

```bash
curl -X DELETE http://localhost:8088/api/v1/merchant/product-inputs \
  -H 'Content-Type: application/json' \
  -d '{
    "accountId": "123456789",
    "dataSourceId": "987654321",
    "productInputId": "online~en~US~sku123"
  }'
```

## Postman

Import files:

- `postman/GoogleApi.postman_collection.json`
- `postman/GoogleApi.local.postman_environment.json`

계정/카테고리/위치 조회는 컬렉션의 `Business Profile` 폴더에서, Merchant 계정/상품 요청은 `Merchant` 폴더에서 바로 실행할 수 있습니다.

## Environment variables

- `PORT`: 서버 포트. 기본값 `8088`
- `APP_BASE_URL`: 외부에서 접근하는 서버 주소. 기본값 `http://localhost:8088`
- `GOOGLE_OAUTH_CLIENT_SECRET_PATH`: OAuth 클라이언트 JSON 경로. 기본값 `credentials/google-oauth-client.json`
- `GOOGLE_OAUTH_CLIENT_KEY`: 기본 OAuth 설정 키. 기본값 `default`
- `GOOGLE_OAUTH_REDIRECT_URI`: 콜백 URL 강제 지정 시 사용

## OAuth client id / secret

매 요청마다 `client_id`, `client_secret` 를 보내지는 않지만, 서버가 액세스 토큰과 리프레시 토큰을 발급/갱신하려면 OAuth 클라이언트 정보가 반드시 필요합니다. 즉:

- Google API 실제 호출 시 헤더에는 보통 `Bearer access token` 만 들어감
- 그 access token 을 처음 받거나 refresh 할 때는 `client_id`, `client_secret` 이 필수
