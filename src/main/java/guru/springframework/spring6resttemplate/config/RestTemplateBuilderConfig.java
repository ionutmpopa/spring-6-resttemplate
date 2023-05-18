package guru.springframework.spring6resttemplate.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.client.RestTemplateBuilderConfigurer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

/**
 * Created by jt, Spring Framework Guru.
 */
@RequiredArgsConstructor
@Configuration
public class RestTemplateBuilderConfig {

    @Value("${rest.template.rootUrl}")
    String rootUrl;


    @Bean
    OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager(ClientRegistrationRepository clientRegistrationRepository,
                                                                OAuth2AuthorizedClientService oAuth2AuthorizedClientService) {
        var authorizedClientProvided = OAuth2AuthorizedClientProviderBuilder.builder()
            .clientCredentials()
            .build();

        var authorizedClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
            clientRegistrationRepository, oAuth2AuthorizedClientService
        );
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvided);
        return authorizedClientManager;
    }

    @Bean
    RestTemplate restTemplate(RestTemplateBuilderConfigurer configurer,
                              OAuthClientInterceptor oAuthClientInterceptor) {

        assert rootUrl != null;

        return configurer.configure(new RestTemplateBuilder())
            .additionalInterceptors(oAuthClientInterceptor)
            .uriTemplateHandler(new DefaultUriBuilderFactory(rootUrl))
            .build();

    }
}
