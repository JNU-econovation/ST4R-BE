package star.home.comment.service;

import static star.common.constants.CommonConstants.ANONYMOUS_MEMBER_ID;

import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.common.exception.server.InternalServerException;
import star.common.service.BaseRetryRecoverService;
import star.home.board.mapper.BoardCommentMapper;
import star.home.board.model.entity.Board;
import star.home.comment.dto.CommentDTO;
import star.home.comment.dto.request.CommentRequest;
import star.home.comment.dto.response.CommentResponse;
import star.home.comment.model.entity.Comment;
import star.home.comment.service.internal.CommentDataService;
import star.member.dto.MemberInfoDTO;

@Service
@RequiredArgsConstructor
public class CommentService extends BaseRetryRecoverService {

    private final CommentDataService commentDataService;

    @Transactional
    public Long createComment(MemberInfoDTO memberInfoDTO, CommentRequest request, Board board) {
        return commentDataService.createComment(memberInfoDTO, request, board);
    }

    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsPage(@Nullable MemberInfoDTO memberInfoDTO,
            Long boardId, Pageable pageable) {

        boolean haveComment = commentDataService.existsComment(boardId);

        if (!haveComment) {
            return new PageImpl<>(new ArrayList<>());
        }

        //fetchType이 LAZY 라서 성능 문제 피해갈 수 있음
        List<Comment> rootComments = commentDataService.getRootCommentEntities(boardId, pageable);
        List<Long> rootCommentIds = rootComments.stream().map(Comment::getId).toList();
        List<Comment> notRootComments = commentDataService.getChildCommentEntitiesUsingRootCommentIds(
                boardId, rootCommentIds);
        List<CommentDTO> commentDTOs = makeCommentDTOList(rootComments, notRootComments);

        Long viewerId = (memberInfoDTO != null) ? memberInfoDTO.id() : ANONYMOUS_MEMBER_ID;
        Long authorId = rootComments.getFirst().getAuthor().getId();

        List<CommentResponse> commentResponseList = BoardCommentMapper.toCommentVOs(commentDTOs,
                viewerId, authorId);

        return new PageImpl<>(commentResponseList, pageable, commentDTOs.size());
    }

    @Transactional
    public void updateComment(Long boardId, Long commentId, MemberInfoDTO memberInfoDTO,
            CommentRequest request) {
        commentDataService.updateComment(boardId, commentId, memberInfoDTO, request);
    }

    @Transactional
    public void softDeleteComment(Long boardId, Long commentId, MemberInfoDTO memberInfoDTO) {
        commentDataService.softDeleteComment(boardId, commentId, memberInfoDTO);
    }

    @Transactional
    public void hardDeleteAllComments(Long boardId) {
        commentDataService.hardDeleteAllComments(boardId);
    }


    private List<CommentDTO> makeCommentDTOList(List<Comment> rootComments,
            List<Comment> notRootComments) {
        List<CommentDTO> result = new ArrayList<>();

        //Key == Comment의 ID
        Map<Long, CommentDTO> CommentsMap = new HashMap<>();

        rootComments.forEach(rootComment -> {
            CommentDTO rootCommentDTO = CommentDTO.from(rootComment);
            CommentsMap.put(rootComment.getId(), rootCommentDTO);
            result.add(rootCommentDTO);
        });

        for (Comment iLevelComment : notRootComments) {
            CommentDTO iLevelCommentDTO = CommentDTO.from(iLevelComment);
            Long iLevelCommentId = iLevelComment.getId();
            Long parentCommentId = iLevelComment.getParentComment().getId();
            CommentDTO parentDTO = CommentsMap.get(parentCommentId);

            if (parentDTO == null) {
                throw new InternalServerException(
                        "id가 %d인 부모 댓글을 찾을 수 없습니다.".formatted(parentCommentId));
            }
            CommentsMap.put(iLevelCommentId, iLevelCommentDTO);
            parentDTO.addChildCommentDTO(iLevelCommentDTO);
        }

        return result;
    }

}
