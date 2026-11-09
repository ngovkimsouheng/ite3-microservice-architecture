@param java.util.Map<String, String> messages
@param String currentLanguage
@param String csrfParameterName
@param String csrfToken
@param java.util.List<String> errors
<!doctype html>
<html lang="${currentLanguage}">
<head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>${messages.get("password.title")}</title></head>
<body>
<main>
    <h1>${messages.get("password.title")}</h1>
    @if (!errors.isEmpty())
        <ul role="alert">
            @for (String error : errors)
                <li>${error}</li>
            @endfor
        </ul>
    @endif
    <form action="/profile/password" method="post">
        @if (!csrfToken.isEmpty())
            <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
        @endif
        <label>${messages.get("password.current")}<input type="password" name="currentPassword" required></label>
        <label>${messages.get("password.new")}<input type="password" name="newPassword" required></label>
        <label>${messages.get("password.confirm")}<input type="password" name="confirmPassword" required></label>
        <button type="submit">${messages.get("password.submit")}</button>
    </form>
    <a href="/">${messages.get("common.back")}</a>
</main>
</body>
</html>
