package au.com.vanguard.weather.exception.handler;

import au.com.vanguard.weather.exception.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private void logException(Throwable throwable) {
        log.error("Exception : {}", throwable);
    }

    private List<ApiError> processErrors(BaseException ex, HttpStatus httpStatus) {
        return Arrays.asList(ApiError.builder()
                .errorDetails(ex.getFieldName())
                .errorId(httpStatus.toString())
                .errorMessage(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build());
    }

    private List<ApiError> processFieldErrors(Set<ConstraintViolation<?>> fieldErrors, HttpStatus httpStatus) {
        List<ApiError> errorsList = new ArrayList<>();

        fieldErrors.forEach(e -> errorsList.add(ApiError.builder()
                .errorDetails(e.getPropertyPath().toString())
                .errorId(httpStatus.toString())
                .timestamp(LocalDateTime.now())
                .errorMessage(e.getMessage()).build()));

        log.error(errorsList.toString());

        return errorsList;
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public List<ApiError> handleRateLimitExceededException(final RateLimitExceededException rateLimitException) {
        return processErrors(rateLimitException, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler({WeatherNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public List<ApiError> onNotFoundException(BaseException ex) {
        return processErrors(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ApiError> onBusinessException(BusinessException ex) {
        return processErrors(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public List<ApiError> onServerException(ServerException ex) {
        return processErrors(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidApiKeyException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public List<ApiError> onInvalidApiKeyException(InvalidApiKeyException ex) {
        return processErrors(ex, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    List<ApiError> onConstraintValidationException(jakarta.validation.ConstraintViolationException e) {
        logException(e);
        List<ApiError> apiErrors = processFieldErrors(e.getConstraintViolations(), HttpStatus.BAD_REQUEST);

        return apiErrors;
    }
}
