package star.common.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import star.common.exception.client.AlreadyHeartedException;
import star.home.board.exception.AlreadyCanceledHeartException;
import star.member.model.entity.Member;

public abstract class BaseHeartService<H, T> extends BaseRetryRecoverService {

    protected final JpaRepository<H, Long> heartRepository;

    @PersistenceContext
    protected EntityManager entityManager;

    protected BaseHeartService(JpaRepository<H, Long> heartRepository) {
        this.heartRepository = heartRepository;
    }

    protected abstract boolean existsByMemberIdAndTargetId(Long memberId, Long targetId);

    protected abstract void deleteByMemberIdAndTargetId(Long memberId, Long targetId);

    protected abstract H createHeartEntity(Member member, T target);

    protected abstract void increaseHeartCount(T target);

    protected abstract void decreaseHeartCount(T target);

    protected abstract Page<T> getForeignEntitiesOfTargetByMemberId(Long memberId, Pageable pageable);

    @Transactional
    public void createHeart(Member member, T target, Long targetId) {
        if (existsByMemberIdAndTargetId(member.getId(), targetId)) {
            throw new AlreadyHeartedException();
        }

        H heart = createHeartEntity(member, target);
        increaseHeartCount(target);
        heartRepository.save(heart);
    }

    @Transactional
    public void deleteHeart(Member member, T target, Long targetId) {
        if (!existsByMemberIdAndTargetId(member.getId(), targetId)) {
            throw new AlreadyCanceledHeartException();
        }

        decreaseHeartCount(target);
        deleteByMemberIdAndTargetId(member.getId(), targetId);
    }

    @Transactional(readOnly = true)
    public boolean hasHearted(Long memberId, Long targetId) {
        return existsByMemberIdAndTargetId(memberId, targetId);
    }
}

