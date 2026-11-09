@param java.util.Map<String, String> messages
@param String currentLanguage
@param String csrfParameterName
@param String csrfToken
@param java.util.List<kh.edu.istad.iam.domain.dto.ConnectedAppResult> apps
<!doctype html>
<html lang="${currentLanguage}">
<head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>${messages.get("apps.title")}</title></head>
<body>
<main>
    <h1>${messages.get("apps.title")}</h1>
    @if (apps.isEmpty())
        <p>${messages.get("apps.empty")}</p>
    @endif
    <ul>
        @for (kh.edu.istad.iam.domain.dto.ConnectedAppResult app : apps)
            <li>
                <strong>${app.clientName()}</strong>
                <p>${messages.get("apps.scopes")}: ${String.join(", ", app.scopes())}</p>
                <form action="/profile/apps/${app.clientId()}/revoke" method="post">
                    @if (!csrfToken.isEmpty())
                        <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
                    @endif
                    <button type="submit">${messages.get("apps.revoke")}</button>
                </form>
            </li>
        @endfor
    </ul>
    <a href="/">${messages.get("common.back")}</a>
</main>
</body>
</html>
