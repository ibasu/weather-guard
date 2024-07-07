package au.com.vanguard.weather.service;

import au.com.vanguard.weather.dto.WeatherDto;
import au.com.vanguard.weather.exception.InvalidApiKeyException;
import au.com.vanguard.weather.exception.ServerException;
import au.com.vanguard.weather.exception.WeatherNotFoundException;
import au.com.vanguard.weather.mapper.ResponseConverter;
import au.com.vanguard.weather.pojo.Openweatherresponse;
import au.com.vanguard.weather.web.client.ClientConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenWeatherMapService implements WeatherService {

    @Qualifier("OpenWeatherClient")
    private final WebClient webClient;

    private final ClientConfiguration clientConfiguration;
    private final ResponseConverter responseConverter;

    public static void logTraceResponse(ClientResponse response) {
        log.error("Response status: {}", response.statusCode());
        log.error("Response headers: {}", response.headers().asHttpHeaders());
        response.bodyToMono(String.class)
                .publishOn(Schedulers.boundedElastic())
                .subscribe(body -> log.error("Response body: {}", body));
    }

    private Mono<Openweatherresponse> fetchWeatherDetails(String uri) {
        try {
            Mono<Openweatherresponse> response = webClient
                    .get()
                    .uri(uri)
                    .retrieve()
                    .onStatus(status -> status == HttpStatus.UNAUTHORIZED, resp -> resp.bodyToMono(String.class).flatMap(ex -> Mono.error(new InvalidApiKeyException(ex, "API_KEY"))))
                    .onStatus(status -> status.is4xxClientError(), resp -> Mono.just(new WeatherNotFoundException("No weather details Found", "Params[" + uri)))
                    .onStatus(status -> status.is5xxServerError(), resp -> {
                        logTraceResponse(resp);
                        return Mono.just(new ServerException("Error while getting weather details, with URL:" + uri));
                    })
                    .bodyToMono(Openweatherresponse.class)
                    .timeout(Duration.of(clientConfiguration.getTimeoutInSeconds(), ChronoUnit.SECONDS))
                    .onErrorResume(throwable -> Mono.error(throwable));

            return response;
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    @Override
    public Mono<WeatherDto> fetchWeatherDetails(String countryName, String cityName) throws WeatherNotFoundException {
        try {
            String openWeatherUrl = String.format(clientConfiguration.getOpenWeatherPath(), cityName, countryName, clientConfiguration.getApiKey());

            /**
             * Fetch the response from external service
             */
            Mono<WeatherDto> weatherDtoMono = fetchWeatherDetails(openWeatherUrl)
                    .map(r -> responseConverter.convert(r));

            return weatherDtoMono;
        } catch (Exception e) {
            log.error("Severe Error while fetching weather details", e);
            return Mono.error(e);
        }
    }
}
