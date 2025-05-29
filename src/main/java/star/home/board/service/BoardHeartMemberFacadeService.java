package star.home.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import star.home.board.model.entity.Board;
import star.member.dto.MemberInfoDTO;
import star.member.model.entity.Member;
import star.member.service.MemberService;

@Service
@RequiredArgsConstructor
public class BoardHeartMemberFacadeService {
    private final BoardService boardService;
    private final BoardHeartService boardHeartService;
    private final MemberService memberService;

    public void createHeart(MemberInfoDTO memberInfoDTO, Long boardId) {
        Member member = memberService.getMemberEntityById(memberInfoDTO.id());
        Board board = boardService.getBoardEntity(boardId);
        boardHeartService.createHeart(member, board);
    }

    public void deleteHeart(MemberInfoDTO memberInfoDTO, Long boardId) {
        Member member = memberService.getMemberEntityById(memberInfoDTO.id());
        Board board = boardService.getBoardEntity(boardId);
        boardHeartService.deleteHeart(member, board);
    }
}
