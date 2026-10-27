package kh.edu.istad.platform.customer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CustomerApplicationService {

    public CustomerApplicationService(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    public static void main(String[] args) {
        SpringApplication.run(CustomerApplicationService.class, args);
    }

    private final DiscoveryClient discoveryClient;


    //this use to query instance when we run service as multiple
    @Bean
    public CommandLineRunner cmd() {
        return _ -> {
            discoveryClient.getInstances("customer-service").forEach(instance -> {
                System.out.println(instance.getUri());
            });
        };
    }
}
