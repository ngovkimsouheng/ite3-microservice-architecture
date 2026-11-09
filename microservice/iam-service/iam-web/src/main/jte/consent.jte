@param java.util.Map<String, String> messages
@param String currentLanguage
@param String csrfParameterName
@param String csrfToken
@param String clientId
@param String state
@param java.util.List<String> requestedScopes
<!doctype html>
<html lang="${currentLanguage}">
<head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>${messages.get("consent.title")}</title><link rel="stylesheet" href="/css/iam.css"></head>
<body>
<main class="page-card page-card-narrow">
    <header class="page-header">
        <h1>${messages.get("consent.title")}</h1>
        <nav class="language-switch" aria-label="Language">
            <a href="?lang=en">${messages.get("common.english")}</a><span aria-hidden="true">·</span>
            <a href="?lang=km">${messages.get("common.khmer")}</a>
        </nav>
    </header>
    <p><code class="client-id">${clientId}</code></p>
    <p class="intro-text">${messages.get("consent.requested")}</p>
    <form class="form-grid" action="/oauth2/authorize" method="post">
        @if (!csrfToken.isEmpty())
            <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
        @endif
        <input type="hidden" name="client_id" value="${clientId}">
        <input type="hidden" name="state" value="${state}">
        <div class="scope-list">
            @for (String scope : requestedScopes)
                <label><input type="checkbox" name="scope" value="${scope}" checked>${scope}</label>
            @endfor
        </div>
        <div class="actions">
            <button type="submit" name="user_oauth_approval" value="true">${messages.get("consent.allow")}</button>
            <button class="button-secondary" type="submit" name="user_oauth_approval" value="false">${messages.get("consent.cancel")}</button>
        </div>
    </form>
</main>
</body>
</html>
