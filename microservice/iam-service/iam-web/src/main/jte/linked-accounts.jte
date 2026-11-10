@param java.util.Map<String, String> messages
@param String currentLanguage
@param String csrfParameterName
@param String csrfToken
@param java.util.List<kh.edu.istad.iam.domain.dto.IdentityResult> identities
<!doctype html>
<html lang="${currentLanguage}">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${messages.get("accounts.title")}</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Bricolage+Grotesque:wght@400;500;600;700&family=Noto+Sans+Khmer:wght@400;500;600;700&display=swap" rel="stylesheet">
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="/css/page-typography.css">
</head>
<body class="min-h-screen bg-white px-5 py-8 text-zinc-950 antialiased sm:px-8" style="font-family: 'Bricolage Grotesque', 'Noto Sans Khmer', sans-serif">
<main class="mx-auto flex min-h-[calc(100vh-4rem)] w-full max-w-3xl flex-col items-center justify-center">
    <nav class="mb-4 flex w-full justify-end gap-3 px-1 text-xs uppercase tracking-[0.1em] text-zinc-500" aria-label="${messages.get("common.language")}">
        <a class="hover:text-black focus-visible:outline focus-visible:outline-2 focus-visible:outline-black" href="?lang=en">${messages.get("common.english")}</a>
        <span aria-hidden="true">/</span>
        <a class="hover:text-black focus-visible:outline focus-visible:outline-2 focus-visible:outline-black" href="?lang=km">${messages.get("common.khmer")}</a>
    </nav>
    <section class="w-full border border-zinc-200 bg-white p-6 shadow-2xl shadow-zinc-900/5 sm:p-10">
        <header class="mb-8">
            <h1 class="text-4xl font-semibold uppercase leading-none tracking-[-0.06em]">${messages.get("accounts.title")}</h1>
        </header>
        <section>
            <h2 class="mb-3 text-xs font-semibold uppercase tracking-[0.2em] text-zinc-500">${messages.get("common.connectedMethods")}</h2>
            @if (identities.isEmpty())
                <p class="text-description border border-dashed border-zinc-300 bg-zinc-50 px-4 py-5 leading-6 text-zinc-600">${messages.get("accounts.empty")}</p>
            @else
                <div class="divide-y divide-zinc-200 border-y border-zinc-200">
                    @for (kh.edu.istad.iam.domain.dto.IdentityResult identity : identities)
                        <article class="flex flex-col gap-4 py-4 sm:flex-row sm:items-center sm:justify-between">
                            <div class="min-w-0"><p class="text-sm font-semibold text-zinc-900">${identity.provider()}</p><p class="mt-1 break-all text-sm text-zinc-500">${identity.email()}</p></div>
                            <form class="shrink-0" action="/profile/accounts/${identity.uuid().toString()}/unlink" method="post">
                                @if (!csrfToken.isEmpty())
                                    <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
                                @endif
                                <button class="inline-flex min-h-10 items-center justify-center border border-zinc-200 px-4 py-2 text-xs font-semibold uppercase tracking-wider text-zinc-600 transition hover:border-red-700 hover:bg-red-700 hover:text-white focus-visible:outline focus-visible:outline-2 focus-visible:outline-red-700" type="submit">${messages.get("accounts.unlink")}</button>
                            </form>
                        </article>
                    @endfor
                </div>
            @endif
        </section>
        <section class="mt-8 border-t border-zinc-200 pt-6">
            <h2 class="text-xs font-semibold uppercase tracking-[0.2em] text-zinc-500">${messages.get("accounts.link")}</h2>
            <div class="mt-3 grid divide-y divide-zinc-200 border-y border-zinc-200 sm:grid-cols-2 sm:divide-x sm:divide-y-0">
                <form class="sm:border-b sm:border-zinc-200" action="/profile/accounts/link/google" method="post">
                    @if (!csrfToken.isEmpty())<input type="hidden" name="${csrfParameterName}" value="${csrfToken}">@endif
                    <button class="flex min-h-14 w-full items-center justify-between px-3 py-3 text-left text-sm font-medium text-zinc-800 transition hover:bg-zinc-50 focus-visible:outline focus-visible:outline-2 focus-visible:outline-inset focus-visible:outline-black" type="submit"><span>${messages.get("provider.google")}</span><span aria-hidden="true">+</span></button>
                </form>
                <form action="/profile/accounts/link/github" method="post">
                    @if (!csrfToken.isEmpty())<input type="hidden" name="${csrfParameterName}" value="${csrfToken}">@endif
                    <button class="flex min-h-14 w-full items-center justify-between px-3 py-3 text-left text-sm font-medium text-zinc-800 transition hover:bg-zinc-50 focus-visible:outline focus-visible:outline-2 focus-visible:outline-inset focus-visible:outline-black" type="submit"><span>${messages.get("provider.github")}</span><span aria-hidden="true">+</span></button>
                </form>
                <form class="sm:border-t sm:border-zinc-200" action="/profile/accounts/link/facebook" method="post">
                    @if (!csrfToken.isEmpty())<input type="hidden" name="${csrfParameterName}" value="${csrfToken}">@endif
                    <button class="flex min-h-14 w-full items-center justify-between px-3 py-3 text-left text-sm font-medium text-zinc-800 transition hover:bg-zinc-50 focus-visible:outline focus-visible:outline-2 focus-visible:outline-inset focus-visible:outline-black" type="submit"><span>${messages.get("provider.facebook")}</span><span aria-hidden="true">+</span></button>
                </form>
                <form class="sm:border-t sm:border-zinc-200" action="/profile/accounts/link/telegram" method="post">
                    @if (!csrfToken.isEmpty())<input type="hidden" name="${csrfParameterName}" value="${csrfToken}">@endif
                    <button class="flex min-h-14 w-full items-center justify-between px-3 py-3 text-left text-sm font-medium text-zinc-800 transition hover:bg-zinc-50 focus-visible:outline focus-visible:outline-2 focus-visible:outline-inset focus-visible:outline-black" type="submit"><span>${messages.get("provider.telegram")}</span><span aria-hidden="true">+</span></button>
                </form>
            </div>
        </section>
        <div class="mt-7"><a class="inline-flex min-h-11 items-center justify-center border border-zinc-200 px-4 py-2 text-xs font-semibold uppercase tracking-wider text-zinc-700 transition hover:border-zinc-500 hover:bg-zinc-50 hover:text-black focus-visible:outline focus-visible:outline-2 focus-visible:outline-black" href="/">${messages.get("common.back")}</a></div>
    </section>
</main>
</body>
</html>
