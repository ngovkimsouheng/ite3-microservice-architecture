@param java.util.Map<String, String> messages
@param String currentLanguage
@param String csrfParameterName
@param String csrfToken
@param kh.edu.istad.iam.domain.dto.UserResult user
@param kh.edu.istad.iam.domain.dto.UserAuthoritiesResult authorities
<!doctype html>
<html lang="${currentLanguage}">
<head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>${messages.get("profile.title")}</title><link rel="stylesheet" href="/css/iam.css"></head>
<body>
<main class="page-card page-card-wide">
    <header class="page-header">
        <h1>${messages.get("profile.title")}</h1>
        <nav class="language-switch" aria-label="Language">
            <a href="?lang=en">${messages.get("common.english")}</a><span aria-hidden="true">·</span>
            <a href="?lang=km">${messages.get("common.khmer")}</a>
        </nav>
    </header>
    <dl class="profile-details">
        <dt>${messages.get("profile.username")}</dt><dd>${user.username()}</dd>
        <dt>${messages.get("profile.email")}</dt><dd>${user.email()}</dd>
        <dt>${messages.get("profile.givenName")}</dt><dd>${user.givenName()}</dd>
        <dt>${messages.get("profile.familyName")}</dt><dd>${user.familyName()}</dd>
        <dt>${messages.get("profile.roles")}</dt><dd>${String.join(", ", authorities.roles())}</dd>
        <dt>${messages.get("profile.permissions")}</dt><dd>${String.join(", ", authorities.permissions())}</dd>
    </dl>
    <hr class="section-divider">
    <nav class="actions" aria-label="Profile actions">
        <a class="button button-secondary" href="/profile/edit">${messages.get("profile.edit")}</a>
        <a class="button button-secondary" href="/profile/password">${messages.get("profile.password")}</a>
        <a class="button button-secondary" href="/profile/accounts">${messages.get("profile.linkedAccounts")}</a>
        <a class="button button-secondary" href="/profile/apps">${messages.get("profile.connectedApps")}</a>
    </nav>
    <form class="actions" action="/logout" method="post">
        @if (!csrfToken.isEmpty())
            <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
        @endif
        <button class="button-secondary" type="submit">${messages.get("common.logout")}</button>
    </form>
</main>
</body>
</html>
