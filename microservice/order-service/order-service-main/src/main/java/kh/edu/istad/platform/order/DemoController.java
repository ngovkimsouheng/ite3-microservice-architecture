package kh.edu.istad.platform.order;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/demo-order")
public class DemoController {
    @GetMapping
    Map<String, Object> getBusinessById(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "OrderId", UUID.randomUUID().toString(),
                "accessToken", jwt.getTokenValue()
        );
    }
}
