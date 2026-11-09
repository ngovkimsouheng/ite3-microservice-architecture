@param java.util.Map<String, String> messages
@param String currentLanguage
@param String csrfParameterName
@param String csrfToken
@param boolean error
@param boolean registered
@param boolean telegramLinkMode
@param String telegramBotUsername
<!doctype html>
<html lang="${currentLanguage}">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${messages.get("login.title")}</title>
    <link rel="stylesheet" href="/css/iam.css">
</head>
<body>
<main class="page-card page-card-narrow">
    <nav class="language-switch" aria-label="Language">
        <a href="?lang=en">${messages.get("common.english")}</a>
        <span aria-hidden="true">·</span>
        <a href="?lang=km">${messages.get("common.khmer")}</a>
    </nav>

    <h1>${messages.get("login.title")}</h1>
    @if (error)
        <p class="notice notice-error" role="alert">${messages.get("login.error")}</p>
    @endif
    @if (registered)
        <p class="notice notice-success" role="status">${messages.get("status.registered")}</p>
    @endif

    <form class="form-grid" action="/login" method="post">
        @if (!csrfToken.isEmpty())
            <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
        @endif
        <div class="field">
            <label for="username">${messages.get("login.username")}</label>
            <input id="username" type="text" name="username" autocomplete="username" required autofocus>
        </div>
        <div class="field">
            <label for="password">${messages.get("login.password")}</label>
            <input id="password" type="password" name="password" autocomplete="current-password" required>
        </div>
        <button type="submit">${messages.get("login.submit")}</button>
    </form>

    @if (!telegramLinkMode)
        <div class="divider">${messages.get("login.continue")}</div>
        <div class="provider-grid">
            <a class="provider-button" href="/oauth2/authorization/github">
                <svg viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
                    <path d="M12 .5C5.65.5.5 5.65.5 12c0 5.08 3.29 9.39 7.86 10.91.58.1.79-.25.79-.56v-2c-3.2.7-3.88-1.54-3.88-1.54-.52-1.33-1.28-1.69-1.28-1.69-1.04-.71.08-.7.08-.7 1.15.08 1.76 1.18 1.76 1.18 1.03 1.76 2.69 1.25 3.35.96.1-.74.4-1.25.73-1.54-2.55-.29-5.24-1.28-5.24-5.69 0-1.26.45-2.28 1.18-3.09-.12-.29-.51-1.46.11-3.05 0 0 .97-.31 3.17 1.18a11 11 0 0 1 5.78 0c2.2-1.49 3.17-1.18 3.17-1.18.62 1.59.23 2.76.11 3.05.74.81 1.18 1.83 1.18 3.09 0 4.42-2.7 5.4-5.27 5.68.41.36.78 1.06.78 2.14v3.17c0 .31.21.67.8.56A11.5 11.5 0 0 0 23.5 12C23.5 5.65 18.35.5 12 .5Z"/>
                </svg>
                ${messages.get("provider.github")}
            </a>
            <a class="provider-button" href="/oauth2/authorization/google">
                <svg viewBox="0 0 24 24" aria-hidden="true">
                    <path fill="#4285F4" d="M23.5 12.27c0-.85-.08-1.67-.22-2.45H12v4.64h6.45a5.52 5.52 0 0 1-2.39 3.62v3h3.87c2.27-2.09 3.57-5.17 3.57-8.81Z"/>
                    <path fill="#34A853" d="M12 24c3.24 0 5.95-1.07 7.93-2.91l-3.87-3c-1.07.72-2.44 1.15-4.06 1.15-3.12 0-5.77-2.11-6.71-4.95H1.29v3.1A12 12 0 0 0 12 24Z"/>
                    <path fill="#FBBC05" d="M5.29 14.29a7.2 7.2 0 0 1 0-4.58v-3.1H1.29a12 12 0 0 0 0 10.78l4-3.1Z"/>
                    <path fill="#EA4335" d="M12 4.76c1.76 0 3.34.61 4.58 1.8l3.43-3.43C17.94 1.19 15.24 0 12 0A12 12 0 0 0 1.29 6.61l4 3.1C6.23 6.87 8.88 4.76 12 4.76Z"/>
                </svg>
                ${messages.get("provider.google")}
            </a>
            <a class="provider-button provider-wide" href="/oauth2/authorization/facebook">
                <svg viewBox="0 0 24 24" aria-hidden="true">
                    <path fill="#1877F2" d="M24 12a12 12 0 1 0-13.875 11.85v-8.38H7.08V12h3.045V9.36c0-3 1.79-4.66 4.52-4.66 1.31 0 2.68.235 2.68.235v2.95h-1.51c-1.49 0-1.955.925-1.955 1.875V12h3.33l-.532 3.47h-2.798v8.38A12 12 0 0 0 24 12Z"/>
                </svg>
                ${messages.get("provider.facebook")}
            </a>
        </div>
    @endif

    @if (telegramBotUsername != null && !telegramBotUsername.isBlank())
        <div class="telegram-widget">
            <p>${messages.get("login.telegram")}</p>
            <script async src="https://telegram.org/js/telegram-widget.js?22"
                    data-telegram-login="${telegramBotUsername}"
                    data-size="large"
                    data-auth-url="/login/telegram/callback"
                    data-request-access="write"></script>
        </div>
    @endif

    @if (!telegramLinkMode)
        <p class="page-footer"><a href="/register">${messages.get("register.title")}</a></p>
    @endif
</main>
</body>
</html>
