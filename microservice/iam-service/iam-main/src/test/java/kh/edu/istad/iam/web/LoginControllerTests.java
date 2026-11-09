package kh.edu.istad.iam.web;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginControllerTests {

    @Test
    void splitsSpaceDelimitedScopesIntoIndividualConsentValues() {
        assertEquals(
                List.of("openid", "profile", "email"),
                LoginController.splitScopes(List.of("openid profile email"))
        );
    }

    @Test
    void normalizesRepeatedAndBlankScopeParameters() {
        assertEquals(
                List.of("openid", "profile", "email"),
                LoginController.splitScopes(List.of("openid profile", "email", "openid", " "))
        );
    }
}
