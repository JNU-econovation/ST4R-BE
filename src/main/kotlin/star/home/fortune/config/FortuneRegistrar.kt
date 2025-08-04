package star.home.fortune.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@EnableConfigurationProperties(FortuneConfig::class)
@PropertySource("classpath:application-fortune.properties")
class FortuneRegistrar
