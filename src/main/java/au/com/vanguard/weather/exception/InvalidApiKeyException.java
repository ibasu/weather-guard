package au.com.vanguard.weather.exception;

import lombok.Getter;

@Getter
public class InvalidApiKeyException extends BaseException {
    private ApiError apiError;

    public InvalidApiKeyException(String message) {
        super(message);
    }

    public InvalidApiKeyException(String message, String fieldName) {
        super(message, fieldName);
    }

    public InvalidApiKeyException(ApiError apiError) {
        super(apiError.getErrorMessage(), apiError.getErrorDetails());
    }

}
