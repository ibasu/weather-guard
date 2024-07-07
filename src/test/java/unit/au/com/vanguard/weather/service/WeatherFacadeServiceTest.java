package au.com.vanguard.weather.service;

import au.com.vanguard.weather.dto.WeatherDto;
import au.com.vanguard.weather.mapper.EntityConverter;
import au.com.vanguard.weather.repository.WeatherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static helper.WeatherDataHelper.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WeatherFacadeServiceTest {

    @InjectMocks
    private WeatherFacadeService weatherFacadeService;

    @Mock
    private OpenWeatherMapService weatherService;

    @Mock
    private EntityConverter entityConverter;

    @Mock
    private WeatherRepository weatherRepository;

    @BeforeEach
    public void init() {
        when(entityConverter.convert(defaultWeatherDTO())).thenReturn(defaultWeatherEntity());
        when(entityConverter.convert(defaultWeatherEntity())).thenReturn(defaultWeatherDTO());
    }

    @Test
    public void testShouldFetchWeatherDetails() throws Exception {
        WeatherDto weatherDto = defaultWeatherDTO();
        when(entityConverter.convert(weatherDto)).thenReturn(defaultWeatherEntity());
        when(weatherService.fetchWeatherDetails(DEFAULT_WEATHER_COUNTRY_CODE, DEFAULT_WEATHER_CITY_NAME)).thenReturn(Mono.just(weatherDto));
        when(weatherRepository.save(any())).thenReturn(defaultWeatherEntity());

        Mono<WeatherDto> weatherDtoMono = weatherFacadeService.fetchWeatherDetails(DEFAULT_WEATHER_COUNTRY_CODE, DEFAULT_WEATHER_CITY_NAME);

        StepVerifier.create(weatherDtoMono)
                .expectNextMatches(w -> w.getDescription().equals(DEFAULT_WEATHER_DESCRIPTION))
                .verifyComplete();

    }

}
