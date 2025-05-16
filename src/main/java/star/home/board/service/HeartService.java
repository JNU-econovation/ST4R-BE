package star.home.board.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.home.board.exception.AlreadyHeartedException;
import star.home.board.model.entity.Board;
import star.home.board.model.entity.Heart;
import star.home.board.repository.HeartRepository;
import star.member.model.entity.Member;

@Service
@RequiredArgsConstructor
public class HeartService {

    private final HeartRepository heartRepository;


    @Transactional
    public Long createHeart(Member member, Board board) {
        if (heartRepository.existsByMemberIdAndBoardId(member.getId(), board.getId())) {
            throw new AlreadyHeartedException();
        }

        Heart heart = Heart.builder()
                .member(member)
                .board(board)
                .build();

        heartRepository.save(heart);
        return heart.getId();
    }

    @Transactional
    public void deleteHeartsByBoardDelete(Long boardId) {
        heartRepository.deleteHeartsByBoardId(boardId);
    }

    @Transactional
    public void cancelHeart(Member member, Board board) {
        if (!heartRepository.existsByMemberIdAndBoardId(member.getId(), board.getId())) {
            throw new AlreadyHeartedException();
        }

        heartRepository.deleteHeartByMemberIdAndBoardId(member.getId(), board.getId());
    }

    @Transactional(readOnly = true)
    public Boolean hasLiked(Long memberId, Long boardId) {
        return heartRepository.existsByMemberIdAndBoardId(memberId, boardId);
    }

}
