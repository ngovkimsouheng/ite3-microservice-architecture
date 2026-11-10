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
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Bricolage+Grotesque:wght@400;500;600;700&family=Noto+Sans+Khmer:wght@400;500;600;700&display=swap" rel="stylesheet">
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="/css/page-typography.css">
</head>
<body class="min-h-screen bg-white px-5 py-8 text-zinc-950 antialiased sm:px-8" style="font-family: 'Bricolage Grotesque', 'Noto Sans Khmer', sans-serif">
<main class="mx-auto flex min-h-[calc(100vh-4rem)] w-full max-w-2xl items-center justify-center">
    <section class="w-full border border-zinc-200 bg-white p-7 shadow-2xl shadow-zinc-900/5 sm:p-12">
        <header class="mb-10 flex flex-col-reverse justify-between gap-6 sm:flex-row sm:items-start">
            <div>
                <h1 class="whitespace-nowrap text-2xl font-semibold leading-tight tracking-[-0.04em] sm:text-4xl">${messages.get("register.title")}</h1>
            </div>
            <nav class="flex justify-end gap-3 text-xs uppercase tracking-[0.1em] text-zinc-500" aria-label="${messages.get("common.language")}">
                <a class="hover:text-black focus-visible:outline focus-visible:outline-2 focus-visible:outline-black" href="?lang=en">${messages.get("common.english")}</a>
                <span aria-hidden="true">/</span>
                <a class="hover:text-black focus-visible:outline focus-visible:outline-2 focus-visible:outline-black" href="?lang=km">${messages.get("common.khmer")}</a>
            </nav>
        </header>
        @if (!errors.isEmpty())
            <div class="mb-5 border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800" role="alert">
                <p class="font-semibold">${messages.get("register.error")}</p>
                <ul class="mt-2 list-inside list-disc">@for (String error : errors)<li>${error}</li>@endfor</ul>
            </div>
        @endif
        <form class="grid gap-6" action="/register" method="post">
            @if (!csrfToken.isEmpty())
                <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
            @endif
            <input class="min-h-12 w-full border border-zinc-200 bg-zinc-50 px-4 py-3 text-sm outline-none transition placeholder:text-zinc-500 hover:border-zinc-400 focus:border-black focus:ring-1 focus:ring-black" id="username" name="username" value="${form.username()}" placeholder="${messages.get("register.username")}" aria-label="${messages.get("register.username")}" autocomplete="username" required>
            <input class="min-h-12 w-full border border-zinc-200 bg-zinc-50 px-4 py-3 text-sm outline-none transition placeholder:text-zinc-500 hover:border-zinc-400 focus:border-black focus:ring-1 focus:ring-black" id="email" type="email" name="email" value="${form.email()}" placeholder="${messages.get("register.email")}" aria-label="${messages.get("register.email")}" autocomplete="email" required>
            <div class="grid gap-4 sm:grid-cols-2">
                <input class="min-h-12 w-full border border-zinc-200 bg-zinc-50 px-4 py-3 text-sm outline-none transition placeholder:text-zinc-500 hover:border-zinc-400 focus:border-black focus:ring-1 focus:ring-black" id="givenName" name="givenName" value="${form.givenName()}" placeholder="${messages.get("register.givenName")}" aria-label="${messages.get("register.givenName")}" autocomplete="given-name" required>
                <input class="min-h-12 w-full border border-zinc-200 bg-zinc-50 px-4 py-3 text-sm outline-none transition placeholder:text-zinc-500 hover:border-zinc-400 focus:border-black focus:ring-1 focus:ring-black" id="familyName" name="familyName" value="${form.familyName()}" placeholder="${messages.get("register.familyName")}" aria-label="${messages.get("register.familyName")}" autocomplete="family-name" required>
            </div>
            <div class="grid gap-4 sm:grid-cols-2">
                <input class="min-h-12 w-full border border-zinc-200 bg-zinc-50 px-4 py-3 text-sm outline-none transition placeholder:text-zinc-500 hover:border-zinc-400 focus:border-black focus:ring-1 focus:ring-black" id="password" type="password" name="password" placeholder="${messages.get("register.password")}" aria-label="${messages.get("register.password")}" autocomplete="new-password" required>
                <input class="min-h-12 w-full border border-zinc-200 bg-zinc-50 px-4 py-3 text-sm outline-none transition placeholder:text-zinc-500 hover:border-zinc-400 focus:border-black focus:ring-1 focus:ring-black" id="confirmPassword" type="password" name="confirmPassword" placeholder="${messages.get("register.confirmPassword")}" aria-label="${messages.get("register.confirmPassword")}" autocomplete="new-password" required>
            </div>
            <div class="grid gap-2">
                <span class="text-sm font-medium text-zinc-600">${messages.get("register.locale")}</span>
                <input type="hidden" id="locale" name="locale" value="${form.locale()}">
                <div class="relative">
                    <button class="flex min-h-12 w-full items-center justify-between border border-zinc-200 bg-zinc-50 px-4 py-3 text-left text-sm text-zinc-950 transition hover:border-zinc-400 focus:border-black focus:outline-none focus:ring-1 focus:ring-black" id="locale-trigger" type="button" aria-haspopup="listbox" aria-expanded="false" aria-controls="locale-options">
                        <span id="locale-label">@if ("en".equals(form.locale()))${messages.get("common.english")}@else${messages.get("common.khmer")}@endif</span>
                        <svg class="h-4 w-4 transition-transform" id="locale-chevron" viewBox="0 0 20 20" fill="none" aria-hidden="true"><path d="m5 7.5 5 5 5-5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
                    </button>
                    <div class="absolute left-0 right-0 top-full z-10 mt-2 hidden border border-zinc-200 bg-white p-1 shadow-xl shadow-zinc-900/10" id="locale-options" role="listbox" aria-label="${messages.get("register.locale")}">
                        <button class="block w-full px-4 py-3 text-left text-sm transition hover:bg-zinc-100 focus:bg-zinc-100 focus:outline-none" type="button" role="option" data-locale="en" aria-selected="${"en".equals(form.locale())}">${messages.get("common.english")}</button>
                        <button class="block w-full px-4 py-3 text-left text-sm transition hover:bg-zinc-100 focus:bg-zinc-100 focus:outline-none" type="button" role="option" data-locale="km" aria-selected="${"km".equals(form.locale())}">${messages.get("common.khmer")}</button>
                    </div>
                </div>
            </div>
            <div class="mt-3 flex flex-wrap gap-4">
                <button class="inline-flex min-h-12 flex-1 items-center justify-center bg-black px-5 py-3 text-xs font-bold uppercase tracking-[0.2em] text-white transition hover:bg-zinc-800 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-black" type="submit">${messages.get("register.submit")}</button>
                <a class="inline-flex min-h-12 items-center justify-center border border-zinc-200 px-5 py-3 text-xs font-semibold uppercase tracking-wider text-zinc-700 transition hover:border-zinc-500 hover:bg-zinc-50 hover:text-black focus-visible:outline focus-visible:outline-2 focus-visible:outline-black" href="/login">${messages.get("common.back")}</a>
            </div>
        </form>
    </section>
