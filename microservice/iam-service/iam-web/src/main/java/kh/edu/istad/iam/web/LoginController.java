package kh.edu.istad.iam.web;

import jakarta.validation.Valid;
import kh.edu.istad.iam.domain.dto.RegisterUserCommand;
import kh.edu.istad.iam.domain.usecase.user.RegisterUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final RegisterUserUseCase registerUserUseCase;
    private final MessageSource messageSource;

    @Value("${app.telegram.bot-username:}")
    private String telegramBotUsername;

    @GetMapping({"/login", "/login.html"})
    public String login(@RequestParam(name = "error", required = false) String error,
                        @RequestParam(name = "oauth2_error", required = false) String oauthError,
                        @RequestParam(name = "registered", required = false) String registered,
                        @RequestParam(name = "telegramLink", required = false) String telegramLink,
                        HttpServletRequest request, Model model) {
        model.addAttribute("error", error != null || oauthError != null);
        model.addAttribute("registered", registered != null);
        model.addAttribute("telegramBotUsername", telegramBotUsername);
        model.addAttribute("telegramLinkMode", telegramLink != null
                && request.getSession(false) != null
                && request.getSession(false).getAttribute("iam.link-provider") instanceof String);
        return "login";
    }

    @GetMapping("/register")
    public String registrationForm(Model model) {
        model.addAttribute("form", emptyForm());
        model.addAttribute("errors", List.of());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegisterForm form,
                           BindingResult bindingResult, Model model, Locale locale) {
        List<String> errors = new ArrayList<>(bindingResult.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .toList());
        if (!form.password().equals(form.confirmPassword())) {
            errors.add(messageSource.getMessage("register.passwordMismatch", null, locale));
        }
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "register";
        }
        try {
            registerUserUseCase.execute(new RegisterUserCommand(
                    form.username(), form.email(), form.password(), form.confirmPassword(),
                    form.givenName(), form.familyName(), form.locale()
            ));
        } catch (IllegalArgumentException exception) {
            model.addAttribute("errors", List.of(exception.getMessage()));
            return "register";
        }
        return "redirect:/login?registered";
    }

    @GetMapping("/oauth2/consent")
    public String consent(@RequestParam("client_id") String clientId,
                          @RequestParam(name = "state", defaultValue = "") String state,
                          @RequestParam(name = "scope", required = false) List<String> scopes,
                          Model model) {
        model.addAttribute("clientId", clientId);
        model.addAttribute("state", state);
        model.addAttribute("requestedScopes", scopes == null ? List.of() : scopes);
        return "consent";
    }

    private RegisterForm emptyForm() {
        return new RegisterForm("", "", "", "", "", "", "en");
    }
}
