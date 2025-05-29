package star.home.board.service;


import static star.common.constants.CommonConstants.OPTIMISTIC_ATTEMPT_COUNT;

import jakarta.persistence.OptimisticLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.common.service.BaseHeartService;
import star.home.board.model.entity.Board;
import star.home.board.model.entity.BoardHeart;
import star.home.board.repository.BoardHeartRepository;
import star.member.model.entity.Member;

@Service
public class BoardHeartService extends BaseHeartService<BoardHeart, Board> {

    private final BoardHeartRepository boardHeartRepository;

    public BoardHeartService(BoardHeartRepository boardHeartRepository) {
        super(boardHeartRepository);
        this.boardHeartRepository = boardHeartRepository;
    }

    @Override
    protected boolean existsByMemberIdAndTargetId(Long memberId, Long boardId) {
        return boardHeartRepository.existsByMemberIdAndBoardId(memberId, boardId);
    }

    @Override
    protected void deleteByMemberIdAndTargetId(Long memberId, Long boardId) {
        boardHeartRepository.deleteHeartByMemberIdAndBoardId(memberId, boardId);
    }

    @Override
    protected BoardHeart createHeartEntity(Member member, Board board) {
        return BoardHeart.builder().member(member).board(board).build();
    }

    @Override
    @Retryable(retryFor = OptimisticLockException.class, maxAttempts = OPTIMISTIC_ATTEMPT_COUNT, backoff = @Backoff(delay = 100))
    protected void increaseHeartCount(Board board) {
        board.increaseHeartCount();
        entityManager.flush();
    }

    @Override
    @Retryable(retryFor = OptimisticLockException.class, maxAttempts = OPTIMISTIC_ATTEMPT_COUNT, backoff = @Backoff(delay = 100))
    protected void decreaseHeartCount(Board board) {
        board.decreaseHeartCount();
        entityManager.flush();
    }

    @Transactional
    public void deleteHeartsByBoardDelete(Long boardId) {
        boardHeartRepository.deleteHeartsByBoardId(boardId);
    }
}
