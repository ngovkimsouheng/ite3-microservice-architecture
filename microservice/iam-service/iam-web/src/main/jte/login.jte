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
</head>
<body>
<main>
    <nav><a href="?lang=en">${messages.get("common.english")}</a> | <a href="?lang=km">${messages.get("common.khmer")}</a></nav>
    <h1>${messages.get("login.title")}</h1>
    @if (error)
        <p role="alert">${messages.get("login.error")}</p>
    @endif
    @if (registered)
        <p role="status">${messages.get("status.registered")}</p>
    @endif
    <form action="/login" method="post">
        @if (!csrfToken.isEmpty())
            <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
        @endif
        <label for="username">${messages.get("login.username")}</label>
        <input id="username" name="username" autocomplete="username" required>
        <label for="password">${messages.get("login.password")}</label>
        <input id="password" type="password" name="password" autocomplete="current-password" required>
        <button type="submit">${messages.get("login.submit")}</button>
    </form>
    <p>${messages.get("login.continue")}</p>
    @if (!telegramLinkMode)
        <ul>
            <li><a href="/oauth2/authorization/google">${messages.get("provider.google")}</a></li>
            <li><a href="/oauth2/authorization/github">${messages.get("provider.github")}</a></li>
            <li><a href="/oauth2/authorization/facebook">${messages.get("provider.facebook")}</a></li>
        </ul>
    @endif
    @if (telegramBotUsername != null && !telegramBotUsername.isBlank())
        <p>${messages.get("login.telegram")}</p>
        <script async src="https://telegram.org/js/telegram-widget.js?22"
                data-telegram-login="${telegramBotUsername}"
                data-size="large"
                data-auth-url="/login/telegram/callback"
                data-request-access="write"></script>
    @endif
    <p><a href="/register">${messages.get("register.title")}</a></p>
</main>
</body>
</html>
