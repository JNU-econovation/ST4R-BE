package star.home.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import star.home.constants.Period;

@Component
public class StringToPeriodConverter implements Converter<String, Period> {

    @Override
    public Period convert(String source) {
        return Period.from(source);
    }
}
