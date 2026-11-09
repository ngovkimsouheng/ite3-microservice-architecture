@param java.util.Map<String, String> messages
@param String currentLanguage
@param String csrfParameterName
@param String csrfToken
@param java.util.List<String> errors
<!doctype html>
<html lang="${currentLanguage}">
<head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>${messages.get("password.title")}</title><link rel="stylesheet" href="/css/iam.css"></head>
<body>
<main class="page-card page-card-narrow">
    <header class="page-header">
        <h1>${messages.get("password.title")}</h1>
        <nav class="language-switch" aria-label="Language">
            <a href="?lang=en">${messages.get("common.english")}</a><span aria-hidden="true">·</span>
            <a href="?lang=km">${messages.get("common.khmer")}</a>
        </nav>
    </header>
    @if (!errors.isEmpty())
        <div class="notice notice-error" role="alert"><ul class="error-list">@for (String error : errors)<li>${error}</li>@endfor</ul></div>
    @endif
    <form class="form-grid" action="/profile/password" method="post">
        @if (!csrfToken.isEmpty())
            <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
        @endif
        <div class="field"><label for="currentPassword">${messages.get("password.current")}</label><input id="currentPassword" type="password" name="currentPassword" autocomplete="current-password" required></div>
        <div class="field"><label for="newPassword">${messages.get("password.new")}</label><input id="newPassword" type="password" name="newPassword" autocomplete="new-password" required></div>
        <div class="field"><label for="confirmPassword">${messages.get("password.confirm")}</label><input id="confirmPassword" type="password" name="confirmPassword" autocomplete="new-password" required></div>
        <div class="actions">
            <button type="submit">${messages.get("password.submit")}</button>
            <a class="button button-secondary" href="/">${messages.get("common.back")}</a>
        </div>
    </form>
</main>
</body>
</html>
