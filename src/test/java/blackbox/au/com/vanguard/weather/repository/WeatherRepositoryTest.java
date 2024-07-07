package au.com.vanguard.weather.repository;

import au.com.vanguard.weather.mapper.EntityConverter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class WeatherRepositoryTest {

    @Autowired
    private WeatherRepository weatherRepository;

    private EntityConverter entityConverter = new EntityConverter();

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(weatherRepository).isNotNull();
    }

}
