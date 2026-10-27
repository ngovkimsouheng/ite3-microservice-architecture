package kh.edu.istad.platform.order.bean;

import kh.edu.istad.platform.order.client.adapter.CustomerServiceClientAdapter;
import kh.edu.istad.platform.order.domain.port.out.client.CustomerServiceClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public CustomerServiceClient customerServiceClient(WebClient.Builder webClientBuilder) {
        return new CustomerServiceClientAdapter(webClientBuilder);
    }
}