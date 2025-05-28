package star.common.resolver;

import java.util.List;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import star.common.annotation.ResolvePageable;
import star.common.constants.SortField;
import star.common.converter.CustomPageRequestToPageableConverter;
import star.common.dto.request.CustomPageRequest;

@Component
public class CustomPageableArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(ResolvePageable.class)
                && parameter.getParameterType().equals(Pageable.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory
    ) {
        ResolvePageable annotation = parameter.getParameterAnnotation(ResolvePageable.class);
        List<SortField> allowedFields = List.of(annotation.allowed());

        String sort = webRequest.getParameter("sort");
        String dir = webRequest.getParameter("direction");
        String page = webRequest.getParameter("page");
        String size = webRequest.getParameter("size");

        CustomPageRequest request = new CustomPageRequest(sort, dir,
                page != null ? Integer.parseInt(page) : null,
                size != null ? Integer.parseInt(size) : null);

        return new CustomPageRequestToPageableConverter(allowedFields).convert(request);
    }
}

