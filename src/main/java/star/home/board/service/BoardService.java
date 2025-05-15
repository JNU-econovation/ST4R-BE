package star.home.board.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import star.home.board.dto.request.BoardRequest;
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

        return board.getId();
    }
}
