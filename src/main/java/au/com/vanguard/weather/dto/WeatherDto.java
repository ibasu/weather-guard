package au.com.vanguard.weather.dto;

import lombok.Data;

@Data
public class WeatherDto {
    private Integer id;
    private String main;
    private String icon;
    private String description;
}
