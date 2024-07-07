package au.com.vanguard.weather.mapper;

import au.com.vanguard.weather.dto.WeatherDto;
import au.com.vanguard.weather.repository.entity.Weather;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class EntityConverter {

    public Weather convert(WeatherDto weatherDto) {
        Weather weather = new Weather();
        weather.setDescription(weatherDto.getDescription());
        weather.setId(Long.valueOf(weatherDto.getId()));
        weather.setIcon(weatherDto.getIcon());
        weather.setMain(weatherDto.getMain());

        return weather;
    }

    public WeatherDto convert(Weather weather) {
        WeatherDto weatherDto = new WeatherDto();
        weatherDto.setDescription(weather.getDescription());
        weatherDto.setId(Math.toIntExact(weather.getId()));
        weatherDto.setIcon(weather.getIcon());
        weatherDto.setMain(weather.getMain());

        return weatherDto;
    }
}
