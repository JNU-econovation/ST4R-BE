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
    private final HeartService heartService;
    private final MemberService memberService;

    public void createHeart(MemberInfoDTO memberInfoDTO, Long boardId) {
        Member member = memberService.getMemberEntityById(memberInfoDTO.id());
        Board board = boardService.getBoardEntity(boardId);
        heartService.createHeart(member, board);
    }

    public void deleteHeart(MemberInfoDTO memberInfoDTO, Long boardId) {
        Member member = memberService.getMemberEntityById(memberInfoDTO.id());
        Board board = boardService.getBoardEntity(boardId);
        heartService.deleteHeart(member, board);
    }
}
