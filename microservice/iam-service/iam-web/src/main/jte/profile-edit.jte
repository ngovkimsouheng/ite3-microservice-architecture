@param java.util.Map<String, String> messages
@param String currentLanguage
@param String csrfParameterName
@param String csrfToken
@param kh.edu.istad.iam.domain.dto.UserResult user
@param java.util.List<String> errors
<!doctype html>
<html lang="${currentLanguage}">
<head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>${messages.get("profile.edit")}</title></head>
<body>
<main>
    <h1>${messages.get("profile.edit")}</h1>
    @if (!errors.isEmpty())
        <ul role="alert">
            @for (String error : errors)
                <li>${error}</li>
            @endfor
        </ul>
    @endif
    <form action="/profile/edit" method="post">
        @if (!csrfToken.isEmpty())
            <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
        @endif
        <label>${messages.get("profile.givenName")}<input name="givenName" value="${user.givenName()}" required></label>
        <label>${messages.get("profile.nickname")}<input name="nickname" value="${user.nickname()}"></label>
        <label>${messages.get("profile.phone")}<input name="phoneNumber" value="${user.phoneNumber()}"></label>
        <label>${messages.get("profile.locale")}
            <select name="locale"><option value="en">${messages.get("common.english")}</option><option value="km">${messages.get("common.khmer")}</option></select>
        </label>
        <button type="submit">${messages.get("common.save")}</button>
    </form>
    <a href="/">${messages.get("common.back")}</a>
</main>
</body>
</html>
