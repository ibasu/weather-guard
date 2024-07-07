package au.com.vanguard.weather.service;

import au.com.vanguard.weather.dto.WeatherDto;
import au.com.vanguard.weather.exception.WeatherNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface WeatherService {
    public Mono<WeatherDto> fetchWeatherDetails(String countryName, String cityName) throws WeatherNotFoundException;
}
