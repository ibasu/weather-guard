package au.com.vanguard.weather.repository;

import au.com.vanguard.weather.repository.entity.Weather;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherRepository extends JpaRepository<Weather, Long> {

}
