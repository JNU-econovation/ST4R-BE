package star.home.board.service;


import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.common.constants.CommonConstants;
import star.common.service.BaseRetryRecoverService;
import star.home.board.exception.AlreadyCanceledHeartException;
import star.home.board.exception.AlreadyHeartedException;
import star.home.board.model.entity.Board;
import star.home.board.model.entity.Heart;
import star.home.board.repository.HeartRepository;
import star.member.model.entity.Member;

@Service
@RequiredArgsConstructor
public class HeartService extends BaseRetryRecoverService {

    private final HeartRepository heartRepository;

    @PersistenceContext
    private EntityManager entityManager;


    @Transactional
    public void createHeart(Member member, Board board) {
        if (heartRepository.existsByMemberIdAndBoardId(member.getId(), board.getId())) {
            throw new AlreadyHeartedException();
        }

        Heart heart = Heart.builder().member(member).board(board).build();
        increaseHeartCount(board);
        heartRepository.save(heart);
    }


    @Transactional
    public void deleteHeart(Member member, Board board) {
        if (!heartRepository.existsByMemberIdAndBoardId(member.getId(), board.getId())) {
            throw new AlreadyCanceledHeartException();
        }

        decreaseHeartCount(board);
        heartRepository.deleteHeartByMemberIdAndBoardId(member.getId(), board.getId());
    }

    @Transactional(readOnly = true)
    public Boolean hasHearted(Long memberId, Long boardId) {
        return heartRepository.existsByMemberIdAndBoardId(memberId, boardId);
    }


    @Transactional
    public void deleteHeartsByBoardDelete(Long boardId) {
        heartRepository.deleteHeartsByBoardId(boardId);
    }

    @Retryable(
            retryFor = OptimisticLockException.class,
            maxAttempts = CommonConstants.OPTIMISTIC_ATTEMPT_COUNT,
            backoff = @Backoff(delay = 100)
    )
    private void increaseHeartCount(Board board) {
        board.increaseHeartCount();
        entityManager.flush();
    }

    @Retryable(
            retryFor = OptimisticLockException.class,
            maxAttempts = CommonConstants.OPTIMISTIC_ATTEMPT_COUNT,
            backoff = @Backoff(delay = 100)
    )
    private void decreaseHeartCount(Board board) {
        board.decreaseHeartCount();
        entityManager.flush();
    }
}
