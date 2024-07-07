package au.com.vanguard.weather.exception;

public class RateLimitExceededException extends BusinessException {

    public RateLimitExceededException(String message, String fieldName) {
        super(message, fieldName);
    }

}
