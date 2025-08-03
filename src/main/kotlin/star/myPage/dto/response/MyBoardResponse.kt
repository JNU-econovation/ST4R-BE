package star.myPage.dto.response

import star.common.util.CommonTimeUtils
import star.home.board.constants.BoardConstants.CONTENT_PREVIEW_MAX_LENGTH
import star.home.board.model.entity.Board
import star.home.dto.BoardPeekDTO
import java.time.OffsetDateTime


data class MyBoardResponse(
    val id: Long,
    val liked: Boolean,
    val title: String,
    val thumbnailImageUrl: String?,
    val contentPreview: String,
    val category: String,
    val viewCount: Int,
    val createdAt: OffsetDateTime,
    val likeCount: Int,
    val commentCount: Int
) {
    companion object {

        fun from(boardPeekDTO: BoardPeekDTO): MyBoardResponse =
            MyBoardResponse(
                id = boardPeekDTO.id,
                liked = boardPeekDTO.liked,
                title = boardPeekDTO.title,
                thumbnailImageUrl = boardPeekDTO.imageUrl,
                contentPreview = boardPeekDTO.contentPreview,
                category = boardPeekDTO.category,
                viewCount = boardPeekDTO.viewCount,
                createdAt = boardPeekDTO.createdAt,
                likeCount = boardPeekDTO.likeCount,
                commentCount = boardPeekDTO.commentCount
            )

        fun fromForLikedBoard(board: Board, thumbnailImageUrl: String?): MyBoardResponse =
            MyBoardResponse(
                id = board.id,
                liked = true,
                title = board.title.value,
                thumbnailImageUrl = thumbnailImageUrl,
                contentPreview = board.content.text.take(CONTENT_PREVIEW_MAX_LENGTH),
                category = board.category.name.toString(),
                viewCount = board.viewCount,
                createdAt = CommonTimeUtils.convertLocalDateTimeToOffsetDateTime(board.createdAt),
                likeCount = board.heartCount,
                commentCount = board.commentCount
            )

    }
}

