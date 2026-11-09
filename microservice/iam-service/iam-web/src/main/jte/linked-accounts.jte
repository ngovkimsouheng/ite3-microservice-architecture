@param java.util.Map<String, String> messages
@param String currentLanguage
@param String csrfParameterName
@param String csrfToken
@param java.util.List<kh.edu.istad.iam.domain.dto.IdentityResult> identities
<!doctype html>
<html lang="${currentLanguage}">
<head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>${messages.get("accounts.title")}</title></head>
<body>
<main>
    <h1>${messages.get("accounts.title")}</h1>
    @if (identities.isEmpty())
        <p>${messages.get("accounts.empty")}</p>
    @endif
    <ul>
        @for (kh.edu.istad.iam.domain.dto.IdentityResult identity : identities)
            <li>
                ${identity.provider()} — ${identity.email()}
                <form action="/profile/accounts/${identity.uuid().toString()}/unlink" method="post">
                    @if (!csrfToken.isEmpty())
                        <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
                    @endif
                    <button type="submit">${messages.get("accounts.unlink")}</button>
                </form>
            </li>
        @endfor
    </ul>
    <p>${messages.get("accounts.link")}</p>
    <ul>
        <li>
            <form action="/profile/accounts/link/google" method="post">
                @if (!csrfToken.isEmpty())
                    <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
                @endif
                <button type="submit">${messages.get("provider.google")}</button>
            </form>
        </li>
        <li>
            <form action="/profile/accounts/link/github" method="post">
                @if (!csrfToken.isEmpty())
                    <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
                @endif
                <button type="submit">${messages.get("provider.github")}</button>
            </form>
        </li>
        <li>
            <form action="/profile/accounts/link/facebook" method="post">
                @if (!csrfToken.isEmpty())
                    <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
                @endif
                <button type="submit">${messages.get("provider.facebook")}</button>
            </form>
        </li>
        <li>
            <form action="/profile/accounts/link/telegram" method="post">
                @if (!csrfToken.isEmpty())
                    <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
                @endif
                <button type="submit">${messages.get("provider.telegram")}</button>
            </form>
        </li>
    </ul>
    <a href="/">${messages.get("common.back")}</a>
</main>
</body>
</html>
