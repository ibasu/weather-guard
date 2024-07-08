package au.com.vanguard.weather.web.client;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
@EnableEncryptableProperties
@Getter
@Setter
@Slf4j
public class ClientConfiguration {
    @Value("${external.openweather.endpoint}")
    private String openWeatherEndpoint;

    @Value("${external.openweather.path}")
    private String openWeatherPath;

    @Value("${external.openweather.apiKey}")
    private String apiKey;

    @Value("${external.openweather.timeoutInSeconds}")
    private Long timeoutInSeconds;

    @Value("${external.authhub.tokens}")
    private List<String> dummyTokens;

    @Bean(name = "OpenWeatherClient")
    public WebClient openWeatherClient() {
        return WebClient.builder()
                .baseUrl(openWeatherEndpoint)
                .filter(logRequest())
                .build();
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            return Mono.just(clientRequest);
        });
    }
}
