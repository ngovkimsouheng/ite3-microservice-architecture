@param java.util.Map<String, String> messages
@param String currentLanguage
@param String csrfParameterName
@param String csrfToken
@param java.util.List<kh.edu.istad.iam.domain.dto.ConnectedAppResult> apps
<!doctype html>
<html lang="${currentLanguage}">
<head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>${messages.get("apps.title")}</title><link rel="stylesheet" href="/css/iam.css"></head>
<body>
<main class="page-card page-card-wide">
    <header class="page-header">
        <h1>${messages.get("apps.title")}</h1>
        <nav class="language-switch" aria-label="Language">
            <a href="?lang=en">${messages.get("common.english")}</a><span aria-hidden="true">·</span>
            <a href="?lang=km">${messages.get("common.khmer")}</a>
        </nav>
    </header>
    @if (apps.isEmpty())
        <p class="empty-state">${messages.get("apps.empty")}</p>
    @endif
    <div class="item-list">
        @for (kh.edu.istad.iam.domain.dto.ConnectedAppResult app : apps)
            <article class="item-card">
                <div>
                    <strong>${app.clientName()}</strong>
                    <p>${messages.get("apps.scopes")}: ${String.join(", ", app.scopes())}</p>
                </div>
                <form action="/profile/apps/${app.clientId()}/revoke" method="post">
                    @if (!csrfToken.isEmpty())
                        <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
                    @endif
                    <button class="button-danger" type="submit">${messages.get("apps.revoke")}</button>
                </form>
            </article>
        @endfor
    </div>
    <div class="actions"><a class="button button-secondary" href="/">${messages.get("common.back")}</a></div>
</main>
</body>
</html>
