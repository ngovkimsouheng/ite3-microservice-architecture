package kh.edu.istad.platform.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "kh.edu.istad.platform.order",
        "kh.edu.istad.platform.order.client.adapter"
})
public class OrderApplicationService {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplicationService.class, args);
    }
}