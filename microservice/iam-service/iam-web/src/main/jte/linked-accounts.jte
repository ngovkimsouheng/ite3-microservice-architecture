@param java.util.Map<String, String> messages
@param String currentLanguage
@param String csrfParameterName
@param String csrfToken
@param java.util.List<kh.edu.istad.iam.domain.dto.IdentityResult> identities
<!doctype html>
<html lang="${currentLanguage}">
<head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>${messages.get("accounts.title")}</title><link rel="stylesheet" href="/css/iam.css"></head>
<body>
<main class="page-card page-card-wide">
    <header class="page-header">
        <h1>${messages.get("accounts.title")}</h1>
        <nav class="language-switch" aria-label="Language">
            <a href="?lang=en">${messages.get("common.english")}</a><span aria-hidden="true">·</span>
            <a href="?lang=km">${messages.get("common.khmer")}</a>
        </nav>
    </header>
    @if (identities.isEmpty())
        <p class="empty-state">${messages.get("accounts.empty")}</p>
    @endif
    <div class="item-list">
        @for (kh.edu.istad.iam.domain.dto.IdentityResult identity : identities)
            <article class="item-card">
                <div><strong>${identity.provider()}</strong><p>${identity.email()}</p></div>
                <form action="/profile/accounts/${identity.uuid().toString()}/unlink" method="post">
                    @if (!csrfToken.isEmpty())
                        <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
                    @endif
                    <button class="button-danger" type="submit">${messages.get("accounts.unlink")}</button>
                </form>
            </article>
        @endfor
    </div>
    <hr class="section-divider">
    <p class="intro-text">${messages.get("accounts.link")}</p>
    <div class="provider-grid">
        <div>
            <form action="/profile/accounts/link/google" method="post">
                @if (!csrfToken.isEmpty())
                    <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
                @endif
                <button class="button-secondary" type="submit">${messages.get("provider.google")}</button>
            </form>
        </div>
        <div>
            <form action="/profile/accounts/link/github" method="post">
                @if (!csrfToken.isEmpty())
                    <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
                @endif
                <button class="button-secondary" type="submit">${messages.get("provider.github")}</button>
            </form>
        </div>
        <div>
            <form action="/profile/accounts/link/facebook" method="post">
                @if (!csrfToken.isEmpty())
                    <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
                @endif
                <button class="button-secondary" type="submit">${messages.get("provider.facebook")}</button>
            </form>
        </div>
        <div>
            <form action="/profile/accounts/link/telegram" method="post">
                @if (!csrfToken.isEmpty())
                    <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
                @endif
                <button class="button-secondary" type="submit">${messages.get("provider.telegram")}</button>
            </form>
        </div>
    </div>
    <div class="actions"><a class="button button-secondary" href="/">${messages.get("common.back")}</a></div>
</main>
</body>
</html>
