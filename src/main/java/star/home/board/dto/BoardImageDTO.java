package star.home.board.dto;

import star.home.board.model.entity.BoardImage;

public record BoardImageDTO(
        String imageUrl
) {
    public static BoardImageDTO from(BoardImage boardImage) {
        return new BoardImageDTO(boardImage.getImageUrl());
    }
}
