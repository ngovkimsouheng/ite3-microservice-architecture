@param java.util.Map<String, String> messages
@param String currentLanguage
@param String csrfParameterName
@param String csrfToken
@param String clientId
@param String state
@param java.util.List<String> requestedScopes
<!doctype html>
<html lang="${currentLanguage}">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${messages.get("consent.title")}</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Archivo:wght@400;500;600;700&display=swap" rel="stylesheet">
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="min-h-screen bg-white px-5 py-8 text-zinc-950 antialiased sm:px-8" style="font-family: 'Archivo', 'Archivo Fallback', sans-serif">
<main class="mx-auto flex min-h-[calc(100vh-4rem)] w-full max-w-xl items-center justify-center">
    <section class="w-full border border-zinc-200 bg-white p-6 shadow-2xl shadow-zinc-900/5 sm:p-10">
        <header class="mb-8 flex flex-col-reverse justify-between gap-5 sm:flex-row sm:items-start">
            <div><p class="mb-3 text-xs font-medium uppercase tracking-[0.35em] text-zinc-500">ISTAD · IDENTITY</p><h1 class="text-4xl font-semibold uppercase leading-none tracking-[-0.06em]">${messages.get("consent.title")}</h1></div>
            <nav class="flex justify-end gap-3 text-xs uppercase tracking-[0.2em] text-zinc-500" aria-label="${messages.get("common.language")}"><a class="hover:text-black focus-visible:outline focus-visible:outline-2 focus-visible:outline-black" href="?lang=en">${messages.get("common.english")}</a><span aria-hidden="true">/</span><a class="hover:text-black focus-visible:outline focus-visible:outline-2 focus-visible:outline-black" href="?lang=km">${messages.get("common.khmer")}</a></nav>
        </header>
        <p class="mb-3 text-sm text-zinc-600">${messages.get("consent.requested")}</p>
        <p class="mb-6"><code class="inline-block max-w-full break-all border border-zinc-200 bg-zinc-50 px-3 py-2 font-mono text-sm text-zinc-800">${clientId}</code></p>
        <form class="grid gap-6" action="/oauth2/authorize" method="post">
            @if (!csrfToken.isEmpty())
                <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
            @endif
            <input type="hidden" name="client_id" value="${clientId}">
            <input type="hidden" name="state" value="${state}">
            <fieldset>
                <legend class="mb-3 text-xs font-semibold uppercase tracking-[0.2em] text-zinc-500">${messages.get("common.permissionsRequested")}</legend>
                <div class="divide-y divide-zinc-200 border-y border-zinc-200">
                    @for (String scope : requestedScopes)
                        <label class="flex min-h-12 cursor-pointer items-center gap-3 px-3 py-3 text-sm text-zinc-800 hover:bg-zinc-50"><input class="h-4 w-4 accent-black" type="checkbox" name="scope" value="${scope}" checked>${scope}</label>
                    @endfor
                </div>
            </fieldset>
            <div class="grid gap-3 sm:grid-cols-2">
                <button class="inline-flex min-h-12 items-center justify-center bg-black px-5 py-3 text-xs font-bold uppercase tracking-[0.2em] text-white transition hover:bg-zinc-800 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-black" type="submit" name="user_oauth_approval" value="true">${messages.get("consent.allow")}</button>
                <button class="inline-flex min-h-12 items-center justify-center border border-zinc-200 bg-white px-5 py-3 text-xs font-semibold uppercase tracking-wider text-zinc-700 transition hover:border-zinc-500 hover:bg-zinc-50 hover:text-black focus-visible:outline focus-visible:outline-2 focus-visible:outline-black" type="submit" name="user_oauth_approval" value="false">${messages.get("consent.cancel")}</button>
            </div>
        </form>
    </section>
</main>
</body>
</html>
