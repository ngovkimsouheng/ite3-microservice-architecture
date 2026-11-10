@param java.util.Map<String, String> messages
@param String currentLanguage
@param String csrfParameterName
@param String csrfToken
@param boolean error
@param boolean registered
@param boolean telegramLinkMode
@param String telegramBotUsername
<!doctype html>
<html lang="${currentLanguage}">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${messages.get("login.title")}</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Bricolage+Grotesque:wght@400;500;600;700&family=Noto+Sans+Khmer:wght@400;500;600;700&display=swap" rel="stylesheet">
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="/css/page-typography.css">
</head>
<body class="min-h-screen bg-white px-5 py-10 text-zinc-950 antialiased sm:px-8" style="font-family: 'Bricolage Grotesque', 'Noto Sans Khmer', sans-serif">
<main class="mx-auto flex min-h-[calc(100vh-5rem)] w-full max-w-6xl items-center justify-center">
    <section class="w-full max-w-md border border-zinc-200 bg-white p-6 shadow-2xl shadow-zinc-900/5 sm:p-10">
        <nav class="mb-12 flex justify-end gap-3 text-xs uppercase tracking-[0.1em] text-zinc-500" aria-label="${messages.get("common.language")}">
            <a class="transition hover:text-black focus-visible:outline focus-visible:outline-2 focus-visible:outline-black" href="?lang=en">${messages.get("common.english")}</a>
            <span aria-hidden="true">/</span>
            <a class="transition hover:text-black focus-visible:outline focus-visible:outline-2 focus-visible:outline-black" href="?lang=km">${messages.get("common.khmer")}</a>
        </nav>

        <h1 class="mb-9 text-4xl font-semibold uppercase leading-none tracking-[-0.06em] sm:text-5xl">${messages.get("login.title")}</h1>
    @if (error)
        <p class="mb-5 border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800" role="alert">${messages.get("login.error")}</p>
    @endif
    @if (registered)
        <p class="mb-5 border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-800" role="status">${messages.get("status.registered")}</p>
    @endif

    <form class="grid gap-5" action="/login" method="post">
        @if (!csrfToken.isEmpty())
            <input type="hidden" name="${csrfParameterName}" value="${csrfToken}">
        @endif
        <input class="min-h-12 w-full border border-zinc-200 bg-zinc-50 px-4 py-3 text-sm text-zinc-950 outline-none transition placeholder:text-zinc-500 hover:border-zinc-400 focus:border-black focus:ring-1 focus:ring-black" id="username" type="text" name="username" placeholder="${messages.get("login.username")}" aria-label="${messages.get("login.username")}" autocomplete="username" required autofocus>
        <input class="min-h-12 w-full border border-zinc-200 bg-zinc-50 px-4 py-3 text-sm text-zinc-950 outline-none transition placeholder:text-zinc-500 hover:border-zinc-400 focus:border-black focus:ring-1 focus:ring-black" id="password" type="password" name="password" placeholder="${messages.get("login.password")}" aria-label="${messages.get("login.password")}" autocomplete="current-password" required>
        <button class="mt-1 min-h-12 w-full bg-black px-5 py-3 text-xs font-bold uppercase tracking-[0.2em] text-white transition hover:bg-zinc-800 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-black" type="submit">${messages.get("login.submit")}</button>
    </form>

    @if (!telegramLinkMode)
        <div class="my-8 flex items-center gap-4 text-[10px] font-medium uppercase tracking-[0.24em] text-zinc-400"><span class="h-px flex-1 bg-zinc-200"></span>${messages.get("login.continue")}<span class="h-px flex-1 bg-zinc-200"></span></div>
    @endif
    <div class="flex justify-center gap-3">
        @if (!telegramLinkMode)
            <a class="flex h-12 w-12 shrink-0 items-center justify-center border border-zinc-200 bg-white text-zinc-700 transition hover:border-zinc-500 hover:bg-zinc-50 hover:text-black focus-visible:outline focus-visible:outline-2 focus-visible:outline-black" href="/oauth2/authorization/github" aria-label="${messages.get("provider.github")}" title="${messages.get("provider.github")}">
                <svg class="h-5 w-5" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
                    <path d="M12 .5C5.65.5.5 5.65.5 12c0 5.08 3.29 9.39 7.86 10.91.58.1.79-.25.79-.56v-2c-3.2.7-3.88-1.54-3.88-1.54-.52-1.33-1.28-1.69-1.28-1.69-1.04-.71.08-.7.08-.7 1.15.08 1.76 1.18 1.76 1.18 1.03 1.76 2.69 1.25 3.35.96.1-.74.4-1.25.73-1.54-2.55-.29-5.24-1.28-5.24-5.69 0-1.26.45-2.28 1.18-3.09-.12-.29-.51-1.46.11-3.05 0 0 .97-.31 3.17 1.18a11 11 0 0 1 5.78 0c2.2-1.49 3.17-1.18 3.17-1.18.62 1.59.23 2.76.11 3.05.74.81 1.18 1.83 1.18 3.09 0 4.42-2.7 5.4-5.27 5.68.41.36.78 1.06.78 2.14v3.17c0 .31.21.67.8.56A11.5 11.5 0 0 0 23.5 12C23.5 5.65 18.35.5 12 .5Z"/>
                </svg>
            </a>
            <a class="flex h-12 w-12 shrink-0 items-center justify-center border border-zinc-200 bg-white text-zinc-700 transition hover:border-zinc-500 hover:bg-zinc-50 hover:text-black focus-visible:outline focus-visible:outline-2 focus-visible:outline-black" href="/oauth2/authorization/google" aria-label="${messages.get("provider.google")}" title="${messages.get("provider.google")}">
                <svg class="h-5 w-5" viewBox="0 0 24 24" aria-hidden="true">
                    <path fill="#4285F4" d="M23.5 12.27c0-.85-.08-1.67-.22-2.45H12v4.64h6.45a5.52 5.52 0 0 1-2.39 3.62v3h3.87c2.27-2.09 3.57-5.17 3.57-8.81Z"/>
                    <path fill="#34A853" d="M12 24c3.24 0 5.95-1.07 7.93-2.91l-3.87-3c-1.07.72-2.44 1.15-4.06 1.15-3.12 0-5.77-2.11-6.71-4.95H1.29v3.1A12 12 0 0 0 12 24Z"/>
                    <path fill="#FBBC05" d="M5.29 14.29a7.2 7.2 0 0 1 0-4.58v-3.1H1.29a12 12 0 0 0 0 10.78l4-3.1Z"/>
                    <path fill="#EA4335" d="M12 4.76c1.76 0 3.34.61 4.58 1.8l3.43-3.43C17.94 1.19 15.24 0 12 0A12 12 0 0 0 1.29 6.61l4 3.1C6.23 6.87 8.88 4.76 12 4.76Z"/>
                </svg>
            </a>
            <a class="flex h-12 w-12 shrink-0 items-center justify-center border border-zinc-200 bg-white text-zinc-700 transition hover:border-zinc-500 hover:bg-zinc-50 hover:text-black focus-visible:outline focus-visible:outline-2 focus-visible:outline-black" href="/oauth2/authorization/facebook" aria-label="${messages.get("provider.facebook")}" title="${messages.get("provider.facebook")}">
                <svg class="h-5 w-5" viewBox="0 0 24 24" aria-hidden="true">
                    <path fill="#1877F2" d="M24 12a12 12 0 1 0-13.875 11.85v-8.38H7.08V12h3.045V9.36c0-3 1.79-4.66 4.52-4.66 1.31 0 2.68.235 2.68.235v2.95h-1.51c-1.49 0-1.955.925-1.955 1.875V12h3.33l-.532 3.47h-2.798v8.38A12 12 0 0 0 24 12Z"/>
                </svg>
            </a>
        @endif

    @if (telegramBotUsername != null && !telegramBotUsername.isBlank())
        <button class="flex h-12 w-12 shrink-0 items-center justify-center border border-zinc-200 bg-white text-zinc-700 transition hover:border-zinc-500 hover:bg-zinc-50 hover:text-black focus-visible:outline focus-visible:outline-2 focus-visible:outline-black" id="telegram-login-toggle" type="button" aria-label="${messages.get("login.telegram")}" title="${messages.get("login.telegram")}" aria-expanded="false" aria-controls="telegram-login-panel">
            <img class="h-5 w-5 object-contain" src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQB8Ga8uvmXLQectoQsAX_Wcn48j02mGjJy3gN27lGPZQ&amp;s=10" alt="" aria-hidden="true">
        </button>
    @endif
    </div>
    @if (telegramBotUsername != null && !telegramBotUsername.isBlank())
        <div class="mt-3 hidden border border-zinc-200 bg-zinc-50 p-4" id="telegram-login-panel" data-bot-username="${telegramBotUsername}" aria-live="polite">
            <p class="mb-3 text-center text-xs font-medium uppercase tracking-[0.18em] text-zinc-500">${messages.get("login.telegram")}</p>
            <div class="flex w-full justify-center" id="telegram-widget-container"></div>
        </div>
        <script>
            const telegramToggle = document.getElementById("telegram-login-toggle");
            const telegramPanel = document.getElementById("telegram-login-panel");
            const telegramWidgetContainer = document.getElementById("telegram-widget-container");
            let telegramWidgetLoaded = false;

            telegramToggle?.addEventListener("click", () => {
                const expanded = telegramToggle.getAttribute("aria-expanded") === "true";
                telegramToggle.setAttribute("aria-expanded", String(!expanded));
                telegramPanel.classList.toggle("hidden", expanded);

                if (!expanded && !telegramWidgetLoaded) {
                    const widget = document.createElement("script");
                    widget.async = true;
                    widget.src = "https://telegram.org/js/telegram-widget.js?22";
                    widget.setAttribute("data-telegram-login", telegramPanel.dataset.botUsername);
                    widget.setAttribute("data-size", "medium");
                    widget.setAttribute("data-userpic", "false");
                    widget.setAttribute("data-auth-url", "/login/telegram/callback");
                    telegramWidgetContainer.appendChild(widget);
                    telegramWidgetLoaded = true;
                }
            });
        </script>
    @endif

    @if (!telegramLinkMode)
        <p class="mb-0 mt-8 text-center text-xs uppercase tracking-[0.16em] text-zinc-500"><a class="font-semibold text-zinc-700 transition hover:text-black hover:underline" href="/register">${messages.get("register.title")}</a></p>
    @endif
    </section>
</main>
</body>
</html>
