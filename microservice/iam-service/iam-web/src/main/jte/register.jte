@param java.util.Map<String, String> messages
@param String currentLanguage
@param String csrfParameterName
@param String csrfToken
@param kh.edu.istad.iam.web.RegisterForm form
@param java.util.List<String> errors
<!doctype html>
<html lang="${currentLanguage}">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${messages.get("register.title")}</title>
    <link rel="stylesheet" href="/css/iam.css">
</head>
<body>
<main class="page-card page-card-narrow">
    <header class="page-header">
        <h1>${messages.get("register.title")}</h1>
        <nav class="language-switch" aria-label="Language">
            <a href="?lang=en">${messages.get("common.english")}</a><span aria-hidden="true">·</span>
            <a href="?lang=km">${messages.get("common.khmer")}</a>
        </nav>
    </header>
    @if (!errors.isEmpty())
        <div class="notice notice-error" role="alert">
            <p>${messages.get("register.error")}</p>
            <ul class="error-list">
                @for (String error : errors)<li>${error}</li>@endfor
            </ul>
        </div>
    @endif
    <form class="form-grid" action="/register" method="post">
        @if (!csrfToken.isEmpty())
            <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
        @endif
        <div class="field"><label for="username">${messages.get("register.username")}</label><input id="username" name="username" value="${form.username()}" autocomplete="username" required></div>
        <div class="field"><label for="email">${messages.get("register.email")}</label><input id="email" type="email" name="email" value="${form.email()}" autocomplete="email" required></div>
        <div class="field"><label for="givenName">${messages.get("register.givenName")}</label><input id="givenName" name="givenName" value="${form.givenName()}" autocomplete="given-name" required></div>
        <div class="field"><label for="familyName">${messages.get("register.familyName")}</label><input id="familyName" name="familyName" value="${form.familyName()}" autocomplete="family-name" required></div>
        <div class="field"><label for="password">${messages.get("register.password")}</label><input id="password" type="password" name="password" autocomplete="new-password" required></div>
        <div class="field"><label for="confirmPassword">${messages.get("register.confirmPassword")}</label><input id="confirmPassword" type="password" name="confirmPassword" autocomplete="new-password" required></div>
        <div class="field"><label for="locale">${messages.get("register.locale")}</label>
            <select id="locale" name="locale">
                <option value="en">${messages.get("common.english")}</option>
                <option value="km">${messages.get("common.khmer")}</option>
            </select>
        </div>
        <div class="actions">
            <button type="submit">${messages.get("register.submit")}</button>
            <a class="button button-secondary" href="/login">${messages.get("common.back")}</a>
        </div>
    </form>
</main>
</body>
</html>
