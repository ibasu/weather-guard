package au.com.vanguard.weather.security.authentication;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.stream.Collectors;

public interface RequestHeaderValidator {
    default boolean isValid(HttpHeaders headers, List<String> mandatoryHeaders) {
        List<String> missingHeaders = mandatoryHeaders.stream().filter(h -> {
            return StringUtils.isNotBlank(headers.getFirst(h));
        }).collect(Collectors.toList());

        return mandatoryHeaders.isEmpty() ? Boolean.TRUE : Boolean.FALSE;
    }
}
