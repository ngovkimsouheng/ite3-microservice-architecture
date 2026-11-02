package kh.edu.istad.platform.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication (scanBasePackages = {
        "kh.edu.istad.platform.order",
        "kh.edu.istad.platform.order.domain.port.out"
})
public class OrderApplicationService {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplicationService.class, args);
    }
}