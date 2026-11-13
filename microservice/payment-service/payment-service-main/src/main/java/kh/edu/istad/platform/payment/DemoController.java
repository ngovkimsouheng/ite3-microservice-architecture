package kh.edu.istad.platform.payment;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/demo-payment")
public class DemoController {
    @GetMapping
    Map<String, Object> getPaymentById(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "paymentId", UUID.randomUUID().toString(),
                "accessToken", jwt.getTokenValue()
        );
    }
}
