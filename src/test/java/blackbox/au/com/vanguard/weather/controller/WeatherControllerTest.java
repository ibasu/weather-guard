package au.com.vanguard.weather.controller;

import au.com.vanguard.weather.configuration.WeatherTestConfiguration;
import au.com.vanguard.weather.web.response.WeatherResponse;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static helper.WeatherDataHelper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Import(WeatherTestConfiguration.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class WeatherControllerTest {

    public static final String WEATHER_ROUTE = "/api/v1/weather?country=";
    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private WeatherTestConfiguration weatherTestConfiguration;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("external.openweather.endpoint", wireMockServer::baseUrl);
    }

    @BeforeEach
    void init() {
        webTestClient = webTestClient.mutate()
                .responseTimeout(Duration.ofMillis(30000))
                .build();
    }

    @AfterEach
    void resetAll() {
        wireMockServer.resetAll();
    }

    @Test
    void test200WeatherDetails() {
        String valid200Url = String.format(OPEN_WEATHER_URI, DEFAULT_WEATHER_CITY_NAME, DEFAULT_WEATHER_COUNTRY_CODE, weatherTestConfiguration.getApiKey());

        wireMockServer.stubFor(
                WireMock.get(WireMock.urlEqualTo(valid200Url))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBodyFile("openweather/200_openweatherresponse.json"))
        );

        EntityExchangeResult<WeatherResponse> weatherResponse = this.webTestClient
                .get()
                .uri("/api/v1/weather?country=" + DEFAULT_WEATHER_COUNTRY_CODE + "&city=" + DEFAULT_WEATHER_CITY_NAME)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header("X-VANGUARD-API-KEY", "sampletoken1")
                .exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(WeatherResponse.class)
                .returnResult();

        WeatherResponse weatherResponseDTO = weatherResponse.getResponseBody();
        assertEquals(weatherResponseDTO.getDescription(), DEFAULT_WEATHER_DESCRIPTION);
    }

    @Test
    void testReturn404ForUnknownTransactionId() {
        this.webTestClient
                .get()
                .uri("/api/v1/weather?country=" + DEFAULT_WEATHER_COUNTRY_CODE + "&city=" + DEFAULT_WEATHER_CITY_NAME)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header("X-VANGUARD-API-KEY", "sampletoken1")
                .exchange()
                .expectStatus()
                .isEqualTo(NOT_FOUND)
                .expectBody()
                .jsonPath("$[0].errorId").isEqualTo("404 NOT_FOUND")
                .jsonPath("$[0].errorMessage").isEqualTo("No weather details Found");
    }

    @Test
    void shouldReturn400ForMissingMandatoryHeader() {

        this.webTestClient
                .get()
                .uri("/api/v1/weather?country=" + DEFAULT_WEATHER_COUNTRY_CODE + "&city=" + DEFAULT_WEATHER_CITY_NAME)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isEqualTo(BAD_REQUEST)
                .expectBody()
                .jsonPath("$.title").isEqualTo("Bad Request")
                .jsonPath("$.detail").isEqualTo("Required header 'X-VANGUARD-API-KEY' is not present.");
    }

    @Test
    void testReturn400ForMissingParams() {

        this.webTestClient
                .get()
                .uri("/api/v1/weather?country=" + DEFAULT_WEATHER_COUNTRY_CODE)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header("X-VANGUARD-API-KEY", "sampletoken1")
                .exchange()
                .expectStatus()
                .isEqualTo(BAD_REQUEST)
                .expectBody()
                .jsonPath("$.title").isEqualTo("Bad Request")
                .jsonPath("$.detail").isEqualTo("Required query parameter 'city' is not present.");
    }

    @Test
    void testReturn401ForInvalidAPIKey() {
        String invalid401Url = String.format(OPEN_WEATHER_URI, DEFAULT_WEATHER_CITY_NAME, DEFAULT_WEATHER_COUNTRY_CODE, weatherTestConfiguration.getApiKey());
        System.out.println("whats the mocked url:" + invalid401Url);

        wireMockServer.stubFor(
                WireMock.get(WireMock.urlEqualTo(invalid401Url))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withStatus(UNAUTHORIZED.value())
                                .withBodyFile("openweather/401_openweatherresponse.json"))
        );

        this.webTestClient
                .get()
                .uri("/api/v1/weather?country=" + DEFAULT_WEATHER_COUNTRY_CODE + "&city=" + DEFAULT_WEATHER_CITY_NAME)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header("X-VANGUARD-API-KEY", "sampletoken1")
                .exchange()
                .expectStatus()
                .isEqualTo(UNAUTHORIZED)
                .expectBody()
                .jsonPath("$[0].errorId").isEqualTo("401 UNAUTHORIZED")
                .jsonPath("$[0].errorDetails").isEqualTo("API_KEY");
    }

    @Test
    void testReturn400InvalidToken() {
        this.webTestClient
                .get()
                .uri("/api/v1/weather?country=" + DEFAULT_WEATHER_COUNTRY_CODE + "&city=" + DEFAULT_WEATHER_CITY_NAME)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header("X-VANGUARD-API-KEY", "sampletoken11")
                .exchange()
                .expectStatus()
                .isEqualTo(BAD_REQUEST)
                .expectBody()
                .jsonPath("$[0].errorId").isEqualTo("400 BAD_REQUEST")
                .jsonPath("$[0].errorMessage").isEqualTo("Invalid API KEY")
                .jsonPath("$[0].errorDetails").isEqualTo("weather.apiKey");

    }
}