package star.common.dto.request;

import lombok.Builder;

@Builder
public record CustomPageRequest(
        String sort,
        String direction,
        Integer size,
        Integer page
) {

}