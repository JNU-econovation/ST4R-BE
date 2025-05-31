package star.common.dto.internal;

import lombok.Builder;

@Builder
public record Author(
        Long id,
        String imageUrl,
        String nickname
) {

}
