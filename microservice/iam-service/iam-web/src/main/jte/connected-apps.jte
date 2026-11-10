@param java.util.Map<String, String> messages
@param String currentLanguage
@param String csrfParameterName
@param String csrfToken
@param java.util.List<kh.edu.istad.iam.domain.dto.ConnectedAppResult> apps
<!doctype html>
<html lang="${currentLanguage}">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${messages.get("apps.title")}</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Archivo:wght@400;500;600;700&display=swap" rel="stylesheet">
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="min-h-screen bg-white px-5 py-8 text-zinc-950 antialiased sm:px-8" style="font-family: 'Archivo', 'Archivo Fallback', sans-serif">
<main class="mx-auto flex min-h-[calc(100vh-4rem)] w-full max-w-3xl items-center justify-center">
    <section class="w-full border border-zinc-200 bg-white p-6 shadow-2xl shadow-zinc-900/5 sm:p-10">
        <header class="mb-8 flex flex-col-reverse justify-between gap-5 sm:flex-row sm:items-start">
            <div><p class="mb-3 text-xs font-medium uppercase tracking-[0.35em] text-zinc-500">ISTAD · IDENTITY</p><h1 class="text-4xl font-semibold uppercase leading-none tracking-[-0.06em]">${messages.get("apps.title")}</h1></div>
            <nav class="flex justify-end gap-3 text-xs uppercase tracking-[0.2em] text-zinc-500" aria-label="Language"><a class="hover:text-black focus-visible:outline focus-visible:outline-2 focus-visible:outline-black" href="?lang=en">${messages.get("common.english")}</a><span aria-hidden="true">/</span><a class="hover:text-black focus-visible:outline focus-visible:outline-2 focus-visible:outline-black" href="?lang=km">${messages.get("common.khmer")}</a></nav>
        </header>
        <section>
            <h2 class="mb-3 text-xs font-semibold uppercase tracking-[0.2em] text-zinc-500">Authorized applications</h2>
            @if (apps.isEmpty())
                <p class="border border-dashed border-zinc-300 bg-zinc-50 px-4 py-5 text-sm leading-6 text-zinc-600">${messages.get("apps.empty")}</p>
            @else
                <div class="divide-y divide-zinc-200 border-y border-zinc-200">
                    @for (kh.edu.istad.iam.domain.dto.ConnectedAppResult app : apps)
                        <article class="flex flex-col gap-4 py-4 sm:flex-row sm:items-center sm:justify-between">
                            <div class="min-w-0"><p class="break-words text-sm font-semibold text-zinc-900">${app.clientName()}</p><p class="mt-1 break-words text-sm text-zinc-500">${messages.get("apps.scopes")}: ${String.join(", ", app.scopes())}</p></div>
                            <form class="shrink-0" action="/profile/apps/${app.clientId()}/revoke" method="post">
                                @if (!csrfToken.isEmpty())
                                    <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
                                @endif
                                <button class="inline-flex min-h-10 items-center justify-center border border-zinc-200 px-4 py-2 text-xs font-semibold uppercase tracking-wider text-zinc-600 transition hover:border-red-700 hover:bg-red-700 hover:text-white focus-visible:outline focus-visible:outline-2 focus-visible:outline-red-700" type="submit">${messages.get("apps.revoke")}</button>
                            </form>
                        </article>
                    @endfor
                </div>
            @endif
        </section>
        <div class="mt-7"><a class="inline-flex min-h-11 items-center justify-center border border-zinc-200 px-4 py-2 text-xs font-semibold uppercase tracking-wider text-zinc-700 transition hover:border-zinc-500 hover:bg-zinc-50 hover:text-black focus-visible:outline focus-visible:outline-2 focus-visible:outline-black" href="/">${messages.get("common.back")}</a></div>
    </section>
</main>
</body>
</html>
