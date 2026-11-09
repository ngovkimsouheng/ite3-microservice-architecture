package kh.edu.istad.iam;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.security.oauth2.client.registration.google.client-id=test-google-client",
        "spring.security.oauth2.client.registration.google.client-secret=test-google-secret",
        "spring.security.oauth2.client.registration.github.client-id=test-github-client",
        "spring.security.oauth2.client.registration.github.client-secret=test-github-secret",
        "spring.security.oauth2.client.registration.facebook.client-id=test-facebook-client",
        "spring.security.oauth2.client.registration.facebook.client-secret=test-facebook-secret"
})
class IamServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
