package star.home.board.service;

import jakarta.annotation.Nullable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.home.board.dto.request.BoardImageDTO;
import star.home.board.dto.request.BoardRequest;
import star.home.board.dto.response.BoardResponse;
import star.home.board.dto.response.BoardResponse.Author;
import star.home.board.dto.response.BoardResponse.Comment;
import star.home.board.exception.NoSuchBoardException;
import star.home.board.mapper.BoardCommentMapper;
import star.home.board.model.entity.Board;
import star.home.board.model.vo.Content;
import star.home.board.repository.BoardRepository;
import star.home.category.service.CategoryService;
import star.home.comment.dto.CommentDTO;
import star.home.comment.service.CommentService;
import star.member.dto.MemberInfoDTO;
import star.member.model.entity.Member;
import star.member.service.MemberService;

@Service
@RequiredArgsConstructor
public class BoardService {
    private static final Long ANONYMOUS_MEMBER_ID = -12345678L;

    private final MemberService memberService;
    private final CategoryService categoryService;
    private final BoardImageService boardImageService;
    private final HeartService heartService;
    private final CommentService commentService;
    private final BoardRepository boardRepository;


    @Transactional
    public Long createBoard(MemberInfoDTO memberInfoDTO, BoardRequest request) {
        Member member = memberService.getMemberEntityById(memberInfoDTO.id());
        Board board = Board.builder()
                .member(member)
                .title(request.title())
                .category(categoryService.getCategory(request.category()))
                .content(request.content())
                .build();

        boardRepository.save(board);
        boardImageService.addImageUrls(board, request.imageUrls());

        return board.getId();
    }

    @Transactional(readOnly = true)
    public BoardResponse getBoard(@Nullable MemberInfoDTO memberInfoDTO, Long boardId) {
        Board board = getBoardEntity(boardId);
        Long viewerId = (memberInfoDTO != null) ? memberInfoDTO.id() : ANONYMOUS_MEMBER_ID;
        MemberInfoDTO authorMember = memberService.getMemberById(board.getMember().getId());
        Long authorId = authorMember.id();

        List<CommentDTO> commentDTOs = commentService.getComments(boardId);
        List<Comment> commentVOs = BoardCommentMapper.toCommentVOs(commentDTOs, viewerId, authorId);

        Author author = Author.builder()
                .id(authorId)
                .imageUrl(authorMember.profileImageUrl())
                .nickname(authorMember.email().value())
                .build();

        List<String> imageUrlStrings = boardImageService.getImageUrls(boardId)
                .stream()
                .map(BoardImageDTO::imageUrl)
                .toList();

        return BoardResponse.builder()
                .id(boardId)
                .author(author)
                .isViewerAuthor(viewerId.equals(authorId))
                .liked(heartService.hasLiked(viewerId, boardId))
                .title(board.getTitle().value())
                .imageUrls(imageUrlStrings)
                .content(Content.copyOf(board.getContent())) //안전하게 깊은 복사하기
                .category(board.getCategory().getName())
                .viewCount(board.getViewCount())
                .createdAt(board.getCreatedAt())
                .likeCount(board.getHeartCount())
                .commentCount(board.getCommentCount())
                .comments(commentVOs)
                .build();
    }

    @Transactional
    public void updateBoard(MemberInfoDTO memberInfoDTO, Long boardId, BoardRequest request) {
        Member member = memberService.getMemberEntityById(memberInfoDTO.id());
        Board board = getBoardEntity(boardId);

        if (!member.equals(board.getMember())) {
            throw new NoSuchBoardException();
        }
        board.update(request.title(), request.content(),
                categoryService.getCategory(request.category()));

        boardImageService.overwriteImageUrls(board, request.imageUrls());
    }

    @Transactional
    public void deleteBoard(MemberInfoDTO memberInfoDTO, Long boardId) {
        Member member = memberService.getMemberEntityById(memberInfoDTO.id());
        Board board = getBoardEntity(boardId);

        if (!member.equals(board.getMember())) {
            throw new NoSuchBoardException();
        }

        commentService.hardDeleteComments(boardId);
        heartService.deleteHeartsByBoardDelete(boardId);
        boardImageService.deleteBoardImageUrls(boardId);
        boardRepository.delete(board);
    }

    @Transactional(readOnly = true)
    public Board getBoardEntity(Long boardId) {
        return boardRepository.getBoardById((boardId)).orElseThrow(NoSuchBoardException::new);
    }
}
