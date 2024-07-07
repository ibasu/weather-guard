package au.com.vanguard.weather.controller;

import au.com.vanguard.weather.common.Constants;
import au.com.vanguard.weather.dto.WeatherDto;
import au.com.vanguard.weather.mapper.ResponseConverter;
import au.com.vanguard.weather.security.authentication.ValidateApiKey;
import au.com.vanguard.weather.service.WeatherFacadeService;
import au.com.vanguard.weather.web.response.WeatherResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/weather")
@Slf4j
public class WeatherController {

    private final WeatherFacadeService weatherFacadeService;
    private final ResponseConverter responseConverter;

    @Operation(summary = "Get Weather by Country and City Name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found Weather Summary",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = WeatherDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid Country or City", content = @Content),
            @ApiResponse(responseCode = "404", description = "Weather Summary not found", content = @Content)})

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Mono<WeatherResponse> weather(@RequestHeader final HttpHeaders requestHeaders,
                                         @RequestHeader(name = Constants.HTTP_HEADER_API_KEY, required = true) @ValidateApiKey String apiKey,
                                         @RequestParam(name = "country", required = true) String countryName,
                                         @RequestParam(name = "city", required = true) String cityName) {
        log.info("Params : country: {}, city: {}", countryName, cityName);

        return weatherFacadeService.fetchWeatherDetails(countryName, cityName)
                .map(r -> responseConverter.convert(r));
    }
}
