package kh.edu.istad.platform.order.client.config;

import kh.edu.istad.platform.order.client.exception.DownStreamServiceException;
import kh.edu.istad.platform.order.client.httpclient.BusinessHttpClient;
import kh.edu.istad.platform.order.client.httpclient.CustomerHttpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.reactive.function.client.support.WebClientHttpServiceGroupConfigurer;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.service.registry.HttpServiceGroup;
import org.springframework.web.service.registry.ImportHttpServices;

@Configuration
//spring boot 4.0.0
//@ImportHttpServices (
//        group = "customer-service",
//        types = {CustomerHttpClient.class},
//        clientType = HttpServiceGroup.ClientType.WEB_CLIENT
//)
@ImportHttpServices (
        group = "business-service",
        types = {BusinessHttpClient.class},
        clientType = HttpServiceGroup.ClientType.WEB_CLIENT
)
//@RequiredArgsConstructor
public class ClientConfig {

    @Bean
    public WebClientHttpServiceGroupConfigurer webClientHttpServiceGroupConfigurer() {
        return groups -> {
            groups.filterByName("customer-service")
                    .forEachClient((group, clientBuilder) ->
                                    clientBuilder
//                                            .baseUrl("")
                                            .defaultHeader("User-Agent", "Web Client")
                                            .defaultStatusHandler(
                                                    HttpStatusCode::isError,
                                                    clientResponse -> {
                                                        return clientResponse
                                                                .createException()
                                                                .map(
                                                                        ex -> new DownStreamServiceException(
                                                                                ex.getMessage(),
                                                                                "customer-service"
                                                                        )
                                                                );
                                                    }
                                            )

                    );

        };

    }
}
