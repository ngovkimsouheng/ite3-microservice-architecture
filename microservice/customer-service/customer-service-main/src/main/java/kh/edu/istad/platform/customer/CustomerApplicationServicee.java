package kh.edu.istad.platform.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "kh.edu.istad.platform.customer",
        "kh.edu.istad.common.restapi"
})
public class CustomerApplicationServicee {

    public static void main(String[] args) {
        SpringApplication.run(CustomerApplicationServicee.class, args);
    }
}
