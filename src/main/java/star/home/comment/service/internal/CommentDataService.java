package star.home.comment.service.internal;


import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.common.constants.CommonConstants;
import star.common.exception.client.YouAreNotAuthorException;
import star.common.service.BaseRetryRecoverService;
import star.home.board.model.entity.Board;
import star.home.comment.dto.request.CommentRequest;
import star.home.comment.exception.InvalidIdCommentException;
import star.home.comment.model.entity.Comment;
import star.home.comment.repository.CommentRepository;
import star.member.dto.MemberInfoDTO;
import star.member.model.entity.Member;
import star.member.service.MemberService;

@Service
@RequiredArgsConstructor
public class CommentDataService {

    private static final Integer ROOT_COMMENT_DEPTH = 0;

    private final MemberService memberService;
    private final CommentRepository commentRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public Boolean existsComment(Long boardId) {
        return commentRepository.existsByBoardId(boardId);
    }

    @Transactional
    public Long createComment(MemberInfoDTO memberInfoDTO, CommentRequest request, Board board) {
        Member member = memberService.getMemberEntityById(memberInfoDTO.id());
        Comment parentComment = null;
        Comment rootComment = null;

        int depth = ROOT_COMMENT_DEPTH;

        if (request.parentCommentId() != null) {
            parentComment = commentRepository.getCommentByIdAndBoardId(request.parentCommentId(),
                            board.getId())
                    .orElseThrow(InvalidIdCommentException::new);
            rootComment = parentComment.getRootComment();
            depth = parentComment.getDepth() + 1;
        }

        Comment comment = Comment.builder()
                .board(board)
                .parentComment(parentComment)
                .author(member)
                .depth(depth)
                .content(request.content())
                .build();

        if (rootComment == null) {
            commentRepository.save(comment);
            /*
             * save 안하면 db에 반영 안되어서
             * org.hibernate.TransientPropertyValueException 이 뜸
             */
        }

        comment.setRootComment(Objects.requireNonNullElse(rootComment, comment));
        increaseCommentCount(board);
        commentRepository.save(comment);

        return comment.getId();
    }

    @Transactional(readOnly = true)
    public List<Comment> getRootCommentEntities(Long boardId, Pageable pageable) {
        return commentRepository.getBoardsByBoardIdAndDepth(boardId, ROOT_COMMENT_DEPTH, pageable);
    }

    @Transactional(readOnly = true)
    public List<Comment> getChildCommentEntitiesUsingRootCommentIds(Long boardId,
            List<Long> rootCommentIds) {
        return commentRepository.getCommentsByBoardIdAndRootCommentIdIn(boardId, rootCommentIds);
    }

    @Transactional
    public void updateComment(Long boardId, Long commentId, MemberInfoDTO memberInfoDTO,
            CommentRequest request) {
        Comment comment = commentRepository.getCommentByIdAndBoardId(commentId, boardId)
                .orElseThrow(InvalidIdCommentException::new);

        if (!comment.getAuthor().getId().equals(memberInfoDTO.id())) {
            throw new YouAreNotAuthorException();
        }

        comment.updateComment(request.content());
    }

    @Transactional
    public void hardDeleteAllComments(Long boardId) {
        commentRepository.deleteCommentsByBoardId(boardId);
    }

    @Transactional
    public void softDeleteComment(Long boardId, Long commentId, MemberInfoDTO memberInfoDTO) {
        Comment comment = commentRepository.getCommentByIdAndBoardId(commentId, boardId)
                .orElseThrow(InvalidIdCommentException::new);

        if (!comment.getAuthor().getId().equals(memberInfoDTO.id())) {
            throw new YouAreNotAuthorException();
        }

        comment.markAsDeprecated();
    }

    @Retryable(
            retryFor = OptimisticLockException.class,
            maxAttempts = CommonConstants.OPTIMISTIC_ATTEMPT_COUNT,
            backoff = @Backoff(delay = 100)
    )
    private void increaseCommentCount(Board board) {
        board.increaseCommentCount();
        entityManager.flush();
    }
}
