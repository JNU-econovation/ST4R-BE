package star.home.board.dto.request;

import static star.common.constants.CommonConstants.MAX_IMAGE_COUNT;
import static star.home.constants.HomeConstants.TITLE_MAX_LENGTH;
import static star.home.constants.HomeConstants.TITLE_MIN_LENGTH;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;
import star.home.board.model.vo.Content;
import jakarta.validation.constraints.NotBlank;

@Builder
public record BoardRequest(

        @NotBlank(message = "제목을 입력해주세요")
        @Size(min = TITLE_MIN_LENGTH, max = TITLE_MAX_LENGTH, message = "제목의 길이는 최소 {min}자, 최대 {max}자여야 합니다.")
        String title,
        
        @NotNull(message = "이미지 url 필드를 입력해주세요")
        @Size(max = MAX_IMAGE_COUNT, message = "이미지는 최대 {max}개까지 등록할 수 있습니다.")
        List<String> imageUrls, //todo: vo로 만들어서 url 정규식 유효성 검사

        Content content,

        String category
) {

}
