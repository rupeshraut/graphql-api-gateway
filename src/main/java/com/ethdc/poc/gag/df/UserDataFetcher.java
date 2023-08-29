package com.ethdc.poc.gag.df;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;


/**
 * The type User data fetcher.
 */
@DgsComponent
@Slf4j
public class UserDataFetcher {

    @Value("${api.gateway.base-url:http://localhost:8080}")
    private String apiGatewayeBaseUrl;
    @Value("${api.gateway.user-find-all-uri}")
    private String userServiceFindAllUri;
    @Value("${api.gateway.user-find-by-id-uri}")
    private String userServiceFindByIdUri;
    private WebClient webClient;


    @PostConstruct
    void init() {
        log.info("apiGatewayeBaseUrl >> {}", apiGatewayeBaseUrl);
        this.webClient = WebClient.builder().baseUrl(apiGatewayeBaseUrl).build();
    }

    /**
     * Users list.
     *
     * @param id the id
     * @return the list
     */
    @DgsQuery
    public Flux<User> users(@InputArgument Integer id) {
        log.info("userServiceUri >> {}", userServiceFindAllUri);
        if (id != null) {
            return this.webClient.get()
                    .uri(ub -> ub.path(userServiceFindByIdUri).build(id))
                    .retrieve()
                    .bodyToFlux(User.class)
                    .onErrorResume(Flux::error);
        }

        return this.webClient.get()
                .uri(userServiceFindAllUri)
                .retrieve()
                .bodyToFlux(User.class)
                .onErrorResume(Flux::error);
    }

}

