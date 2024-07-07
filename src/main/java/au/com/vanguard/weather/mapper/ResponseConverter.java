package au.com.vanguard.weather.mapper;

import au.com.vanguard.weather.dto.WeatherDto;
import au.com.vanguard.weather.pojo.Openweatherresponse;
import au.com.vanguard.weather.web.response.WeatherResponse;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class ResponseConverter {

    public WeatherDto convert(Openweatherresponse openweatherresponse) {
        WeatherDto weatherDto = new WeatherDto();
        weatherDto.setDescription(openweatherresponse.getWeather().get(0).getDescription());
        weatherDto.setIcon(openweatherresponse.getWeather().get(0).getIcon());
        weatherDto.setMain(openweatherresponse.getWeather().get(0).getMain());
        weatherDto.setId(openweatherresponse.getWeather().get(0).getId());

        return weatherDto;
    }

    public WeatherResponse convert(WeatherDto weatherDto) {
        WeatherResponse weatherResponse = new WeatherResponse();
        weatherResponse.setDescription(weatherDto.getDescription());

        return weatherResponse;
    }
}
