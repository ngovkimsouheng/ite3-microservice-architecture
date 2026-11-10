@param java.util.Map<String, String> messages
@param String currentLanguage
@param String csrfParameterName
@param String csrfToken
@param java.util.List<String> errors
<!doctype html>
<html lang="${currentLanguage}">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${messages.get("password.title")}</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Bricolage+Grotesque:wght@400;500;600;700&family=Noto+Sans+Khmer:wght@400;500;600;700&display=swap" rel="stylesheet">
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="/css/page-typography.css">
</head>
<body class="min-h-screen bg-white px-5 py-8 text-zinc-950 antialiased sm:px-8" style="font-family: 'Bricolage Grotesque', 'Noto Sans Khmer', sans-serif">
<main class="mx-auto flex min-h-[calc(100vh-4rem)] w-full max-w-xl items-center justify-center">
    <section class="w-full border border-zinc-200 bg-white p-6 shadow-2xl shadow-zinc-900/5 sm:p-10">
        <header class="mb-8 flex flex-col-reverse justify-between gap-5 sm:flex-row sm:items-start">
            <div><h1 class="text-4xl font-semibold uppercase leading-none tracking-[-0.06em]">${messages.get("password.title")}</h1></div>
            <nav class="flex justify-end gap-3 text-xs uppercase tracking-[0.1em] text-zinc-500" aria-label="${messages.get("common.language")}"><a class="hover:text-black focus-visible:outline focus-visible:outline-2 focus-visible:outline-black" href="?lang=en">${messages.get("common.english")}</a><span aria-hidden="true">/</span><a class="hover:text-black focus-visible:outline focus-visible:outline-2 focus-visible:outline-black" href="?lang=km">${messages.get("common.khmer")}</a></nav>
        </header>
        @if (!errors.isEmpty())
            <div class="mb-5 border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800" role="alert"><ul class="list-inside list-disc">@for (String error : errors)<li>${error}</li>@endfor</ul></div>
        @endif
        <form class="grid gap-5" action="/profile/password" method="post">
            @if (!csrfToken.isEmpty())
                <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
            @endif
            <div class="grid gap-2"><label class="text-xs font-medium uppercase tracking-[0.16em] text-zinc-500" for="currentPassword">${messages.get("password.current")}</label><input class="min-h-12 w-full border border-zinc-200 bg-zinc-50 px-4 py-3 text-sm outline-none transition hover:border-zinc-400 focus:border-black focus:ring-1 focus:ring-black" id="currentPassword" type="password" name="currentPassword" autocomplete="current-password" required></div>
            <div class="grid gap-2"><label class="text-xs font-medium uppercase tracking-[0.16em] text-zinc-500" for="newPassword">${messages.get("password.new")}</label><input class="min-h-12 w-full border border-zinc-200 bg-zinc-50 px-4 py-3 text-sm outline-none transition hover:border-zinc-400 focus:border-black focus:ring-1 focus:ring-black" id="newPassword" type="password" name="newPassword" autocomplete="new-password" required></div>
            <div class="grid gap-2"><label class="text-xs font-medium uppercase tracking-[0.16em] text-zinc-500" for="confirmPassword">${messages.get("password.confirm")}</label><input class="min-h-12 w-full border border-zinc-200 bg-zinc-50 px-4 py-3 text-sm outline-none transition hover:border-zinc-400 focus:border-black focus:ring-1 focus:ring-black" id="confirmPassword" type="password" name="confirmPassword" autocomplete="new-password" required></div>
            <div class="mt-2 flex flex-wrap gap-3">
                <button class="inline-flex min-h-12 flex-1 items-center justify-center bg-black px-5 py-3 text-xs font-bold uppercase tracking-[0.2em] text-white transition hover:bg-zinc-800 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-black" type="submit">${messages.get("password.submit")}</button>
                <a class="inline-flex min-h-12 items-center justify-center border border-zinc-200 px-5 py-3 text-xs font-semibold uppercase tracking-wider text-zinc-700 transition hover:border-zinc-500 hover:bg-zinc-50 hover:text-black focus-visible:outline focus-visible:outline-2 focus-visible:outline-black" href="/">${messages.get("common.back")}</a>
            </div>
        </form>
    </section>
</main>
</body>
</html>
