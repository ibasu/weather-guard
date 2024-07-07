package au.com.vanguard.weather.exception;

public class WeatherNotFoundException extends BaseException {
    public WeatherNotFoundException(String message, String fieldName) {
        super(message, fieldName);
    }
}
