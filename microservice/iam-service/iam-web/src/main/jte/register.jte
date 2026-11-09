@param java.util.Map<String, String> messages
@param String currentLanguage
@param String csrfParameterName
@param String csrfToken
@param kh.edu.istad.iam.web.RegisterForm form
@param java.util.List<String> errors
<!doctype html>
<html lang="${currentLanguage}">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${messages.get("register.title")}</title>
</head>
<body>
<main>
    <nav><a href="?lang=en">${messages.get("common.english")}</a> | <a href="?lang=km">${messages.get("common.khmer")}</a></nav>
    <h1>${messages.get("register.title")}</h1>
    @if (!errors.isEmpty())
        <div role="alert">
            <p>${messages.get("register.error")}</p>
            <ul>
                @for (String error : errors)
                    <li>${error}</li>
                @endfor
            </ul>
        </div>
    @endif
    <form action="/register" method="post">
        @if (!csrfToken.isEmpty())
            <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
        @endif
        <label>${messages.get("register.username")}<input name="username" value="${form.username()}" required></label>
        <label>${messages.get("register.email")}<input type="email" name="email" value="${form.email()}" required></label>
        <label>${messages.get("register.givenName")}<input name="givenName" value="${form.givenName()}" required></label>
        <label>${messages.get("register.familyName")}<input name="familyName" value="${form.familyName()}" required></label>
        <label>${messages.get("register.password")}<input type="password" name="password" required></label>
        <label>${messages.get("register.confirmPassword")}<input type="password" name="confirmPassword" required></label>
        <label>${messages.get("register.locale")}
            <select name="locale">
                <option value="en">${messages.get("common.english")}</option>
                <option value="km">${messages.get("common.khmer")}</option>
            </select>
        </label>
        <button type="submit">${messages.get("register.submit")}</button>
    </form>
    <a href="/login">${messages.get("common.back")}</a>
</main>
</body>
</html>
