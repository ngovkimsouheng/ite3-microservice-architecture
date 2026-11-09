@param java.util.Map<String, String> messages
@param String currentLanguage
@param String csrfParameterName
@param String csrfToken
@param String clientId
@param String state
@param java.util.List<String> requestedScopes
<!doctype html>
<html lang="${currentLanguage}">
<head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>${messages.get("consent.title")}</title></head>
<body>
<main>
    <h1>${messages.get("consent.title")}</h1>
    <p>${clientId}</p>
    <p>${messages.get("consent.requested")}</p>
    <form action="/oauth2/authorize" method="post">
        @if (!csrfToken.isEmpty())
            <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
        @endif
        <input type="hidden" name="client_id" value="${clientId}">
        <input type="hidden" name="state" value="${state}">
        @for (String scope : requestedScopes)
            <label><input type="checkbox" name="scope" value="${scope}" checked>${scope}</label>
        @endfor
        <button type="submit" name="user_oauth_approval" value="true">${messages.get("consent.allow")}</button>
        <button type="submit" name="user_oauth_approval" value="false">${messages.get("consent.cancel")}</button>
    </form>
</main>
</body>
</html>
