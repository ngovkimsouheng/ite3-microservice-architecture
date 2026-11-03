package kh.edu.istad.platform.order.client.config;

import kh.edu.istad.platform.order.client.httpclient.CustomerHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.reactive.function.client.support.OAuth2WebClientHttpServiceGroupConfigurer;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class WebClientConfig {

    @Bean
    public ReactiveOAuth2AuthorizedClientManager authorizedClientManager(
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            ReactiveOAuth2AuthorizedClientService authorizedClientService) {

        var authorizedClientProvider = ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();

        var manager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
                clientRegistrationRepository,
                authorizedClientService
        );
        manager.setAuthorizedClientProvider(authorizedClientProvider);
        return manager;
    }

    @Bean
    public OAuth2WebClientHttpServiceGroupConfigurer securityConfigurer(
            ReactiveOAuth2AuthorizedClientManager manager) {
        return OAuth2WebClientHttpServiceGroupConfigurer.from(manager);
    }

    @Bean
    public CustomerHttpClient customerHttpClient(
            ReactiveOAuth2AuthorizedClientManager authorizedClientManager,
            ReactorLoadBalancerExchangeFilterFunction loadBalancerFilter,
            @Value("${http.serviceclient.customer-service.base-url:http://customer-service}") String customerServiceBaseUrl) {

        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2Filter =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);

        WebClient webClient = WebClient.builder()
                .baseUrl(customerServiceBaseUrl)
                .filter(loadBalancerFilter) // Resolves "customer-service" via Consul
                .filter(oauth2Filter)       // Attaches OAuth2 token
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient))
                .build();

        return factory.createClient(CustomerHttpClient.class);
    }
}

//package kh.edu.istad.platform.order.client.config;
//
//import org.springframework.cloud.client.loadbalancer.LoadBalanced;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.reactive.function.client.WebClient;
//
//@Configuration
//public class WebClientConfig {
//
//    @Bean
//    @LoadBalanced
//    public WebClient.Builder webClient(){
//        return WebClient.builder();
//    }
//}