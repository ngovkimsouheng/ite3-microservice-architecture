@param java.util.Map<String, String> messages
@param String currentLanguage
@param String csrfParameterName
@param String csrfToken
@param kh.edu.istad.iam.domain.dto.UserResult user
@param kh.edu.istad.iam.domain.dto.UserAuthoritiesResult authorities
<!doctype html>
<html lang="${currentLanguage}">
<head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>${messages.get("profile.title")}</title></head>
<body>
<main>
    <nav><a href="?lang=en">${messages.get("common.english")}</a> | <a href="?lang=km">${messages.get("common.khmer")}</a></nav>
    <h1>${messages.get("profile.title")}</h1>
    <dl>
        <dt>${messages.get("profile.username")}</dt><dd>${user.username()}</dd>
        <dt>${messages.get("profile.email")}</dt><dd>${user.email()}</dd>
        <dt>${messages.get("profile.givenName")}</dt><dd>${user.givenName()}</dd>
        <dt>${messages.get("profile.familyName")}</dt><dd>${user.familyName()}</dd>
        <dt>${messages.get("profile.roles")}</dt><dd>${String.join(", ", authorities.roles())}</dd>
        <dt>${messages.get("profile.permissions")}</dt><dd>${String.join(", ", authorities.permissions())}</dd>
    </dl>
    <nav>
        <a href="/profile/edit">${messages.get("profile.edit")}</a>
        <a href="/profile/password">${messages.get("profile.password")}</a>
        <a href="/profile/accounts">${messages.get("profile.linkedAccounts")}</a>
        <a href="/profile/apps">${messages.get("profile.connectedApps")}</a>
    </nav>
    <form action="/logout" method="post">
        @if (!csrfToken.isEmpty())
            <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
        @endif
        <button type="submit">${messages.get("common.logout")}</button>
    </form>
</main>
</body>
</html>
