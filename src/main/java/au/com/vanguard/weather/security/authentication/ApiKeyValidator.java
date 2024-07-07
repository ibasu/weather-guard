package au.com.vanguard.weather.security.authentication;

import au.com.vanguard.weather.web.client.ClientConfiguration;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApiKeyValidator implements RequestHeaderValidator, ConstraintValidator<ValidateApiKey, String> {

    @Autowired
    private ClientConfiguration clientConfiguration;

    @Override
    public void initialize(ValidateApiKey constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String apiKeyHeader, ConstraintValidatorContext context) {
        try {
            return clientConfiguration.getDummyTokens().contains(apiKeyHeader) ? Boolean.TRUE : Boolean.FALSE;
        } catch (final Exception e) {
            log.error("Error while validating incoming api key tokens", e);
            return false;
        }
    }
}
