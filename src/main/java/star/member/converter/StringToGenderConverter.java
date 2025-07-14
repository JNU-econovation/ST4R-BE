package star.member.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import star.member.model.vo.Gender;

@Component
public class StringToGenderConverter implements Converter<String, Gender>{

    @Override
    public Gender convert(String source) {
        return Gender.from(source);
    }

}
