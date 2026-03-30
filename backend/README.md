# Google API OAuth Server

로컬에서 Google OAuth 인증을 받고, 저장된 토큰으로 Google REST API를 호출하는 Kotlin/Ktor 서버입니다. Google Business Profile `categories.list`, `categories:batchGet` 와 Google Merchant 제품 조회/등록/수정/삭제용 엔드포인트를 포함합니다.

Swagger UI는 `http://localhost:8080/swagger` 에서 확인할 수 있습니다.

## Run

```bash
cd backend
GRADLE_USER_HOME=../.gradle-home ./gradlew run
```

기본 주소는 `http://localhost:8080` 입니다.

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
http://localhost:8080/swagger
```

## OAuth flow

1. 브라우저에서 아래 URL을 엽니다.

```text
http://localhost:8080/api/v1/auth/google-api/authorize
```

특정 OAuth 설정 키를 선택하려면 `credentialKey` 쿼리 파라미터를 붙입니다.

```text
http://localhost:8080/api/v1/auth/google-api/authorize?credentialKey=web2
```

- 기본 scope
  - `https://www.googleapis.com/auth/business.manage` (Business Profile API)
  - `https://www.googleapis.com/auth/content` (Merchant API)

2. 로그인과 동의를 마치면 액세스 토큰과 리프레시 토큰이 `data/google-oauth-token.json`에 저장됩니다.

3. 현재 토큰 상태 확인:

```bash
curl http://localhost:8080/api/v1/auth/google-api/token
```

## Google API call example

기본 사용자 정보 호출:

```bash
curl -X POST http://localhost:8080/api/v1/google-api/call \
  -H 'Content-Type: application/json' \
  -d '{
    "url": "https://www.googleapis.com/oauth2/v2/userinfo",
    "method": "GET"
  }'
```

비즈니스 프로필 API처럼 다른 Google API도 같은 방식으로 호출할 수 있습니다. 필요한 scope는 authorize URL에 쉼표 구분으로 넣으면 됩니다.

```text
http://localhost:8080/api/v1/auth/google-api/authorize?scopes=https://www.googleapis.com/auth/business.manage
```

credential JSON 안에 `web`, `web2`, `web3` 같은 최상위 객체가 추가되면 같은 이름을 `credentialKey`로 넘겨 선택할 수 있습니다.

## Business Profile category examples

카테고리 목록 조회:

```bash
curl "http://localhost:8080/api/v1/business-profile/categories?regionCode=KR&languageCode=ko&view=BASIC&searchTerm=%EC%B9%B4%ED%8E%98"
```

카테고리 상세 일괄 조회:

```bash
curl "http://localhost:8080/api/v1/business-profile/categories/batch-get?languageCode=ko&view=FULL&names=categories/gcid:restaurant&names=categories/gcid:cafe"
```

## Merchant product examples

처리된 상품 목록 조회:

```bash
curl "http://localhost:8080/api/v1/merchant/products?accountId=123456789&pageSize=20"
```

처리된 상품 단건 조회:

```bash
curl "http://localhost:8080/api/v1/merchant/products?accountId=123456789&productId=online~en~US~sku123"
```

상품 입력 생성:

```bash
curl -X POST http://localhost:8080/api/v1/merchant/product-inputs \
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
curl -X PATCH http://localhost:8080/api/v1/merchant/product-inputs \
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
curl -X DELETE http://localhost:8080/api/v1/merchant/product-inputs \
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

카테고리 조회는 컬렉션의 `Business Profile` 폴더에서, Merchant 상품 요청은 `Merchant` 폴더에서 바로 실행할 수 있습니다.

## Environment variables

- `PORT`: 서버 포트. 기본값 `8080`
- `APP_BASE_URL`: 외부에서 접근하는 서버 주소. 기본값 `http://localhost:8080`
- `GOOGLE_OAUTH_CLIENT_SECRET_PATH`: OAuth 클라이언트 JSON 경로. 기본값 `credentials/google-oauth-client.json`
- `GOOGLE_OAUTH_CLIENT_KEY`: 기본 OAuth 설정 키. 기본값 `web`
- `GOOGLE_OAUTH_REDIRECT_URI`: 콜백 URL 강제 지정 시 사용

## OAuth client id / secret

매 요청마다 `client_id`, `client_secret` 를 보내지는 않지만, 서버가 액세스 토큰과 리프레시 토큰을 발급/갱신하려면 OAuth 클라이언트 정보가 반드시 필요합니다. 즉:

- Google API 실제 호출 시 헤더에는 보통 `Bearer access token` 만 들어감
- 그 access token 을 처음 받거나 refresh 할 때는 `client_id`, `client_secret` 이 필수
