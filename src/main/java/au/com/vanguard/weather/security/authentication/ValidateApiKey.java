package au.com.vanguard.weather.security.authentication;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = ApiKeyValidator.class)
@Target({PARAMETER, TYPE, METHOD})
@Retention(RUNTIME)
@Documented
public @interface ValidateApiKey {

    String message() default "Invalid API KEY";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

