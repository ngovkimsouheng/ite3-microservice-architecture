package kh.edu.istad.platform.order;

import kh.edu.istad.platform.order.config.ServicePropConfig;
import kh.edu.istad.platform.order.domain.port.out.client.CustomerServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@RefreshScope
@RestController
//@RequiredArgsConstructor
public class WebFluxController {
    private final ServicePropConfig servicePropConfig;
    private final CustomerServiceClient customerServiceClient;

    public WebFluxController(ServicePropConfig servicePropConfig, CustomerServiceClient customerServiceClient) {
        this.servicePropConfig = servicePropConfig;
        this.customerServiceClient = customerServiceClient;
    }

//    public WebFluxController(CustomerServiceClient customerServiceClient) {
//        this.customerServiceClient = customerServiceClient;
//    }

    @GetMapping("customers")
    public Flux<CustomerServiceClient.CustomerResponse> getCustomers() {
        return customerServiceClient.getCustomerNames();
    }

    //    @Value("${service.message}")
//    private String serviceMessage;
//    @Value("${service.env}")
//    private String serviceEnv;
//    @Value("${service.version}")
//    private String serviceVersion;
    @Value("${global.version}")
    private String globalVersion;



    @GetMapping("/consul/config")
    public Mono<Map<String, Object>> getConsulConfig() {
        return Mono.just(
                Map.of(
                        "globalVersion", globalVersion,
                        "message", servicePropConfig.getMessage(),
                        "env", servicePropConfig.getEnv(),
                        "version", servicePropConfig.getVersion(),
                        "baseUrl", servicePropConfig.getBaseUrl()));

    }


    @GetMapping("/flux")
    public Flux<String> getFluxData() {
        return Flux.just("A", "B", "C", "D", "E", "F")
                .log()
                .delayElements(Duration.ofSeconds(1));
    }

}