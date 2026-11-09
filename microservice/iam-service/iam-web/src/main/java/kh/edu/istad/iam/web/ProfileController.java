package kh.edu.istad.iam.web;

import jakarta.validation.Valid;
import kh.edu.istad.iam.domain.dto.ChangePasswordCommand;
import kh.edu.istad.iam.domain.dto.ConnectedAppResult;
import kh.edu.istad.iam.domain.dto.IdentityResult;
import kh.edu.istad.iam.domain.dto.UpdateUserProfileCommand;
import kh.edu.istad.iam.domain.dto.UserAuthoritiesResult;
import kh.edu.istad.iam.domain.dto.UserResult;
import kh.edu.istad.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.iam.domain.usecase.identity.ListConnectedAppsUseCase;
import kh.edu.istad.iam.domain.usecase.identity.ListUserIdentitiesUseCase;
import kh.edu.istad.iam.domain.usecase.identity.RevokeConnectedAppUseCase;
import kh.edu.istad.iam.domain.usecase.identity.UnlinkIdentityUseCase;
import kh.edu.istad.iam.domain.usecase.user.ChangePasswordUseCase;
import kh.edu.istad.iam.domain.usecase.user.GetUserAuthoritiesUseCase;
import kh.edu.istad.iam.domain.usecase.user.GetUserUseCase;
import kh.edu.istad.iam.domain.usecase.user.UpdateUserProfileUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.UUID;
import java.util.Locale;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final CurrentActorProvider currentActorProvider;
    private final GetUserUseCase getUserUseCase;
    private final GetUserAuthoritiesUseCase getUserAuthoritiesUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final ListUserIdentitiesUseCase listUserIdentitiesUseCase;
    private final UnlinkIdentityUseCase unlinkIdentityUseCase;
    private final ListConnectedAppsUseCase listConnectedAppsUseCase;
    private final RevokeConnectedAppUseCase revokeConnectedAppUseCase;

    @GetMapping("/")
    public String profile(Model model) {
        UUID userUuid = currentUserUuid();
        model.addAttribute("user", getUserUseCase.execute(userUuid));
        model.addAttribute("authorities", getUserAuthoritiesUseCase.execute(userUuid));
        return "index";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model) {
        model.addAttribute("user", getUserUseCase.execute(currentUserUuid()));
        model.addAttribute("errors", List.of());
        return "profile-edit";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@Valid @ModelAttribute ProfileEditForm form,
                                BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", getUserUseCase.execute(currentUserUuid()));
            model.addAttribute("errors", errors(bindingResult));
            return "profile-edit";
        }
        UUID userUuid = currentUserUuid();
        UserResult user = updateUserProfileUseCase.execute(new UpdateUserProfileCommand(
                userUuid, null, form.givenName(), form.nickname(), form.phoneNumber(), form.locale()
        ));
        model.addAttribute("user", user);
        model.addAttribute("errors", List.of());
        model.addAttribute("saved", true);
        return "profile-edit";
    }

    @GetMapping("/profile/password")
    public String changePasswordForm(Model model) {
        model.addAttribute("errors", List.of());
        return "change-password";
    }

    @PostMapping("/profile/password")
    public String changePassword(@Valid @ModelAttribute ChangePasswordForm form,
                                 BindingResult bindingResult, Model model) {
        List<String> errors = errors(bindingResult);
        if (!form.newPassword().equals(form.confirmPassword())) {
            errors = new java.util.ArrayList<>(errors);
            errors.add("Passwords do not match.");
        }
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "change-password";
        }
        try {
            changePasswordUseCase.execute(currentUserUuid(), new ChangePasswordCommand(
                    form.currentPassword(), form.newPassword(), form.confirmPassword()
            ));
        } catch (IllegalArgumentException exception) {
            model.addAttribute("errors", List.of(exception.getMessage()));
            return "change-password";
        }
        return "redirect:/?passwordChanged";
    }

    @GetMapping("/profile/accounts")
    public String linkedAccounts(Model model) {
        List<IdentityResult> identities = listUserIdentitiesUseCase.execute(currentUserUuid());
        model.addAttribute("identities", identities);
        return "linked-accounts";
    }

    @PostMapping("/profile/accounts/link/{provider}")
    public String linkAccount(@PathVariable String provider, HttpServletRequest request) {
        String normalizedProvider = provider.toUpperCase(Locale.ROOT);
        if (!List.of("GOOGLE", "GITHUB", "FACEBOOK", "TELEGRAM").contains(normalizedProvider)) {
            throw new IllegalArgumentException("Unsupported identity provider: " + provider);
        }
        request.getSession(true).setAttribute(
                "iam.link-provider",
                normalizedProvider + ":" + System.currentTimeMillis()
        );
        if ("TELEGRAM".equals(normalizedProvider)) {
            return "redirect:/login?telegramLink";
        }
        return "redirect:/oauth2/authorization/" + provider.toLowerCase(Locale.ROOT);
    }

    @PostMapping("/profile/accounts/{identityUuid}/unlink")
    public String unlinkAccount(@PathVariable UUID identityUuid) {
        unlinkIdentityUseCase.execute(currentUserUuid(), identityUuid);
        return "redirect:/profile/accounts";
    }

    @GetMapping("/profile/apps")
    public String connectedApps(Model model) {
        String principal = currentActorProvider.getCurrentActorSub();
        List<ConnectedAppResult> apps = listConnectedAppsUseCase.execute(principal);
        model.addAttribute("apps", apps);
        return "connected-apps";
    }

    @PostMapping("/profile/apps/{clientId}/revoke")
    public String revokeConnectedApp(@PathVariable String clientId) {
        revokeConnectedAppUseCase.execute(currentActorProvider.getCurrentActorSub(), clientId);
        return "redirect:/profile/apps";
    }

    private UUID currentUserUuid() {
        return UUID.fromString(currentActorProvider.getCurrentActorSub());
    }

    private List<String> errors(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .toList();
    }
}
