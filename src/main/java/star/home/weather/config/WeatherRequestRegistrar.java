package star.home.weather.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties(WeatherRequestConfig.class)
@PropertySource({"classpath:application-secret.properties",
        "classpath:application-weather.properties"})
public class WeatherRequestRegistrar {

}
