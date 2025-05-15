package star.home.board.dto.request;

import java.util.List;
import star.home.board.model.vo.Content;
import star.home.board.model.vo.Title;

public record BoardRequest(
        Title title,
        List<String> imageUrls, //todo: vo로 만들어서 url 정규식 유효성 검사
        Content content,
        String category
) {

}
