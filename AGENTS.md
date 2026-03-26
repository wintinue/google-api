# GoogleApi Agent Guide

## Purpose
- This repository provides a local Kotlin/Ktor server for Google OAuth and Google API calls.
- The current implemented target APIs include generic Google REST proxy calls and Google Business Profile category APIs.

## Tech Stack
- Kotlin JVM
- Gradle
- Ktor server
- kotlinx.serialization

## Runtime Assumptions
- Default local server URL is `http://localhost:8080`.
- OAuth client credentials are loaded from `credentials/google-oauth-client.json` unless `GOOGLE_OAUTH_CLIENT_SECRET_PATH` is set.
- OAuth redirect URI defaults to the first redirect URI in the credential JSON unless `GOOGLE_OAUTH_REDIRECT_URI` is set.
- Token and OAuth state are persisted locally under `data/`.

## Current Architecture
- `src/main/kotlin/org/example/Main.kt`
  - Application entrypoint and dependency wiring.
- `src/main/kotlin/org/example/app`
  - App module, root routes, and cross-domain server wiring.
- `src/main/kotlin/org/example/oauth`
  - OAuth routes, models, and token storage.
- `src/main/kotlin/org/example/googleapi`
  - Generic Google API proxy request/route models.
- `src/main/kotlin/org/example/merchant/product`
  - Merchant product routes and request models.
- `src/main/kotlin/org/example/businessprofile/category`
  - Business Profile category routes and request models.
- `src/main/kotlin/org/example/service`
  - Shared service implementations that call Google APIs or orchestrate flows.
- `src/main/kotlin/org/example/config`
  - Shared JSON and OAuth config loading.
- `src/main/resources/openapi`
  - Swagger/OpenAPI documents served locally.

## Development Rules
- Keep route handlers thin and domain-scoped. Put OAuth logic, Google API calls, and Business Profile logic in services.
- Add new routes under the matching domain package first. Avoid a single shared controller file for unrelated APIs.
- Add new Google API integrations by:
  1. Adding a focused service method.
  2. Adding or extending route handlers.
  3. Reusing the existing OAuth/token flow instead of duplicating auth logic.
- Reuse `GoogleApiProxyService` for authenticated Google REST calls unless there is a strong reason to bypass it.
- Keep configuration out of code. Read secrets and environment-dependent values from files or environment variables.
- Prefer minimal, explicit models for request parameters instead of passing raw maps around.
- Update `README.md` and `postman/GoogleApi.postman_collection.json` when API usage changes.
- Update `src/main/resources/openapi/documentation.yaml` when route contracts change.

## OAuth Rules
- Google API calls rely on OAuth access tokens, not direct API key usage.
- `client_id` and `client_secret` are still required for:
  - authorization code exchange
  - refresh token exchange
- Do not remove the OAuth client credential loading path unless replacing it with another secure secret source.
- For Google Business Profile APIs, ensure the authorization flow includes `https://www.googleapis.com/auth/business.manage`.
- For Merchant API product operations, ensure the authorization flow includes `https://www.googleapis.com/auth/content`.

## Implemented Endpoints
- `GET /`
- `GET /api/v1/auth/google-api/authorize`
- `GET /api/v1/auth/google-api/redirection`
- `GET /api/v1/auth/google-api/token`
- `POST /api/v1/google-api/call`
- `GET /api/v1/business-profile/categories`
- `GET /api/v1/business-profile/categories/batch-get`

## Business Profile Notes
- `categories.list` and `categories:batchGet` are implemented through the local server.
- Keep query parameter validation in routes and remote call construction in services.
- URL query values sent to Google APIs should be encoded explicitly.

## Build And Verification
- Use `GRADLE_USER_HOME=.gradle-home ./gradlew test` for local verification.
- Use `GRADLE_USER_HOME=.gradle-home ./gradlew run` to start the server locally.
- Use `GRADLE_USER_HOME=.gradle-home ./gradlew runDev` to start the server in Ktor development mode.
- Use `GRADLE_USER_HOME=.gradle-home ./gradlew runDev --continuous` for auto-restart during development.

## Git And Workspace Notes
- `.idea/gradle.xml` and `.idea/misc.xml` are currently treated as local IDE metadata, not required application changes.
- Do not commit IDE-only file changes unless there is a deliberate decision to version those settings.

## Commit Guidance
- Prefer small commits grouped by concern.
- A recent preferred commit split in this repository was:
  - initialization/build setup
  - docs and Postman collection
  - OAuth flow and routing
  - Google API proxy and Business Profile endpoints