</main>
<script>
    const localeTrigger = document.getElementById("locale-trigger");
    const localeOptions = document.getElementById("locale-options");
    const localeValue = document.getElementById("locale");
    const localeLabel = document.getElementById("locale-label");
    const localeChevron = document.getElementById("locale-chevron");
    const localeChoices = Array.from(localeOptions.querySelectorAll('[role="option"]'));

    function setLocaleMenuOpen(open) {
        localeTrigger.setAttribute("aria-expanded", String(open));
        localeOptions.classList.toggle("hidden", !open);
        localeChevron.classList.toggle("rotate-180", open);
        if (open) {
            (localeChoices.find(choice => choice.dataset.locale === localeValue.value) || localeChoices[0]).focus();
        }
    }

    localeTrigger.addEventListener("click", () => {
        setLocaleMenuOpen(localeTrigger.getAttribute("aria-expanded") !== "true");
    });

    localeChoices.forEach((choice, index) => {
        choice.addEventListener("click", () => {
            localeValue.value = choice.dataset.locale;
            localeLabel.textContent = choice.textContent;
            localeChoices.forEach(option => option.setAttribute("aria-selected", String(option === choice)));
            setLocaleMenuOpen(false);
            localeTrigger.focus();
        });
        choice.addEventListener("keydown", event => {
            if (event.key === "Escape") {
                setLocaleMenuOpen(false);
                localeTrigger.focus();
            } else if (event.key === "ArrowDown" || event.key === "ArrowUp") {
                event.preventDefault();
                const direction = event.key === "ArrowDown" ? 1 : -1;
                localeChoices[(index + direction + localeChoices.length) % localeChoices.length].focus();
            }
        });
    });
</script>
</body>
</html>
