package au.com.vanguard.weather.web.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class WeatherResponse {
    private String description;

}
