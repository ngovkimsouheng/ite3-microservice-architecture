@param java.util.Map<String, String> messages
@param String currentLanguage
@param String csrfParameterName
@param String csrfToken
@param kh.edu.istad.iam.domain.dto.UserResult user
@param java.util.List<String> errors
<!doctype html>
<html lang="${currentLanguage}">
<head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>${messages.get("profile.edit")}</title><link rel="stylesheet" href="/css/iam.css"></head>
<body>
<main class="page-card page-card-narrow">
    <header class="page-header">
        <h1>${messages.get("profile.edit")}</h1>
        <nav class="language-switch" aria-label="Language">
            <a href="?lang=en">${messages.get("common.english")}</a><span aria-hidden="true">·</span>
            <a href="?lang=km">${messages.get("common.khmer")}</a>
        </nav>
    </header>
    @if (!errors.isEmpty())
        <div class="notice notice-error" role="alert"><ul class="error-list">@for (String error : errors)<li>${error}</li>@endfor</ul></div>
    @endif
    <form class="form-grid" action="/profile/edit" method="post">
        @if (!csrfToken.isEmpty())
            <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
        @endif
        <div class="field"><label for="givenName">${messages.get("profile.givenName")}</label><input id="givenName" name="givenName" value="${user.givenName()}" autocomplete="given-name" required></div>
        <div class="field"><label for="nickname">${messages.get("profile.nickname")}</label><input id="nickname" name="nickname" value="${user.nickname()}"></div>
        <div class="field"><label for="phoneNumber">${messages.get("profile.phone")}</label><input id="phoneNumber" type="tel" name="phoneNumber" value="${user.phoneNumber()}" autocomplete="tel"></div>
        <div class="field"><label for="locale">${messages.get("profile.locale")}</label>
            <select id="locale" name="locale"><option value="en">${messages.get("common.english")}</option><option value="km">${messages.get("common.khmer")}</option></select>
        </div>
        <div class="actions">
            <button type="submit">${messages.get("common.save")}</button>
            <a class="button button-secondary" href="/">${messages.get("common.back")}</a>
        </div>
    </form>
</main>
</body>
</html>
