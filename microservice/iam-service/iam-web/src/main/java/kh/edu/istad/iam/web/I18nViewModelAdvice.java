package kh.edu.istad.iam.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@ControllerAdvice(annotations = Controller.class)
public class I18nViewModelAdvice {

    private static final List<String> MESSAGE_KEYS = List.of(
            "common.home", "common.logout", "common.save", "common.cancel", "common.back",
            "common.english", "common.khmer",
            "provider.google", "provider.github", "provider.facebook", "provider.telegram",
            "login.title", "login.username", "login.password", "login.submit", "login.continue",
            "login.error", "login.telegram",
            "register.title", "register.username", "register.email", "register.password",
            "register.confirmPassword", "register.givenName", "register.familyName",
            "register.locale", "register.submit", "register.error",
            "consent.title", "consent.requested", "consent.allow", "consent.cancel",
            "profile.title", "profile.username", "profile.email", "profile.givenName",
            "profile.familyName", "profile.nickname", "profile.phone", "profile.locale",
            "profile.edit", "profile.password", "profile.linkedAccounts", "profile.connectedApps",
            "profile.roles", "profile.permissions", "password.title", "password.current",
            "password.new", "password.confirm", "password.submit", "accounts.title",
            "accounts.link", "accounts.unlink", "accounts.empty", "apps.title",
            "apps.name", "apps.scopes", "apps.revoke", "apps.empty", "status.saved",
            "status.registered", "status.passwordChanged"
    );

    private final MessageSource messageSource;

    public I18nViewModelAdvice(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ModelAttribute
    public void addCommonModel(Locale locale, HttpServletRequest request, Model model) {
        Map<String, String> messages = new LinkedHashMap<>();
        for (String key : MESSAGE_KEYS) {
            messages.put(key, messageSource.getMessage(key, null, locale));
        }
        model.addAttribute("messages", messages);
        model.addAttribute("currentLanguage", locale.getLanguage());
        Object csrfAttribute = request.getAttribute(CsrfToken.class.getName());
        if (!(csrfAttribute instanceof CsrfToken)) {
            csrfAttribute = request.getAttribute("_csrf");
        }
        if (csrfAttribute instanceof CsrfToken csrfToken) {
            model.addAttribute("csrfParameterName", csrfToken.getParameterName());
            model.addAttribute("csrfToken", csrfToken.getToken());
        } else {
            model.addAttribute("csrfParameterName", "");
            model.addAttribute("csrfToken", "");
        }
    }
}
