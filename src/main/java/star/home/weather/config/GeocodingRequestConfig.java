package star.home.weather.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "geocoding")
public record GeocodingRequestConfig(
        String restApiKey,
        String requestBaseUrl
) {

}
