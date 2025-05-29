package star.common.dto.response.internal;

import lombok.Builder;

@Builder
public record Author(
        Long id,
        String imageUrl,
        String nickname
) {

}
