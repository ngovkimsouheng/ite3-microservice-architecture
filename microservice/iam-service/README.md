# IAM Service

The IAM service is a Spring Authorization Server and identity-management application. It separates domain rules and use cases from persistence, HTTP, and browser adapters:

- `iam-domain/iam-domain-core` contains domain entities and invariants.
- `iam-domain/iam-application-service` contains use cases and outbound ports; shared domain primitives come from `istad-common-domain`.
- `iam-persistence` and `iam-infrastructure` implement database and security/provider adapters.
- `iam-rest-api` exposes the protected management API.
- `iam-web` contains the localized JTE browser application.
- `iam-main` wires the modules, migrations, and OAuth clients.

## Local setup

Run PostgreSQL with a database named `db_oauth2` on `localhost:9543`, then configure the database password, OAuth client secrets, and provider credentials in the environment. The service deliberately does not ship usable secrets.

Required variables:

| Variable | Purpose |
| --- | --- |
| `DATASOURCE_PASSWORD` | PostgreSQL password |
| `GOOGLE_OAUTH_CLIENT_ID`, `GOOGLE_OAUTH_CLIENT_SECRET` | Google sign-in |
| `GITHUB_OAUTH_CLIENT_ID`, `GITHUB_OAUTH_CLIENT_SECRET` | GitHub sign-in |
| `FACEBOOK_OAUTH_CLIENT_ID`, `FACEBOOK_OAUTH_CLIENT_SECRET` | Facebook sign-in |
| `REGISTRAR_CLIENT_SECRET`, `GATEWAY_CLIENT_SECRET`, `WEB_CLIENT_SECRET` | Internal OAuth clients |

For Telegram sign-in, also set `TELEGRAM_BOT_TOKEN` and `TELEGRAM_BOT_USERNAME`, and configure Telegram's login widget callback to `http://localhost:9000/login/telegram/callback`. Configure each OAuth provider's redirect URI as `http://localhost:9000/login/oauth2/code/{registrationId}`.

From the repository root, start the service with:

```powershell
.\gradlew.bat -p microservice/iam-service :iam-main:bootRun
```

Open `http://localhost:9000/login` (or `http://localhost:9000/login.html`). Both paths render the same sign-in page. The default `dev` profile generates a development-only signing key; configure `IAM_JWK_PRIVATE_KEY` with a persistent RSA PKCS#8 private key outside that profile.

## Authentication surfaces

- Browser accounts can sign in locally or through Google, GitHub, Facebook, and Telegram. Social identities may be linked from the authenticated profile; linking requests use CSRF protection and provider callbacks are validated before persistence.
- The REST API is stateless and accepts bearer access tokens under `/api/**`. API authentication and authorization errors use Problem Details responses.
- The authorization server exposes OAuth 2.0/OIDC endpoints, including dynamic client registration. The built-in web client requires PKCE and rotates refresh tokens.
- Telegram login signatures are checked with the bot token, limited to five minutes, and protected against replay using the database-backed replay table.

The first application start runs Liquibase migrations, including the default `USER`/`ADMIN` roles and permissions.

Import `postman/iam-service.postman_collection.json` to exercise public registration, gateway and registrar client-credentials tokens, the protected user list, and dynamic client registration. Set the two client-secret collection variables to the corresponding environment values before requesting tokens.
