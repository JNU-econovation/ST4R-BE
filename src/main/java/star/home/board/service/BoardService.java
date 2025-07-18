package star.home.board.service;

import static star.common.constants.CommonConstants.ANONYMOUS_MEMBER_ID;

import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.common.constants.CommonConstants;
import star.common.exception.client.YouAreNotAuthorException;
import star.common.service.BaseRetryRecoverService;
import star.home.board.dto.BoardImageDTO;
import star.home.board.dto.BoardSearchDTO;
import star.home.board.dto.request.BoardRequest;
import star.home.board.dto.response.BoardResponse;
import star.home.board.exception.NoSuchBoardException;
import star.home.board.model.entity.Board;
import star.home.board.repository.BoardRepository;
import star.home.category.service.CategoryService;
import star.home.comment.service.CommentCoordinateService;
import star.home.dto.BoardPeekDTO;
import star.member.dto.MemberInfoDTO;
import star.member.model.entity.Member;
import star.member.service.MemberService;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService extends BaseRetryRecoverService {

    private final MemberService memberService;
    private final CategoryService categoryService;
    private final BoardImageService boardImageService;
    private final BoardHeartService boardHeartService;
    private final CommentCoordinateService commentCoordinateService;
    private final BoardRepository boardRepository;

    @PersistenceContext
    private EntityManager entityManager;

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

    public Page<BoardPeekDTO> getBoardPeeks(
            @Nullable MemberInfoDTO memberInfoDTO,
            BoardSearchDTO searchDTO,
            Pageable pageable
    ) {

        Long viewerId = (memberInfoDTO != null) ? memberInfoDTO.id() : ANONYMOUS_MEMBER_ID;

        Page<Board> boardsPage = boardRepository.searchBoards(searchDTO, pageable);

        List<BoardPeekDTO> boardPeekDTOs = boardsPage.getContent().stream()
                .map(board -> {
                    List<BoardImageDTO> images = boardImageService.getImageUrls(board.getId());
                    String thumbnailUrl = images.isEmpty() ? null : images.getFirst().imageUrl();
                    return BoardPeekDTO.from(
                            board,
                            thumbnailUrl,
                            boardHeartService.hasHearted(viewerId, board.getId())
                    );
                }).toList();

        return new PageImpl<>(boardPeekDTOs, pageable, boardsPage.getTotalElements());
    }


    @Transactional
    public BoardResponse getBoard(@Nullable MemberInfoDTO memberInfoDTO, Long boardId) {

        Board board = getBoardEntity(boardId);
        Long viewerId = (memberInfoDTO != null) ? memberInfoDTO.id() : ANONYMOUS_MEMBER_ID;
        MemberInfoDTO authorInfo = memberService.getMemberById(board.getMember().getId());
        Long authorId = authorInfo.id();

        increaseViewCount(board);

        List<String> imageUrlStrings = boardImageService.getImageUrls(boardId)
                .stream()
                .map(BoardImageDTO::imageUrl)
                .toList();

        return BoardResponse.from(
                board,
                viewerId.equals(authorId),
                boardHeartService.hasHearted(viewerId, boardId),
                imageUrlStrings
        );

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
            throw new YouAreNotAuthorException();
        }

        commentCoordinateService.hardDeleteAllComments(boardId);
        boardHeartService.deleteHeartsByBoardDelete(boardId);
        boardImageService.deleteBoardImageUrls(boardId);
        boardRepository.delete(board);
    }

    @Transactional(readOnly = true)
    public Board getBoardEntity(Long boardId) {
        return boardRepository.getBoardById((boardId)).orElseThrow(NoSuchBoardException::new);
    }

    @Retryable(
            retryFor = OptimisticLockException.class,
            maxAttempts = CommonConstants.OPTIMISTIC_ATTEMPT_COUNT,
            backoff = @Backoff(delay = 100)
    )
    private void increaseViewCount(Board board) {
        board.increaseViewCount();
        entityManager.flush();
    }
}