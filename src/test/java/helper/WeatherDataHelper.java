package helper;

import au.com.vanguard.weather.dto.WeatherDto;
import au.com.vanguard.weather.pojo.Openweatherresponse;
import au.com.vanguard.weather.repository.entity.Weather;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.ResourceUtils;

import java.nio.file.Path;

public final class WeatherDataHelper {

    public static final Long DEFAULT_WEATHER_ID = 721l;
    public static final String DEFAULT_WEATHER_MAIN = "Haze";
    public static final String DEFAULT_WEATHER_DESCRIPTION = "haze";
    public static final String DEFAULT_WEATHER_ICON = "50d";
    public static final String DEFAULT_WEATHER_COUNTRY_CODE = "IND";
    public static final String DEFAULT_WEATHER_CITY_NAME = "Kolkata";
    public static final String OPEN_WEATHER_URI = "/data/2.5/weather?q=%s,%s&appid=%s";

    private final static ObjectMapper objectMapper = new ObjectMapper();

    public static WeatherDto defaultWeatherDTO() {
        WeatherDto weatherDto = new WeatherDto();
        weatherDto.setMain(DEFAULT_WEATHER_MAIN);
        weatherDto.setIcon(DEFAULT_WEATHER_ICON);
        weatherDto.setId(DEFAULT_WEATHER_ID.intValue());
        weatherDto.setDescription(DEFAULT_WEATHER_DESCRIPTION);

        return weatherDto;
    }

    public static Weather defaultWeatherEntity() {
        Weather weather = new Weather();
        weather.setMain(DEFAULT_WEATHER_MAIN);
        weather.setIcon(DEFAULT_WEATHER_ICON);
        weather.setId(DEFAULT_WEATHER_ID);
        weather.setDescription(DEFAULT_WEATHER_DESCRIPTION);

        return weather;
    }

    public static Openweatherresponse defaultOpenWeatherResponse() throws Exception {
        Path filePath = ResourceUtils.getFile("classpath:__files/openweather/200_weather_response.json").toPath();
        Openweatherresponse openweatherresponse = objectMapper.readValue(filePath.toFile(), Openweatherresponse.class);

        return openweatherresponse;
    }
}
