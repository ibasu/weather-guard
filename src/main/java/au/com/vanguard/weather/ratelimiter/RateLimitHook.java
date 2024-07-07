package au.com.vanguard.weather.ratelimiter;

import au.com.vanguard.weather.common.Constants;
import au.com.vanguard.weather.exception.RateLimitExceededException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@AllArgsConstructor
public class RateLimitHook {

    private RateLimitConfiguration rateLimitConfiguration;
    private RateLimiter rateLimiter;

    @Around("@annotation(RateLimit)")
    public Object enforceRateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("JointPoint in RateLimit Hook: {}", joinPoint);

        String apiKey = (String) joinPoint.getArgs()[1];
        log.info("API Key in the request header : {}", apiKey);

        if (!rateLimiter.tryAcquire(apiKey, rateLimitConfiguration.getMaxRequests(), rateLimitConfiguration.getLimitSeconds())) {
            throw new RateLimitExceededException("Rate limit exceeded", Constants.HTTP_HEADER_API_KEY.concat("-").concat(apiKey));
        }
        return joinPoint.proceed();
    }
}
