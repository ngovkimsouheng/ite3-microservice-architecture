# IAM Service

This service gives the project one place to manage identity and access: users sign in once, receive OAuth/OIDC tokens, and use their roles and permissions across protected APIs.

## What happens

1. A user creates an account or signs in with a password, Google, GitHub, Facebook, or Telegram. Social sign-in finds or provisions the local user and records the provider identity, so accounts can also be linked or unlinked later.
2. The authorization server issues OAuth 2.1/OIDC tokens. Authorization-code clients use PKCE; the built-in web client rotates refresh tokens. Dynamic client registration is available for clients that need to register themselves.
3. Clients send access tokens to `/api/**`. The API checks the user's roles and permissions before allowing protected operations. Effective permissions combine active role permissions and direct user grants, less direct denials.
4. Users can manage their profile, password, linked identities, and authorized applications in the English/Khmer browser pages.

The domain keeps the core rules: system roles and permissions are protected, inactive roles cannot be assigned, assignments are audited and revoked rather than erased, and a user cannot unlink their last sign-in method. `EmailAddress` centralizes email normalization and basic domain validation; request validation handles user-facing format errors.

## Run locally

Start PostgreSQL with `db_oauth2` on `localhost:9543`, then provide the database password and OAuth client/provider credentials as environment variables. Secrets belong in the environment, not in this file or source code.

| Variables | Used for |
| --- | --- |
| `DATASOURCE_PASSWORD` | PostgreSQL |
| `REGISTRAR_CLIENT_SECRET`, `GATEWAY_CLIENT_SECRET`, `WEB_CLIENT_SECRET` | Built-in OAuth clients |
| `GOOGLE_OAUTH_CLIENT_ID`, `GOOGLE_OAUTH_CLIENT_SECRET` | Google sign-in |
| `GITHUB_OAUTH_CLIENT_ID`, `GITHUB_OAUTH_CLIENT_SECRET` | GitHub sign-in |
| `FACEBOOK_OAUTH_CLIENT_ID`, `FACEBOOK_OAUTH_CLIENT_SECRET` | Facebook sign-in |
| `TELEGRAM_BOT_TOKEN`, `TELEGRAM_BOT_USERNAME` | Telegram sign-in |

From the repository root:

```powershell
.\gradlew.bat -p microservice/iam-service :iam-main:bootRun
```

Open `http://localhost:9000/login`. For local Google/GitHub/Facebook sign-in, configure the provider redirect URI as `http://localhost:9000/login/oauth2/code/{registrationId}`. Telegram requires a public HTTPS URL: set the exact public hostname as the bot's linked domain in BotFather and open the login page on that same hostname. Its callback path is `/login/telegram/callback`. The default `dev` profile uses a development signing key; configure `IAM_JWK_PRIVATE_KEY` with a persistent RSA PKCS#8 key outside development.

Liquibase applies the schema and default roles/permissions at startup. Import `postman/iam-service.postman_collection.json` to try registration, client-credentials tokens, protected API calls, and dynamic client registration.

## Check changes

```powershell
.\gradlew.bat -p microservice/iam-service :iam-web:generateJte :iam-web:compileJava
.\gradlew.bat -p microservice/iam-service :iam-main:test
```

The application tests need the configured PostgreSQL database. The web module currently has no dedicated tests; template generation and compilation check the JTE views and their bindings. Tailwind CSS and Archivo currently load from CDNs, so these pages require internet access; production deployment should serve pinned local assets instead.
