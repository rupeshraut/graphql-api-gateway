package com.ethdc.poc.gag.df;

import com.netflix.graphql.dgs.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


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
    @Value("${api.gateway.user-role-find-by-user-id-uri}")
    private String userRoleServiceFindByUserIdUri;
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

    @DgsData(parentType = "User")
    public Mono<UserRole> role(DgsDataFetchingEnvironment dfe) {
       var user = dfe.<User>getSource();
        return this.webClient.get()
                .uri(ub -> ub.path(userRoleServiceFindByUserIdUri).build(user.id()))
                .retrieve()
                .bodyToMono(UserRole.class)
                .onErrorResume(Mono::error);

    }

}

