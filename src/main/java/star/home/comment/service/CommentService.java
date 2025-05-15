package star.home.comment.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.home.board.model.entity.Board;
import star.home.board.service.BoardService;
import star.home.comment.dto.request.CommentRequest;
import star.home.comment.exception.InvalidIdCommentException;
import star.home.comment.model.entity.Comment;
import star.home.comment.repository.CommentRepository;
import star.member.dto.MemberInfoDTO;
import star.member.model.entity.Member;
import star.member.service.MemberService;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final MemberService memberService;
    private final BoardService boardService;
    private final CommentRepository commentRepository;

    @Transactional
    public Long createComment(MemberInfoDTO memberInfoDTO, Long boardId, CommentRequest request) {
        Member member = memberService.getMemberEntityById(memberInfoDTO.id());
        Board board = boardService.getBoardEntity(boardId);
        Comment parentComment = null;
        Integer depth = 1;

        if (request.parentCommentId() != null) {
            parentComment = commentRepository.getCommentByIdAndBoardId(request.parentCommentId(), boardId)
                    .orElseThrow(InvalidIdCommentException::new);
            depth = parentComment.getDepth() + 1;
        }

        Comment comment = Comment.builder()
                .board(board)
                .parentComment(parentComment)
                .author(member)
                .depth(depth)
                .content(request.content())
                .build();

        commentRepository.save(comment);

        return comment.getId();
    }
}
