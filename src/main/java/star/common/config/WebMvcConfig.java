package star.common.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import star.common.resolver.CustomPageableArgumentResolver;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final CustomPageableArgumentResolver resolver;

    public WebMvcConfig(CustomPageableArgumentResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(resolver);
    }
}
