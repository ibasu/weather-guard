package au.com.vanguard.weather.exception;

import lombok.Getter;

@Getter
public class ServerException extends BaseException {
    private ApiError apiError;

    public ServerException(String message) {
        super(message);
    }

    public ServerException(ApiError apiError) {
        super(apiError.getErrorMessage());
    }
}
