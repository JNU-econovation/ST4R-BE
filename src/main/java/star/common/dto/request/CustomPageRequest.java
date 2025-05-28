package star.common.dto.request;

public record CustomPageRequest(
        String sort,
        String direction,
        Integer size,
        Integer page
) {

}