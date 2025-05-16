package star.home.board.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import star.home.board.dto.request.BoardRequest;
import star.home.board.dto.response.BoardResponse;
import star.home.board.exception.NoSuchBoardException;
import star.home.board.model.entity.Board;
import star.home.board.repository.BoardRepository;
import star.home.category.service.CategoryService;
import star.member.dto.MemberInfoDTO;
import star.member.model.entity.Member;
import star.member.service.MemberService;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final MemberService memberService;
    private final CategoryService categoryService;
    private final BoardImageService boardImageService;
    private final BoardRepository boardRepository;
    private final HeartService heartService;

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
    public BoardResponse getBoard(MemberInfoDTO memberInfoDTO, Long boardId) {

    }

    @Transactional
    public void updateBoard(MemberInfoDTO memberInfoDTO, Long boardId, BoardRequest request) {
        Member member = memberService.getMemberEntityById(memberInfoDTO.id());
        Board board = getBoardEntity(boardId);

        if (!member.equals(board.getMember())) {
            throw new NoSuchBoardException();
        }

        board = Board.builder()
                .member(member)
                .title(request.title())
                .category(categoryService.getCategory(request.category()))
                .content(request.content())
                .build();

        boardRepository.save(board);
        boardImageService.overwriteImageUrls(board, request.imageUrls());
    }

    @Transactional
    public void deleteBoard(MemberInfoDTO memberInfoDTO, Long boardId) {
        Member member = memberService.getMemberEntityById(memberInfoDTO.id());
        Board board = getBoardEntity(boardId);

        if (!member.equals(board.getMember())) {
            throw new NoSuchBoardException();
        }

        heartService.deleteHeartsByBoardDelete(boardId);
        boardImageService.deleteBoardImageUrls(boardId);
    }


    @Transactional(readOnly = true)
    public Board getBoardEntity(Long boardId) {
        return boardRepository.getBoardById((boardId)).orElseThrow(NoSuchBoardException::new);
    }
}
