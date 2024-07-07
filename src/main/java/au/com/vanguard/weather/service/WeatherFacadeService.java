package au.com.vanguard.weather.service;

import au.com.vanguard.weather.dto.WeatherDto;
import au.com.vanguard.weather.mapper.EntityConverter;
import au.com.vanguard.weather.ratelimiter.RateLimit;
import au.com.vanguard.weather.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherFacadeService {

    @Qualifier("OpenWeatherMapService")
    private final OpenWeatherMapService weatherService;
    private final EntityConverter entityConverter;
    private final WeatherRepository weatherRepository;

    @RateLimit
    public Mono<WeatherDto> fetchWeatherDetails(String countryCode, String cityName) {
        try {
            /**
             * Fetch the response from external service, convert to our own model and store the weather response to the DB
             */
            Mono<WeatherDto> weatherDtoMono = weatherService.fetchWeatherDetails(countryCode, cityName)
                    .map(w -> entityConverter.convert(w))
                    .map(we -> weatherRepository.save(we))
                    .map(s -> entityConverter.convert(s));

            return weatherDtoMono;
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.error(e);
        }
    }
}
