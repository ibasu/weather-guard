package au.com.vanguard.weather.controller;

import au.com.vanguard.weather.dto.WeatherDto;
import au.com.vanguard.weather.exception.WeatherNotFoundException;
import au.com.vanguard.weather.mapper.EntityConverter;
import au.com.vanguard.weather.mapper.ResponseConverter;
import au.com.vanguard.weather.repository.WeatherRepository;
import au.com.vanguard.weather.service.WeatherFacadeService;
import au.com.vanguard.weather.web.client.ClientConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static helper.WeatherDataHelper.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(WeatherController.class)
@Import({ResponseConverter.class, EntityConverter.class, ClientConfiguration.class})
class WeatherControllerUnitTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private WeatherFacadeService weatherFacadeService;

    @MockBean
    private WeatherRepository weatherRepository;

    private WeatherDto defaultWeatherDto = defaultWeatherDTO();

    @BeforeEach
    void beforeEach() {
        when(weatherRepository.save(defaultWeatherEntity())).thenReturn(defaultWeatherEntity());
    }

    @Test
    public void testShouldGetWeatherDetails() throws WeatherNotFoundException {
        when(weatherFacadeService.fetchWeatherDetails(DEFAULT_WEATHER_COUNTRY_CODE, DEFAULT_WEATHER_CITY_NAME)).thenReturn(Mono.just(defaultWeatherDto));

        webTestClient
                .get().uri("/api/v1/weather?country=" + DEFAULT_WEATHER_COUNTRY_CODE + "&city=" + DEFAULT_WEATHER_CITY_NAME)
                .header("X-VANGUARD-API-KEY", "sampletoken1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.description").isEqualTo(DEFAULT_WEATHER_DESCRIPTION);
    }

    @Test
    public void testGetWeatherDetailsNotFoundError() throws WeatherNotFoundException {
        when(weatherFacadeService.fetchWeatherDetails("UNKNOWCOUNTRY", "UNKNOWCity")).thenThrow(new WeatherNotFoundException("No weather details Found", "URL"));

        webTestClient
                .get().uri("/api/v1/weather?country=UNKNOWCOUNTRY&city=UNKNOWCity")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-VANGUARD-API-KEY", "sampletoken1")
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$[0].errorId").isEqualTo("404 NOT_FOUND")
                .jsonPath("$[0].errorMessage").isEqualTo("No weather details Found");

    }

    @Test
    public void testMissingMandatoryHeaderError() throws WeatherNotFoundException {
        webTestClient
                .get().uri("/api/v1/weather?country=UNKNOWCOUNTRY&city=UNKNOWCity")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Bad Request")
                .jsonPath("$.status").isEqualTo("400");

    }
}