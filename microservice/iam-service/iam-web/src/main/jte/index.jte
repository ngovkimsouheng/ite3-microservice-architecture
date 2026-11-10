@param java.util.Map<String, String> messages
@param String currentLanguage
@param String csrfParameterName
@param String csrfToken
@param kh.edu.istad.iam.domain.dto.UserResult user
@param kh.edu.istad.iam.domain.dto.UserAuthoritiesResult authorities
<!doctype html>
<html lang="${currentLanguage}">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${messages.get("profile.title")}</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Bricolage+Grotesque:wght@400;500;600;700&family=Noto+Sans+Khmer:wght@400;500;600;700&display=swap" rel="stylesheet">
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="/css/page-typography.css">
</head>
<body class="min-h-screen bg-white px-5 py-10 text-zinc-950 antialiased sm:px-8" style="font-family: 'Bricolage Grotesque', 'Noto Sans Khmer', sans-serif">
<main class="mx-auto flex min-h-[calc(100vh-5rem)] w-full max-w-4xl items-center justify-center">
    <section class="border border-zinc-200 bg-white p-6 shadow-2xl shadow-zinc-900/5 sm:p-10">
        <header class="mb-8 flex flex-col-reverse justify-between gap-5 sm:flex-row sm:items-start">
            <div>
                <h1 class="text-4xl font-semibold uppercase leading-none tracking-[-0.06em] sm:text-5xl">${messages.get("profile.title")}</h1>
            </div>
            <nav class="flex justify-end gap-3 text-xs uppercase tracking-[0.2em] text-zinc-500" aria-label="${messages.get("common.language")}">
                <a class="transition hover:text-black focus-visible:outline focus-visible:outline-2 focus-visible:outline-black" href="?lang=en">${messages.get("common.english")}</a>
                <span aria-hidden="true">/</span>
                <a class="transition hover:text-black focus-visible:outline focus-visible:outline-2 focus-visible:outline-black" href="?lang=km">${messages.get("common.khmer")}</a>
            </nav>
        </header>

        <dl class="grid grid-cols-1 sm:grid-cols-[minmax(9rem,0.35fr)_minmax(0,1fr)]">
            <dt class="border-b border-zinc-100 py-3 text-sm text-zinc-500">${messages.get("profile.username")}</dt>
            <dd class="m-0 min-w-0 border-b border-zinc-100 bg-zinc-50 px-3 py-3 text-sm font-medium break-words">${user.username()}</dd>
            <dt class="border-b border-zinc-100 py-3 text-sm text-zinc-500">${messages.get("profile.email")}</dt>
            <dd class="m-0 min-w-0 border-b border-zinc-100 bg-zinc-50 px-3 py-3 text-sm font-medium break-words">${user.email()}</dd>
            <dt class="border-b border-zinc-100 py-3 text-sm text-zinc-500">${messages.get("profile.givenName")}</dt>
            <dd class="m-0 min-w-0 border-b border-zinc-100 bg-zinc-50 px-3 py-3 text-sm font-medium break-words">${user.givenName()}</dd>
            <dt class="border-b border-zinc-100 py-3 text-sm text-zinc-500">${messages.get("profile.familyName")}</dt>
            <dd class="m-0 min-w-0 border-b border-zinc-100 bg-zinc-50 px-3 py-3 text-sm font-medium break-words">${user.familyName()}</dd>
            <dt class="border-b border-zinc-100 py-3 text-sm text-zinc-500">${messages.get("profile.roles")}</dt>
            <dd class="m-0 min-w-0 border-b border-zinc-100 bg-zinc-50 px-3 py-3 text-sm font-medium break-words">${String.join(", ", authorities.roles())}</dd>
            <dt class="py-3 text-sm text-zinc-500">${messages.get("profile.permissions")}</dt>
            <dd class="m-0 min-w-0 bg-zinc-50 px-3 py-3 text-sm font-medium break-words">${String.join(", ", authorities.permissions())}</dd>
        </dl>

        <section class="mt-7 border-t border-zinc-200 pt-6">
            <h2 class="mb-4 text-xs font-semibold uppercase tracking-[0.2em] text-zinc-500">${messages.get("common.accountSettings")}</h2>
            <nav class="grid divide-y divide-zinc-200 border-y border-zinc-200 sm:grid-cols-2 sm:divide-x sm:divide-y-0" aria-label="${messages.get("profile.actions")}">
                <a class="flex min-h-14 items-center justify-between px-3 py-3 text-sm font-medium text-zinc-800 transition hover:bg-zinc-50 hover:text-black focus-visible:outline focus-visible:outline-2 focus-visible:outline-inset focus-visible:outline-black sm:border-b sm:border-zinc-200" href="/profile/edit"><span>${messages.get("profile.edit")}</span><span aria-hidden="true">→</span></a>
                <a class="flex min-h-14 items-center justify-between px-3 py-3 text-sm font-medium text-zinc-800 transition hover:bg-zinc-50 hover:text-black focus-visible:outline focus-visible:outline-2 focus-visible:outline-inset focus-visible:outline-black" href="/profile/password"><span>${messages.get("profile.password")}</span><span aria-hidden="true">→</span></a>
                <a class="flex min-h-14 items-center justify-between px-3 py-3 text-sm font-medium text-zinc-800 transition hover:bg-zinc-50 hover:text-black focus-visible:outline focus-visible:outline-2 focus-visible:outline-inset focus-visible:outline-black" href="/profile/accounts"><span>${messages.get("profile.linkedAccounts")}</span><span aria-hidden="true">→</span></a>
                <a class="flex min-h-14 items-center justify-between px-3 py-3 text-sm font-medium text-zinc-800 transition hover:bg-zinc-50 hover:text-black focus-visible:outline focus-visible:outline-2 focus-visible:outline-inset focus-visible:outline-black sm:border-t sm:border-zinc-200" href="/profile/apps"><span>${messages.get("profile.connectedApps")}</span><span aria-hidden="true">→</span></a>
            </nav>
        </section>
        <form class="mt-5" action="/logout" method="post">
            @if (!csrfToken.isEmpty())
                <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
            @endif
            <button class="inline-flex min-h-11 items-center justify-center border border-zinc-200 bg-white px-4 py-2 text-xs font-semibold uppercase tracking-wider text-zinc-600 transition hover:border-red-700 hover:bg-red-700 hover:text-white focus-visible:outline focus-visible:outline-2 focus-visible:outline-red-700" type="submit">${messages.get("common.logout")}</button>
        </form>
    </section>
</main>
</body>
</html>
