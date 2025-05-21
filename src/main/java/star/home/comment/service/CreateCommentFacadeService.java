package star.home.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.home.board.model.entity.Board;
import star.home.board.service.BoardService;
import star.home.comment.dto.request.CommentRequest;
import star.member.dto.MemberInfoDTO;

@Service
@RequiredArgsConstructor
public class CreateCommentFacadeService { //순환참조 때매 만듦
    private final BoardService boardService;
    private final CommentService commentService;

    @Transactional
    public Long createComment(MemberInfoDTO memberInfoDTO, CommentRequest request, Long boardId) {
        Board board = boardService.getBoardEntity(boardId);
        return commentService.createComment(memberInfoDTO, request, board);
    }
}
