package star.common.service;

import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Recover;
import star.common.constants.CommonConstants;

@Slf4j
public abstract class BaseRetryRecoverService {

    @Recover
    protected void recoverOptimisticLockException(OptimisticLockException ex, Object... args) {
        log.warn("{}회가 넘는 Optimistic Lock Exception이 발생하였지만 무시 args={}, message={}",
                CommonConstants.OPTIMISTIC_ATTEMPT_COUNT, args, ex.getMessage());
    }
}
