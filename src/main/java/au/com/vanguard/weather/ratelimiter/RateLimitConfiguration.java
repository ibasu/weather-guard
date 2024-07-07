package au.com.vanguard.weather.ratelimiter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class RateLimitConfiguration {

    @Value("${infrastructure.rate.limit.requests}")
    private int maxRequests;

    @Value("${infrastructure.rate.limit.seconds}")
    private int limitSeconds;
}
